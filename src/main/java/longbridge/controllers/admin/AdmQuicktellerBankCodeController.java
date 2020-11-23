package longbridge.controllers.admin;

import longbridge.dtos.QuicktellerBankCodeDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.services.QuicktellerBankCodeService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
@Controller
@RequestMapping("/admin/quickbankcode")
public class AdmQuicktellerBankCodeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private QuicktellerBankCodeService quicktellerBankCodeService;

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String getQuicktellerBankCode() {
        return "/adm/quicktellerbankcode/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<QuicktellerBankCodeDTO> getAllQIs(DataTablesInput input, @RequestParam("csearch") String search) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<QuicktellerBankCodeDTO> qis = null;
        if (StringUtils.isNoneBlank(search)) {
        	qis = quicktellerBankCodeService.findQuicktellerBankCodes(search,pageable);
		}else{
			qis = quicktellerBankCodeService.getQuicktellerBankCodes(pageable);
		}
        DataTablesOutput<QuicktellerBankCodeDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(qis.getContent());
        out.setRecordsFiltered(qis.getTotalElements());
        out.setRecordsTotal(qis.getTotalElements());
        return out;
    }


    @GetMapping("/new")
    public String addQuicktellerBankCodes(Model model) {
        model.addAttribute("quicktellerBankCode", new QuicktellerBankCodeDTO());
        return "/adm/quicktellerbankcode/add";
    }



    @PostMapping
    public String createQuicktellerBankCode(@ModelAttribute("quicktellerBankCode") @Valid QuicktellerBankCodeDTO quicktellerBankCodeDTO, BindingResult result, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            logger.error("Error occurred creating code{}", result.toString());
            return "/adm/quicktellerbankcode/add";
        }
        try {
            String message = quicktellerBankCodeService.addQuicktellerBankCode(quicktellerBankCodeDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/quickbankcode";
        } catch (DuplicateObjectException doe) {
            logger.error("Error occurred creating quickteller bank code", doe);
            result.addError(new ObjectError("error", doe.getMessage()));
            return "adm/quicktellerbankcode/add";
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred creating quickteller bank code {}", ibe.toString());
            result.addError(new ObjectError("error", messageSource.getMessage("quickbankcode.add.failure", null, locale)));
            return "/adm/quicktellerbankcode/add";
        }

    }

    @GetMapping("/{id}/edit")
    public String editQuicktellerBankCode(@PathVariable Long id, Model model) {
        QuicktellerBankCodeDTO fi = quicktellerBankCodeService.getQuicktellerBankCode(id);
        model.addAttribute("quicktellerBankCode", fi);
        return "/adm/quicktellerbankcode/edit";
    }

    @PostMapping("/update")
    public String updateQuicktellerBankCode(@ModelAttribute("quicktellerBankCode") @Valid QuicktellerBankCodeDTO quicktellerBankCodeDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/quicktellerbankcode/edit";
        }
        try {
            String message = quicktellerBankCodeService.updateQuicktellerBankCode(quicktellerBankCodeDTO);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred updating quickteller bank code", ibe);
            result.addError(new ObjectError("error", ibe.getMessage()));
            return "/adm/quicktellerbankcode/edit";
        }

        return "redirect:/admin/quickbankcode";
    }

    @GetMapping("/{id}/delete")
    public String deleteQuicktellerBankCode(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = quicktellerBankCodeService.deleteQuicktellerBankCode(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Error occurred deleting quickteller bank code ", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/admin/quickbankcode";

    }
}
