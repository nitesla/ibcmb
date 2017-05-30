package longbridge.controllers;

import com.sun.javafx.binding.StringFormatter;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Created by Fortune on 5/26/2017.
 */
@Controller
@RequestMapping("/token")
public class TokenAuthController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MessageSource messageSource;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/authenticate")
    public String authenticate(HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale) {

        String redirectUrl = "";
        String username = "";
        String otp = "";

        if (request.getSession().getAttribute("url") != null) {
            redirectUrl = (String) request.getSession().getAttribute("url");
        }

        if (request.getSession().getAttribute("username") != null) {
            username = (String) request.getSession().getAttribute("username");
        }
        if (request.getSession().getAttribute("token") != null) {
            otp = (String) request.getSession().getAttribute("token");
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
        return "redirect:/" + redirectUrl;
    }


    @GetMapping("/ops")
    public String getOpsToken() {
        return "ops/token";
    }
}
