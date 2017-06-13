package longbridge.controllers.retail;

import longbridge.dtos.ServiceRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.CustSyncTokenForm;
import longbridge.forms.TokenProp;
import longbridge.services.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 28/05/2017.
 */
@Controller
@RequestMapping("/retail/token")
public class TokenManagementController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;
    private Locale locale;
    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String getRetailToken( HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().setAttribute("2FA", "2FA");
        return "/cust/logintoken";
    }

    @PostMapping
    public String performTokenAuthentication(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, Locale locale, Model model){

        String username = principal.getName();
        String tokenCode = request.getParameter("token");
        try{
            boolean result = securityService.performTokenValidation(username,tokenCode);
            if(result){
                if( request.getSession().getAttribute("2FA") !=null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message",messageSource.getMessage("token.auth.success",null,locale)) ;
                return "redirect:/retail/dashboard";
            }
        }
        catch (InternetBankingSecurityException ibe){
            logger.error("Error authenticating token");
            model.addAttribute("failure",messageSource.getMessage("token.auth.failure",null,locale));
        }
         model.addAttribute("failure",messageSource.getMessage("token.auth.failure",null,locale));
        return "/cust/logintoken";

    }

    @GetMapping("/sync")
    public String syncToken(Principal principal, Model model){
        try {
            String serials = securityService.getTokenSerials(principal.getName());

            logger.info("Serial received :"+serials);
            if (serials != null && !"".equals(serials)) {
                List<String> serialNos = Arrays.asList(StringUtils.split(serials, ","));
                model.addAttribute("serials", serialNos);
            }
        }
        catch (InternetBankingSecurityException ibe){
            logger.error("Failed to load corp user {} token serials", principal.getName(),ibe);
            model.addAttribute("failure", "Failed to load token serial numbers");
        }

        model.addAttribute("tokenSync", new CustSyncTokenForm());
        return "cust/token/sync";    }

    @PostMapping("/sync")
    public String synchroniseToken(@ModelAttribute("tokenSync") @Valid CustSyncTokenForm custSyncTokenForm, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "cust/token/sync";
        }

        try{
            boolean res = securityService.synchronizeToken(principal.getName(), custSyncTokenForm.getSerialNo(), custSyncTokenForm.getTokenCode1(), custSyncTokenForm.getTokenCode2());
            if (res){
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.sync.success", null, locale));
                return "redirect:/retail/token/sync";
            }
        }catch (InternetBankingSecurityException ibe){
            logger.error("Error Synchronizing Token", ibe);

        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.sync.failure", null, locale));
        return "redirect:/retail/token/sync";

    }


    @GetMapping("/lost")
    public String lostToken(Principal principal, Model model){

        try {
            String serials = securityService.getTokenSerials(principal.getName());
            if (serials != null && !"".equals(serials)) {
                List<String> serialNos = Arrays.asList(StringUtils.split(serials, ","));
                model.addAttribute("serials", serialNos);
            }
        }
        catch (InternetBankingSecurityException ibe){
            logger.error("Failed to load corp user {} token serials", principal.getName(),ibe);
            model.addAttribute("failure", "Failed to load token serial numbers");
        }

        model.addAttribute("tokenProp", new TokenProp());
        return "cust/token/lost";
    }


    @PostMapping("/lost")
    public String performDeactivateToken(@ModelAttribute("token") @Valid TokenProp tokenProp, BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/cust/token/lost";
        }
        try {
            boolean result = securityService.deActivateToken(principal.getName(), tokenProp.getSerialNo());
            if (result) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.deactivate.success", null, locale));
                return "redirect:/retail/token/lost";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error deactivating token", ibe);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.deactivate.failure", null, locale));
        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.deactivate.failure", null, locale));
        return "redirect:/retail/token/lost";
    }

    @GetMapping("/authenticate")
    public String getAuthPage(HttpSession session, Principal principal){
        return "/cust/tokenauth";
    }

    @PostMapping("/authenticate")
    public String performAuthenticate(WebRequest webRequest, HttpSession session, Principal principal, Model model, RedirectAttributes redirectAttributes){
        String url;
        if (webRequest.getParameter("token") == null){
            redirectAttributes.addFlashAttribute("failure", "Enter authentication code");
            return "/cust/tokenauth";
        }
        if (webRequest.getParameter("token") != null && session.getAttribute("redirectURL") != null && session.getAttribute("requestDTO") != null){
            String token = webRequest.getParameter("token");
            url = (String) session.getAttribute("redirectURL");

            try {
                boolean result = securityService.performTokenValidation(principal.getName(), token);
                if (!result){
                    model.addAttribute("failure", "Token Authentication Failed");
                    return "/cust/tokenauth";

                }
            }catch(InternetBankingSecurityException ibe){
                logger.error("Error authenticating token", ibe);
                model.addAttribute("failure", "Token Authentication Failed");
                return "/cust/tokenauth";


            }

        }else {
            redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
            return "redirect:/token/authenticate";
        }
        session.setAttribute("authenticated","authenticated");

        return "redirect:"+url;
    }

}
