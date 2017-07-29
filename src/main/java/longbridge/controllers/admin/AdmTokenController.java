package longbridge.controllers.admin;

import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.SyncTokenForm;
import longbridge.forms.TokenForm;
import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.services.*;
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

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private OperationsUserService operationsUserService;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private ConfigurationService configService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("userTypes", UserType.getUseryTypes());
    }

    @GetMapping
    public String getAdminToken(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().setAttribute("2FA", "2FA");

        return "adm/admin/token";
    }

    @PostMapping
    public String performTokenAuthentication(HttpServletRequest request, Principal principal,
                                             RedirectAttributes redirectAttributes, Locale locale) {

        AdminUser user = adminUserService.getUserByName(principal.getName());

        String tokenCode = request.getParameter("token");
        try {
            boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
            if (result) {
                if (request.getSession().getAttribute("2FA") != null) {
                    request.getSession().removeAttribute("2FA");
                }
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.auth.success", null, locale));
                return "redirect:/admin/dashboard";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error authenticating token", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/admin/token";

    }

    @GetMapping("/assign")
    public String assignToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/assign";
    }

    @PostMapping("/assign")
    public String performAssignToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(
                    new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/assign";
        }

        try {
            String username = tokenForm.getUsername();
            String group = getUserGroup(tokenForm.getUserType());
            String entrustId = getEntrustId(username, tokenForm.getUserType());

            boolean result = securityService.assignToken(entrustId, group, tokenForm.getSerialNumber());
            if (result) {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.assign.success", null, locale));
                return "redirect:/admin/token/assign";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error assigning token", ibe);
        }
        bindingResult
                .addError(new ObjectError("invalid", messageSource.getMessage("token.assign.failure", null, locale)));
        return "/adm/token/assign";
    }

    @GetMapping("/activate/username")
    public String getTokenUsernameForActivation() {
        return "/adm/token/activate1";
    }

    @PostMapping("/activate/username")
    public String getTokenSerialsForActivation(@RequestParam("username") String username, @RequestParam("userType") String userType, Model model, Locale locale,
                                               RedirectAttributes redirectAttributes) {
        if (username == null || "".equals(username)) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/adm/token/activate1";
        }

        try {
            String group = getUserGroup(userType);
            String entrustId = getEntrustId(username, userType);

            String serials = securityService.getTokenSerials(entrustId, group);
            logger.info("Serials recieved are " + serials);
            if (serials != null && !"".equals(serials)) {
                String serialNums = StringUtils.trim(serials);
                List<String> serialNos = Arrays.asList(StringUtils.split(serialNums, ","));
                model.addAttribute("serials", serialNos);
                TokenForm tokenForm = new TokenForm();
                tokenForm.setUsername(username);
                model.addAttribute("token", tokenForm);
            } else {
                redirectAttributes.addFlashAttribute("failure",
                        messageSource.getMessage("token.serials.failure", null, locale));
                return "redirect:/admin/token/activate/username";
            }
        } catch (InternetBankingSecurityException se) {
            logger.error("Error getting token serials", se);
            redirectAttributes.addFlashAttribute("failure",
                    messageSource.getMessage("token.serials.failure", null, locale));
            return "redirect:/admin/token/activate/username";
        }
        return "/adm/token/activate2";
    }

    @PostMapping("/activate")
    public String performActivateToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(
                    new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/activate2";
        }

        try {
            String username = tokenForm.getUsername();
            String group = getUserGroup(tokenForm.getUserType());
            String entrustId = getEntrustId(username, tokenForm.getUserType());

            boolean result = securityService.activateToken(entrustId, group, tokenForm.getSerialNumber());
            if (result) {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.activate.success", null, locale));
                return "redirect:/admin/token/activate2";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error activating token", ibe);
        }
        bindingResult
                .addError(new ObjectError("invalid", messageSource.getMessage("token.activate.failure", null, locale)));
        return "/adm/token/activate2";
    }

    @GetMapping("/assign/activate")
    public String assignActivateToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/assignactivate";
    }

    @PostMapping("/assign/activate")
    public String assignActivateToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(
                    new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/assignactivate";
        }

        try {
            String username = tokenForm.getUsername();
            String group = getUserGroup(tokenForm.getUserType());
            String entrustId = getEntrustId(username, tokenForm.getUserType());

            boolean result1 = securityService.assignToken(entrustId, group, tokenForm.getSerialNumber());
            if (result1) {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.assign.success", null, locale));
                boolean result2 = securityService.activateToken(entrustId, group, tokenForm.getSerialNumber());
                if (result2) {
                    redirectAttributes.addFlashAttribute("message",
                            messageSource.getMessage("token.activate.success", null, locale));
                }
                return "redirect:/admin/token/assign/activate";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error assigning and activating token", ibe);
        }
        bindingResult
                .addError(new ObjectError("invalid", messageSource.getMessage("token.assign.failure", null, locale)));
        return "/adm/token/assignactivate";
    }

    @GetMapping("/deactivate/username")
    public String getTokenUsernameForDeactivation() {
        return "/adm/token/deactivate1";
    }

    @PostMapping("/deactivate/username")
    public String getTokenSerialsForDeactivation(@RequestParam("username") String username, @RequestParam("userType") String userType, Model model, Locale locale,
                                                 RedirectAttributes redirectAttributes) {
        if (username == null || "".equals(username)) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/adm/token/deactivate1";
        }

        try {
            String group = getUserGroup(userType);
            String entrustId = getEntrustId(username, userType);

            String serials = securityService.getTokenSerials(entrustId, group);
            if (serials != null && !"".equals(serials)) {
                String serialNums = StringUtils.trim(serials);
                List<String> serialNos = Arrays.asList(StringUtils.split(serialNums, ","));
                model.addAttribute("serials", serialNos);
                TokenForm tokenForm = new TokenForm();
                tokenForm.setUsername(username);
                model.addAttribute("token", tokenForm);
            } else {
                redirectAttributes.addFlashAttribute("failure",
                        messageSource.getMessage("token.serials.failure", null, locale));
                return "redirect:/admin/token/deactivate/username";
            }
        } catch (InternetBankingSecurityException se) {
            logger.error("Error getting token serials", se);
            redirectAttributes.addFlashAttribute("failure",
                    messageSource.getMessage("token.serials.failure", null, locale));
            return "redirect:/admin/token/deactivate/username";
        }
        return "/adm/token/deactivate2";
    }

    @GetMapping("/deactivate")
    public String deactivateToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/deactivate";

    }

    @PostMapping("/deactivate")
    public String performDeactivateToken(@ModelAttribute("token") @Valid TokenForm tokenForm,
                                         BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(
                    new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/deactivate2";
        }
        try {
            String username = tokenForm.getUsername();
            String group = getUserGroup(tokenForm.getUserType());
            String entrustId = getEntrustId(username, tokenForm.getUserType());

            boolean result = securityService.deActivateToken(entrustId, group, tokenForm.getSerialNumber());
            if (result) {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.deactivate.success", null, locale));
                return "redirect:/admin/token/deactivate2";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error deactivating token", ibe);
        }
        bindingResult.addError(
                new ObjectError("invalid", messageSource.getMessage("token.deactivate.failure", null, locale)));
        return "/adm/token/deactivate2";
    }

    @GetMapping("/unlock")
    public String unlockToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/unlock";

    }

    @PostMapping("/unlock")
    public String UnlockToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                              Locale locale, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/adm/token/unlock";
        }
        logger.info("the group is {}",getUserGroup(tokenForm.getUserType()));
        try {

            String username = tokenForm.getUsername();
            String group = getUserGroup(tokenForm.getUserType());
            String entrustId = getEntrustId(username, tokenForm.getUserType());

            boolean result = securityService.unLockUser(entrustId, group);
            if (result) {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.unlock.success", null, locale));
                return "redirect:/admin/token/unlock";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error unlocking token", ibe);
        }
        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.unlock.failure", null, locale));
        return "redirect:/admin/token/unlock";
    }

    @GetMapping("/synchronize")
    public String SyncToken(Model model) {
        model.addAttribute("token", new SyncTokenForm());
        return "/adm/token/synchronize";

    }

    @PostMapping("/synchronize")
    public String performSyncToken(@ModelAttribute("token") @Valid SyncTokenForm tokenForm, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(
                    new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/synchronize";
        }
        try {
            String username = tokenForm.getUsername();
            String group = getUserGroup(tokenForm.getUserType());
            String entrustId = getEntrustId(username, tokenForm.getUserType());

            boolean result = securityService.synchronizeToken(entrustId, group, tokenForm.getSerialNumber(),
                    tokenForm.getTokenCode1(), tokenForm.getTokenCode2());
            if (result) {
                redirectAttributes.addFlashAttribute("message",
                        messageSource.getMessage("token.sync.success", null, locale));
                return "redirect:/admin/token/synchronize";
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error synchronizing token", ibe);
        }
        bindingResult.addError(new ObjectError("error", messageSource.getMessage("token.sync.failure", null, locale)));
        return "/adm/token/synchronize";
    }

    @GetMapping("/serials")
    public List<String> getTokenSerials(@RequestParam("username") String username, @RequestParam("userType") String userType) {
        List<String> serials = new ArrayList<>();
        try {
            String group = getUserGroup(userType);
            String entrustId = getEntrustId(username, userType);

            String serial = securityService.getTokenSerials(entrustId, group);
            if (serials != null && !serials.isEmpty()) {
                String serialNums = StringUtils.trim(serial);
                serials = Arrays.asList(StringUtils.split(serialNums, ","));
            }
        } catch (InternetBankingSecurityException ibe) {
            logger.error("Error getting token serials", ibe);
        }
        return serials;
    }

    private String getUserGroup(String userType) {
        String group = "";

        if ("ADMIN".equals(userType)) {
            group = configService.getSettingByName("DEF_ENTRUST_ADM_GRP").getValue();

        } else if ("OPERATIONS".equals(userType)) {
            group = configService.getSettingByName("DEF_ENTRUST_OPS_GRP").getValue();

        } else if ("RETAIL".equals(userType)) {
            group = configService.getSettingByName("DEF_ENTRUST_RET_GRP").getValue();

        } else if ("CORPORATE".equals(userType)) {
            group = configService.getSettingByName("DEF_ENTRUST_CORP_GRP").getValue();

        }

        return group;
    }

    private String getEntrustId(String username, String userType) {

        String entrustId = "";
        User user = null;

        if ("ADMIN".equals(userType)) {
            user = adminUserService.getUserByName(username);

        } else if ("OPERATIONS".equals(userType)) {

            user = operationsUserService.getUserByName(username);


        } else if ("RETAIL".equals(userType)) {

            user = retailUserService.getUserByName(username);


        } else if ("CORPORATE".equals(userType)) {

            user = corporateUserService.getUserByName(username);

        }

        if (user != null) {
            entrustId = user.getEntrustId();
        }
        return entrustId;

    }
}
