package longbridge.controllers;

import com.sun.javafx.binding.StringFormatter;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.services.SecurityService;
import longbridge.utils.HostMaster;
import net.minidev.json.JSONObject;
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
import javax.servlet.http.HttpSession;
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
        HostMaster hostMaster;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/otp/authenticate")
    public String authenticate(HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale) {

        String redirectUrl = "";
        String otpUrl="";
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
            boolean result = securityService.performOtpValidation(username, otp);
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
        if (request.getSession().getAttribute("username") != null) {
            username = (String) request.getSession().getAttribute("username");
        }
        logger.info("The username {}",username);
        if(!username.equalsIgnoreCase("")) {
            boolean sendOtp;
            try {
                if (securityService.sendOtp(username)) sendOtp = true;
                else sendOtp = false;
                logger.info("otp sent {}",sendOtp);
                if (sendOtp){
                    stringBuilder.append("sucess");
                }else{
                    stringBuilder.append("failed");
                }
            } catch (InternetBankingSecurityException e) {
                stringBuilder.append("failed");
                logger.info(e.getMessage());
            } catch (NoSuchMessageException e) {
//                e.printStackTrace();
                stringBuilder.append("failed");
                logger.info(e.getMessage());

            }
        }else {
            stringBuilder.append("failed");
        }
        return stringBuilder.toString();
    }
}
