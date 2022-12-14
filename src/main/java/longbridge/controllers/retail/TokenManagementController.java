package longbridge.controllers.retail;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.CustSyncTokenForm;
import longbridge.forms.TokenProp;
import longbridge.models.RetailUser;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/retail/token")
public class TokenManagementController {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecurityService securityService;
    private final Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RetailUserService retailUserService;

    @GetMapping
    public String getRetailToken( HttpServletRequest httpServletRequest, Principal principal, Model model){
        Integer noOfAttempts = 0;
        httpServletRequest.getSession().setAttribute("2FA", "2FA");
        String name = principal == null ? "" : principal.getName();
        model.addAttribute("username",name);
        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user.getNoOfTokenAttempts() != null){
            noOfAttempts = user.getNoOfTokenAttempts();
        }
        model.addAttribute("noOfAttempts",noOfAttempts);
        logger.debug("Loading the token page with number of attempts {} for user {}" ,noOfAttempts,name);
        return "/cust/logintoken";
    }

    @PostMapping
    public String performTokenAuthentication(HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes, Locale locale,Model model){

        String tokenCode = request.getParameter("token");
        Integer noOfAttempts = 0;
        RetailUser user = retailUserService.getUserByName(principal.getName());
        try{
            boolean result = securityService.performTokenValidation(user.getEntrustId(),user.getEntrustGroup(),tokenCode);
            if(result){
                if(request.getSession().getAttribute("2FA") !=null) {
                    request.getSession().removeAttribute("2FA");
                }
                retailUserService.resetNoOfTokenAttempt(user);

                CustomUserPrincipal user1 = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                user1.setSfactorAuthIndicator("Y");//added by GB for antifraudData

                logger.debug("Token authentication successful for user {}",user.getUserName());
                return "redirect:/retail/dashboard";
            }

        } catch (Exception ibe){
            logger.error("Error authenticating token",ibe);
            redirectAttributes.addFlashAttribute("failure",ibe.getMessage());

        }
        if(user != null){
            retailUserService.increaseNoOfTokenAttempt(user);
            noOfAttempts = user.getNoOfTokenAttempts();
            logger.info("no of attempts {}",noOfAttempts);
        }
        model.addAttribute("noOfAttempts",noOfAttempts);
        return "redirect:/retail/token";

    }

    @GetMapping("/sync")
    public String syncToken(Principal principal, Model model){

        try {
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String serials = securityService.getTokenSerials(user.getEntrustId(), user.getEntrustGroup());

            logger.info("Serial received :"+serials);
            if (serials != null && !"".equals(serials)) {
                String serialNums = StringUtils.trim(serials);
                List<String> serialNos = Arrays.asList(StringUtils.split(serialNums, ","));
                model.addAttribute("serials", serialNos);
            }
        }
        catch (InternetBankingSecurityException ibe){
            logger.error("Failed to load retail user {} token serials", principal.getName(),ibe);
            model.addAttribute("failure", ibe.getMessage());
        }

        model.addAttribute("tokenSync", new CustSyncTokenForm());
        return "cust/token/sync";    }

    @PostMapping("/sync")
    public String synchroniseToken(@ModelAttribute("tokenSync") @Valid CustSyncTokenForm custSyncTokenForm, BindingResult result, Principal principal, RedirectAttributes redirectAttributes){

        if (result.hasErrors()){

            return "cust/token/sync";
        }

        try{
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            boolean res = securityService.synchronizeToken(retailUser.getEntrustId(), retailUser.getEntrustGroup(), custSyncTokenForm.getSerialNo(), custSyncTokenForm.getTokenCode1(), custSyncTokenForm.getTokenCode2());
            if (res) {

                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.sync.success", null, locale));
            }else{

                redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.sync.failure", null, locale));
            }
            if (res){
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("token.sync.success", null, locale));
                return "redirect:/retail/token/sync";
            }
        }catch (InternetBankingSecurityException ibe){
            logger.error("Error Synchronizing Token", ibe);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.sync.failure", null, locale));
        }
        return "redirect:/retail/token/sync";

    }


    @GetMapping("/lost")
    public String lostToken(Principal principal, Model model){

        try {
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            String serials = securityService.getTokenSerials(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            if (serials != null && !"".equals(serials)) {
                String serialNums = StringUtils.trim(serials);
                List<String> serialNos = Arrays.asList(StringUtils.split(serialNums, ","));
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
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            boolean result = securityService.deActivateToken(retailUser.getEntrustId(), retailUser.getEntrustGroup(), tokenProp.getSerialNo());
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
    public String performAuthenticate(WebRequest webRequest, HttpSession session, Principal principal, RedirectAttributes redirectAttributes){
        String url;
        if (webRequest.getParameter("token") == null){
            redirectAttributes.addFlashAttribute("failure", "Enter authentication code");
            return "/cust/tokenauth";
        }
        if (webRequest.getParameter("token") != null && session.getAttribute("redirectURL") != null && session.getAttribute("requestDTO") != null){
            String token = webRequest.getParameter("token");
            url = (String) session.getAttribute("redirectURL");

            try {
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (!result){
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                    return "redirect:/retail/token/authenticate";
                }
            }catch(InternetBankingSecurityException ibe){
                logger.error("Error authenticating token", ibe);
                redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                return "redirect:/retail/token/authenticate";

            }

        }else {
            redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
            return "redirect:/retail/token/authenticate";
        }
        session.setAttribute("authenticated","authenticated");

        return "redirect:"+url;
    }



}
