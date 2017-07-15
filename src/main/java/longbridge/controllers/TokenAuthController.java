package longbridge.controllers;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
import longbridge.utils.HostMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Fortune on 5/26/2017.
 */
@Controller
@RequestMapping
public class TokenAuthController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    RetailUserService retailUserService;

@Autowired
        HostMaster hostMaster;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/otp/authenticate")
    public String authenticate(HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale, Principal principal) {
        String redirectUrl = "";
        String otpUrl="";
        logger.info("the otp sent is {}",request.getParameter("otp"));
        String username = "";
        if (request.getSession().getAttribute("redirectUrl") != null) {
            redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
        }
        if (request.getSession().getAttribute("otpUrl") != null) {
            otpUrl = (String) request.getSession().getAttribute("otpUrl");
        }

        if (request.getSession().getAttribute("username") != null) {
            username = (String) request.getSession().getAttribute("username");
        }
            String otp = request.getParameter("otp");
            if(otp==null||"".equals(otp)){
                redirectAttributes.addFlashAttribute("failure",messageSource.getMessage("otp.required",null,locale));
                return "redirect:/"+otpUrl;
            }

        try {
            RetailUser user  = retailUserService.getUserByName(principal.getName());
            boolean result = securityService.performOtpValidation(user.getEntrustId(), user.getEntrustGroup(), otp);
            if (result) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.auth.success", null, locale));
                return "redirect:/" + redirectUrl;
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error authenticating token");
        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
        return "redirect:" + redirectUrl;
    }
    @GetMapping("/otp/send")
    @ResponseBody
    public String sendOTP(Principal principal, RedirectAttributes redirectAttributes,Locale locale,HttpServletRequest request){
        String username = "";
        String message = "";
        StringBuilder stringBuilder = new StringBuilder(message);
//        if (request.getSession().getAttribute("username") != null) {
//            username = (String) request.getSession().getAttribute("username");
//        }
//        if(principal.getName() != null){
//            username=principal.getName();
//        }
        logger.info("The username {}",username);
        if(!username.equalsIgnoreCase("")) {
            boolean sendOtp;
            try {
                RetailUser user  = retailUserService.getUserByName(principal.getName());
                if (securityService.sendOtp(user.getEntrustId(), user.getEntrustGroup()))
                    sendOtp = true;
                else sendOtp = false;
                logger.info("otp sent {}",sendOtp);
                if (sendOtp){
                    stringBuilder.append("success");
                }
            } catch (InternetBankingSecurityException e) {
                logger.info(e.getMessage());
            } catch (NoSuchMessageException e) {
//                e.printStackTrace();
                logger.info(e.getMessage());

            }
        }else {
            stringBuilder.append("empty");
        }

        if(stringBuilder.toString().equalsIgnoreCase("")){
            stringBuilder.append("failed");
        }
        return stringBuilder.toString();
    }
}
