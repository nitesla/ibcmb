package longbridge.controllers.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.models.FinancialInstitutionType;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.VerificationService;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
@Controller
@RequestMapping("/admin/finst")
public class AdmFinancialInstitutionController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    private VerificationService verificationService;

    @GetMapping
    public String getFis() {
        return "adm/financialinstitution/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<FinancialInstitutionDTO> getAllFis(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FinancialInstitutionDTO> fis = financialInstitutionService.getFinancialInstitutions(pageable);
        DataTablesOutput<FinancialInstitutionDTO> out = new DataTablesOutput<FinancialInstitutionDTO>();
        out.setDraw(input.getDraw());
        out.setData(fis.getContent());
        out.setRecordsFiltered(fis.getTotalElements());
        out.setRecordsTotal(fis.getTotalElements());
        return out;
    }

    @GetMapping("/new")
    public String addFi(FinancialInstitutionDTO financialInstitutionDTO, Model model) {
        model.addAttribute("types", FinancialInstitutionType.values());
        return "adm/financialinstitution/add";
    }

    @PostMapping
    public String createFi(@ModelAttribute("financialInstitutionDTO") FinancialInstitutionDTO financialInstitutionDTO, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            //return "add";
            logger.error("Error occurred creating code{}", result.toString());
            return "adm/financialinstitution/add";
        }

        boolean response = financialInstitutionService.addFinancialInstitution(financialInstitutionDTO);
        if (response == false){
            logger.error("Error occurred creating FI to DB {}");
            return "adm/financialinstitution/add";
        }

        redirectAttributes.addFlashAttribute("success", "Financial Institution added successfully");
        return "redirect:/admin/finst";
    }

    @GetMapping("/{id}/edit")
    public String editFi(@PathVariable Long id, Model model) {
        FinancialInstitutionDTO fi = financialInstitutionService.getFinancialInstitution(id);
        model.addAttribute("fi", fi);
        model.addAttribute("types", FinancialInstitutionType.values());
        return "adm/financialinstitution/edit";
    }

    @PostMapping("/update")
    public String updateFi(@ModelAttribute("financialInstitution") FinancialInstitutionDTO financialInstitutionDTO, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add";
        }

        boolean response = financialInstitutionService.updateFinancialInstitution(financialInstitutionDTO);

        if (response == false){
            logger.error("Error occurred updatinging FI to DB {}");
            return "adm/financialinstitution/add";
        }

        redirectAttributes.addFlashAttribute("success", "Financial Institution updated successfully");
        return "redirect:/admin/finst";
    }

    @GetMapping("/{id}/delete")
    public String deleteCode(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean response = financialInstitutionService.deleteFi(id);
        if (response == false){
            logger.error("Error occurred deleting FI {}");
            return "adm/financialinstitution/view";
        }

        redirectAttributes.addFlashAttribute("success", "Financial Institution deleted successfully");
        return "redirect:/admin/finst";
    }
}
