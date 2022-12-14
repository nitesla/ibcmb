package longbridge.controllers.admin;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.FinancialInstitutionType;
import longbridge.services.FinancialInstitutionService;
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
@RequestMapping("/admin/finst")
public class AdmFinancialInstitutionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String getFinancialInstitutions() {
        return "/adm/financialinstitution/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<FinancialInstitutionDTO> getAllFis(DataTablesInput input,@RequestParam("csearch") String search) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FinancialInstitutionDTO> fis = null;
        if (StringUtils.isNoneBlank(search)) {
        	fis = financialInstitutionService.findFinancialInstitutions(search,pageable);
		}else{
			fis = financialInstitutionService.getFinancialInstitutions(pageable);
		}
        DataTablesOutput<FinancialInstitutionDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(fis.getContent());
        out.setRecordsFiltered(fis.getTotalElements());
        out.setRecordsTotal(fis.getTotalElements());
        return out;
    }

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("types", FinancialInstitutionType.values());
    }

    @GetMapping("/new")
    public String addFinancialInstutions(Model model) {
        model.addAttribute("financialInstitution", new FinancialInstitutionDTO());
        return "/adm/financialinstitution/add";
    }



    @PostMapping
    public String createFinancialInstitution(@ModelAttribute("financialInstitution") @Valid FinancialInstitutionDTO financialInstitutionDTO, BindingResult result, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            logger.error("Error occurred creating code{}", result.toString());
            return "/adm/financialinstitution/add";
        }
        try {
            String message = financialInstitutionService.addFinancialInstitution(financialInstitutionDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/finst";
        } catch (DuplicateObjectException doe) {
            logger.error("Error occurred creating financial institution", doe);
            result.addError(new ObjectError("error", doe.getMessage()));
            return "adm/financialinstitution/add";
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred creating financial institution {}", ibe.toString());
            result.addError(new ObjectError("error", messageSource.getMessage("institution.add.failure", null, locale)));
            return "/adm/financialinstitution/add";
        }

    }

    @GetMapping("/{id}/edit")
    public String editFinancialInstitution(@PathVariable Long id, Model model) {
        FinancialInstitutionDTO fi = financialInstitutionService.getFinancialInstitution(id);
        model.addAttribute("financialInstitution", fi);
        return "/adm/financialinstitution/edit";
    }

    @PostMapping("/update")
    public String updateFinancialInstitution(@ModelAttribute("financialInstitution") @Valid FinancialInstitutionDTO financialInstitutionDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/financialinstitution/edit";
        }
        try {
            String message = financialInstitutionService.updateFinancialInstitution(financialInstitutionDTO);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred updating financial institution", ibe);
            result.addError(new ObjectError("error", ibe.getMessage()));
            return "/adm/financialinstitution/edit";
        }

        return "redirect:/admin/finst";
    }

    @GetMapping("/{id}/delete")
    public String deleteFinancialInstitution(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = financialInstitutionService.deleteFinancialInstitution(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Error occurred deleting financial institution ", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/admin/finst";

    }
}
