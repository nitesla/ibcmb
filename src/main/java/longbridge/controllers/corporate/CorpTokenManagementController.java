package longbridge.controllers.corporate;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.TokenProp;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Wunmi on 28/05/2017.
 */
@Controller
@RequestMapping("/corporate/token")
public class CorpTokenManagementController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;
    private Locale locale;
    @Autowired
    private MessageSource messageSource;


    @GetMapping
    public String getRetailToken(){
        return "/corp/logintoken";
    }

    @PostMapping
    public String performTokenAuthentication(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, Locale locale){

        String username = principal.getName();
        String tokenCode = request.getParameter("token");
        try{
            boolean result = securityService.performTokenValidation(username,tokenCode);
            if(result){
                if( request.getSession().getAttribute("2FA") !=null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message",messageSource.getMessage("token.auth.success",null,locale)) ;
                return "redirect:/corporate/dashboard";
            }
        }
        catch (InternetBankingSecurityException ibe){
            logger.error("Error authenticating token");
        }
        redirectAttributes.addFlashAttribute("failure",messageSource.getMessage("token.auth.failure",null,locale));
        return "redirect:/corporate/token";

    }


    @GetMapping("/sync")
    public String syncToken(TokenProp tokenProp, Principal principal, Model model){
        return "corp/token/sync";
    }

    @PostMapping("/sync")
    public String synchroniseToken(@Valid TokenProp tokenProp, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "corp/token/sync";
        }

       // securityService.synchronizeToken(principal.getName());
        return "redirect:/corporate/token/sync";
    }


    @GetMapping("/lost")
    public String lostToken(TokenProp tokenProp, Principal principal, Model model){
        return "corp/token/lost";
    }

    @PostMapping("/lost")
    public String blockToken(@Valid TokenProp tokenProp, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "corp/token/lost";
        }

       // securityService.synchronizeToken(principal.getName());
        return "redirect:/corporate/token/lost";
    }

}
