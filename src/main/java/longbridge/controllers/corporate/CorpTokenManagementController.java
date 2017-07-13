package longbridge.controllers.corporate;

import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.CustSyncTokenForm;
import longbridge.forms.TokenProp;
import longbridge.models.CorporateUser;
import longbridge.services.CorporateUserService;
import longbridge.services.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 28/05/2017.
 */
@Controller
@RequestMapping("/corporate/token")
public class CorpTokenManagementController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;
    private Locale locale;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CorporateUserService corporateUserService;


    @GetMapping
    public String getCorporateToken(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().setAttribute("2FA", "2FA");

        return "/corp/logintoken";
    }

    @PostMapping
    public String performTokenAuthentication(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {

        CorporateUser user  = corporateUserService.getUserByName(principal.getName());

        String tokenCode = request.getParameter("token");
        try {
            boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
            if (result) {
                if (request.getSession().getAttribute("2FA") != null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.auth.success", null, locale));
                return "redirect:/corporate/dashboard";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error authenticating token {} ", ibe);
        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
        return "redirect:/corporate/token";

    }


    @GetMapping("/sync")
    public String syncToken(Model model, Principal principal) {
        CorporateUser user  = corporateUserService.getUserByName(principal.getName());
        try {
            String serials = securityService.getTokenSerials(user.getEntrustId(), user.getEntrustGroup());
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
            return "corp/token/sync";
        }

        @PostMapping("/sync")
        public String synchroniseToken (@ModelAttribute("tokenSync") @Valid CustSyncTokenForm
        custSyncTokenForm, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){
            if (result.hasErrors()) {
                return "corp/token/sync";
            }
            CorporateUser user  = corporateUserService.getUserByName(principal.getName());

            try {
                boolean res = securityService.synchronizeToken(user.getEntrustId(), user.getEntrustGroup(), custSyncTokenForm.getSerialNo(), custSyncTokenForm.getTokenCode1(), custSyncTokenForm.getTokenCode2());
                if (res) {
                    redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.sync.success", null, locale));
                }else{
                	redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.sync.failure", null, locale));
                } 
                
            } catch (InternetBankingSecurityException ibe) {
                logger.error("Error Synchronizing Token", ibe);
                redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.sync.failure", null, locale));
            }
           
            return "redirect:/corporate/token/sync";
        }


        @GetMapping("/lost")
        public String lostToken (Model model, Principal principal){

            try {
                CorporateUser user  = corporateUserService.getUserByName(principal.getName());
                String serials = securityService.getTokenSerials(user.getEntrustId(), user.getEntrustGroup());
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
            return "corp/token/lost";
        }

        @PostMapping("/lost")
        public String blockToken (@ModelAttribute("tokenProp") @Valid TokenProp tokenProp, BindingResult
        bindingResult, Principal principal, RedirectAttributes redirectAttributes){
            if (bindingResult.hasErrors()) {
                return "corp/token/lost";
            }
            CorporateUser user  = corporateUserService.getUserByName(principal.getName());
            try {
                boolean result = securityService.deActivateToken(user.getEntrustId(), user.getEntrustGroup(), tokenProp.getSerialNo());
                if (result) {
                    redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.deactivate.success", null, locale));
                    return "redirect:/corporate/token/lost";
                }
            } catch (InternetBankingException ibe) {
                logger.error("Error deactivating token", ibe);
            }
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.deactivate.failure", null, locale));
            return "redirect:/corporate/token/lost";
        }

    }
