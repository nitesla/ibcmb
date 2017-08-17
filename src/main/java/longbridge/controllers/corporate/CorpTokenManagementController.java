package longbridge.controllers.corporate;

import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.CustSyncTokenForm;
import longbridge.forms.TokenProp;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CorporateUserService corporateUserService;


    @GetMapping
    public String getCorporateToken(HttpServletRequest httpServletRequest, Principal principal, Model model) {
        Integer noOfAttempts = 0;
        httpServletRequest.getSession().setAttribute("2FA", "2FA");
        model.addAttribute("username", principal.getName());
        CorporateUser user  = corporateUserService.getUserByName(principal.getName());
        if (user.getNoOfTokenAttempts() != null){
            noOfAttempts = user.getNoOfTokenAttempts();
        }
        model.addAttribute("noOfAttempts",noOfAttempts);
        return "/corp/logintoken";
    }

    @PostMapping
    public String performTokenAuthentication(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, Locale locale,Model model) {
        Integer noOfAttempts = 0;
        CorporateUser user  = corporateUserService.getUserByName(principal.getName());
        String tokenCode = request.getParameter("token");
        try {
            boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
            if (result) {
                if (request.getSession().getAttribute("2FA") != null) {
                    request.getSession().removeAttribute("2FA");
                }

                if ("Y".equals(user.getIsFirstTimeLogon())){
                    return "redirect:/corporate/setup";
                }
                corporateUserService.resetNoOfTokenAttempt(user);
                //redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.auth.success", null, locale));
                return "redirect:/corporate/dashboard";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error authenticating token {} ", ibe);
        }
        if (user.getNoOfTokenAttempts() != null){
            corporateUserService.increaseNoOfTokenAttempt(user);
            noOfAttempts = user.getNoOfTokenAttempts();
        }
        model.addAttribute("noOfAttempts",noOfAttempts);
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
        custSyncTokenForm, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
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
        bindingResult, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
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


    @GetMapping("/authenticate")
    public String getAuthPage(HttpSession session, Principal principal){
        return "/corp/tokenauth";
    }


    @PostMapping("/authenticate")
    public String performAuthenticate(WebRequest webRequest, HttpSession session, Principal principal, RedirectAttributes redirectAttributes){
        String url;
        if (webRequest.getParameter("token") == null){
            redirectAttributes.addFlashAttribute("failure", "Enter authentication code");
            return "/corp/tokenauth";
        }
        if (webRequest.getParameter("token") != null && session.getAttribute("redirectURL") != null && session.getAttribute("requestDTO") != null){
            String token = webRequest.getParameter("token");
            url = (String) session.getAttribute("redirectURL");

            try {
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
                if (!result){
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                    return "redirect:/corporate/token/authenticate";
                }
            }catch(InternetBankingSecurityException ibe){
                logger.error("Error authenticating token", ibe);
                redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                return "redirect:/corporate/token/authenticate";

            }

        }else {
            redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
            return "redirect:/retail/token/authenticate";
        }
        session.setAttribute("authenticated","authenticated");

        return "redirect:"+url;
    }


}
