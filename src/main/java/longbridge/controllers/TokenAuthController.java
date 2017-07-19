package longbridge.controllers;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
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
    private  RetailUserService retailUserService;

    @Autowired
    private CorporateUserService corporateUserService;

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
        if (request.getParameter("username") != null) {
            username = request.getParameter("username");
        }
        logger.info("The username {}",username);
        if(!username.equalsIgnoreCase("")) {
            boolean sendOtp;
            try {
                RetailUser user  = retailUserService.getUserByName(username);
                logger.info("the user is {}",user);
                if (securityService.sendOtp(user.getEntrustId(),user.getEntrustGroup())) sendOtp = true;
                else sendOtp = false;
                logger.info("otp sent {}",sendOtp);
                if (sendOtp){
                    stringBuilder.append("success");
                }
            } catch (InternetBankingSecurityException e) {
                logger.info(e.getMessage());
                if(e.getMessage() !=null) {
                    stringBuilder.append(e.getMessage());
                }else {
                    stringBuilder.append("Service not available");
                }
            } catch (NoSuchMessageException e) {
//                e.printStackTrace();
                logger.info(e.getMessage());
                stringBuilder.append(e.getMessage());

            }catch (Exception e){
                e.printStackTrace();
                logger.info(e.getMessage());
                stringBuilder.append(e.getMessage());
            }
        }else {
            stringBuilder.append("empty");
        }

//        if(stringBuilder.toString().equalsIgnoreCase("")){
//            stringBuilder.append("failed");
//        }
        return stringBuilder.toString();
    }
    @GetMapping("/otp/send/corporate")
    @ResponseBody
    public String sendOTPForCorporate(Principal principal, RedirectAttributes redirectAttributes,Locale locale,HttpServletRequest request){
        String username = "";
        String message = "";
        StringBuilder stringBuilder = new StringBuilder(message);
        if (request.getParameter("username") != null) {
            username = request.getParameter("username");
        }
        logger.info("The username {}",username);
        if(!username.equalsIgnoreCase("")) {
            boolean sendOtp;
            try {
                CorporateUser user  = corporateUserService.getUserByName(username);
                if (securityService.sendOtp(user.getEntrustId(),user.getEntrustGroup())) sendOtp = true;
                else sendOtp = false;
                logger.info("otp sent {}",sendOtp);
                if (sendOtp){
                    stringBuilder.append("success");
                }
            } catch (InternetBankingSecurityException e) {
                logger.info(e.getMessage());
                if(e.getMessage() !=null) {
                    stringBuilder.append(e.getMessage());
                }else {
                    stringBuilder.append("Service not available");
                }

            } catch (NoSuchMessageException e) {
//                e.printStackTrace();
                logger.info(e.getMessage());
                stringBuilder.append(e.getMessage());

            }catch (Exception e){
                e.printStackTrace();
                logger.info(e.getMessage());
                stringBuilder.append(e.getMessage());
            }
        }else {
            stringBuilder.append("empty");
        }

//        if(stringBuilder.toString().equalsIgnoreCase("")){
//            stringBuilder.append("failed");
//        }
        return stringBuilder.toString();
    }
    @PostMapping("/otp/retail/login")
    public String authenticateOTPforRetails(HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale,HttpSession session) {
        String redirectUrl = "";
        String otpUrl = "";
        String otp=request.getParameter("otp");
        String username = request.getParameter("username");
        logger.info("the otp {} username{} redirect {}",otp,username,redirectUrl);
        if (request.getSession().getAttribute("otpUrl") != null) {
            otpUrl = (String) request.getSession().getAttribute("otpUrl");
        }
        try {
            RetailUser user  = retailUserService.getUserByName(username);
            boolean result = securityService.performOtpValidation(user.getEntrustId(), user.getEntrustGroup(), otp);
            logger.info("the result is {}",result);
            if (result) {
                if( request.getSession().getAttribute("2FA") !=null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("otp.auth.success", null, locale));
                return "redirect:/retail/dashboard";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error authenticating token {}",ibe);
        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("otp.auth.failure", null, locale));
        return "redirect:/retail/token";
    }
    @PostMapping("/otp/corporate/login")
    public String authenticateOTPForCorp(HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale,HttpSession session) {
        String redirectUrl = "";
        String otpUrl = "";
        String otp=request.getParameter("otp");
        String username = request.getParameter("username");
        logger.info("the otp {} username{} redirect {}",otp,username,redirectUrl);
        if (request.getSession().getAttribute("otpUrl") != null) {
            otpUrl = (String) request.getSession().getAttribute("otpUrl");
        }
        try {
            CorporateUser user  = corporateUserService.getUserByName(username);
            boolean result = securityService.performOtpValidation(user.getEntrustId(), user.getEntrustGroup(), otp);
            logger.info("the result is {}",result);
            if (result) {
                if( request.getSession().getAttribute("2FA") !=null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("otp.auth.success", null, locale));
                return "redirect:/corporate/dashboard";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error authenticating token {}",ibe);
        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("otp.auth.failure", null, locale));
        return "redirect:/corporate/token";
    }
}
