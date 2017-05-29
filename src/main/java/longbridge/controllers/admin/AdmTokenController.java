package longbridge.controllers.admin;

import longbridge.forms.SyncTokenForm;
import longbridge.forms.TokenForm;
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

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by Fortune on 5/28/2017.
 */

@Controller
@RequestMapping("/admin/token")
public class AdmTokenController {

    @Autowired
    MessageSource messageSource;


    @GetMapping("/assign")
    public String assignToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/assign";

    }

    @PostMapping("/assign")
    public String performAssignToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/assign";
        }

        //TODO call service method

        return "";
    }

    @GetMapping("/activate")
    public String activateToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/activate";

    }

    @PostMapping("/activate")
    public String performActivateToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/activate";
        }

        //TODO call service method

        return "";
    }

    @GetMapping("/block")
    public String blockToken(Model model) {
        model.addAttribute("token", new TokenForm());
        return "/adm/token/block";

    }

    @PostMapping("/block")
    public String performBlockToken(@ModelAttribute("token") @Valid TokenForm tokenForm, BindingResult bindingResult, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/block";
        }

        //TODO call service method

        return "";
    }

    @GetMapping("/synchronize")
    public String SyncToken(Model model) {
        model.addAttribute("token", new SyncTokenForm());
        return "/adm/token/synchronize";

    }

    @PostMapping("/synchronize")
    public String performSyncToken(@ModelAttribute("token") @Valid SyncTokenForm tokenForm, BindingResult bindingResult, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/token/synchronize";
        }

        //TODO call service method

        return "";
    }
}
