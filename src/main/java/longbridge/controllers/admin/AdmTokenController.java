package longbridge.controllers.admin;

import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.SyncTokenForm;
import longbridge.forms.TokenForm;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 5/28/2017.
 */

@Controller
@RequestMapping("/admin/token")
public class AdmTokenController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SecurityService securityService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @GetMapping
    public String getAdminToken(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().setAttribute("2FA","2FA");

        return "adm/admin/token";
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
                return "redirect:/admin/dashboard";
            }
        }
        catch (InternetBankingSecurityException ibe){
            logger.error("Error authenticating token");
        }
        redirectAttributes.addFlashAttribute("failure",messageSource.getMessage("token.auth.failure",null,locale));
        return "redirect:/admin/token";

    }

    @GetMapping("/assign")
    public String assignToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/assign";
    }

    @PostMapping("/assign")
    public String performAssignToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/assign";
        }

        try {
            boolean result = securityService.assignToken(tokenForm.getUsername(), tokenForm.getSerialNumber());
            if (result) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.assign.success", null, locale));
                return "redirect:/admin/token/assign";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error assigning token", ibe);
        }
        bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("token.assign.failure", null, locale)));
        return "/adm/token/assign";
    }

    @GetMapping("/activate")
    public String activateToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/activate";
    }

    @PostMapping("/activate")
    public String performActivateToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/activate";
        }

        try {
            boolean result = securityService.activateToken(tokenForm.getUsername(), tokenForm.getSerialNumber());
            if (result) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.activate.success", null, locale));
                return "redirect:/admin/token/activate";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error activating token", ibe);
        }
        bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("token.activate.failure", null, locale)));
        return "/adm/token/activate";
    }

    @GetMapping("/deactivate")
    public String deactivateToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/deactivate";

    }

    @PostMapping("/deactivate")
    public String performDeactivateToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/deactivate";
        }
        try {
            boolean result = securityService.deActivateToken(tokenForm.getUsername(), tokenForm.getSerialNumber());
            if (result) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.deactivate.success", null, locale));
                return "redirect:/admin/token/deactivate";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error deactivating token", ibe);
        }
        bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("token.deactivate.failure", null, locale)));
        return "/adm/token/deactivate";
    }

    @GetMapping("/synchronize")
    public String SyncToken(Model model) {
        model.addAttribute("token", new SyncTokenForm());
        return "/adm/token/synchronize";

    }

    @PostMapping("/synchronize")
    public String performSyncToken(@ModelAttribute("token") @Valid SyncTokenForm tokenForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/synchronize";
        }
        try {
            boolean result = securityService.synchronizeToken(tokenForm.getUsername(), tokenForm.getSerialNumber(), tokenForm.getTokenCode1(), tokenForm.getTokenCode2());
            if (result) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.sync.success", null, locale));
                return "redirect:/admin/token/synchronize";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error synchronizing token", ibe);
        }
        bindingResult.addError(new ObjectError("error", messageSource.getMessage("token.sync.failure", null, locale)));
        return "/adm/token/synchronize";
    }


    @GetMapping("/serials")
    public List<String> getTokenSerials(@RequestParam("username") String username){
        List<String > serials = new ArrayList<>();
        try {
           String serial = securityService.getTokenSerials(username);
            if(serial!=null && !"".equals(serial)){
               serials = Arrays.asList(StringUtils.split(serial,","));
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error getting token serials", ibe);
        }
        return serials;
    }
}
