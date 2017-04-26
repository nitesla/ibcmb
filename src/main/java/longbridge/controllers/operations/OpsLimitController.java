package longbridge.controllers.operations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.models.GlobalLimit;
import longbridge.models.Limit;
import longbridge.models.UserType;
import longbridge.services.CodeService;
import longbridge.services.LimitService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */

@Controller
@RequestMapping("/ops/limits")
public class OpsLimitController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CodeService codeService;

    @Autowired
    LimitService limitService;

    @GetMapping("/retail/new")
    public String getRetailGlobalLimit(Model model) {
        model.addAttribute("globalLimit", new GlobalLimitDTO());
        Iterable<CodeDTO> transferChannels = codeService.getCodesByType("TRANSFER_CHANNEL");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        Iterable<CodeDTO> frequencies = codeService.getCodesByType("FREQUENCY");
        model.addAttribute("transferChannels", transferChannels);
        model.addAttribute("currencies", currencies);
        model.addAttribute("frequencies", frequencies);
        return "/ops/limit/retail/add";
    }

    @PostMapping("/retail")
    public String createRetailGlobalLimit(@ModelAttribute("globalLimit") GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/ops/limit/retail/add";
        }
        globalLimitDTO.setCustomerType(UserType.RETAIL.name());
        limitService.addGlobalLimit(globalLimitDTO);
        redirectAttributes.addFlashAttribute("message", "Retail Global limit created successfully");
        return "redirect:/ops/limits/retail";
    }


    @GetMapping("/retail")
    public String viewRetailLimits() {
        return "ops/limit/retail/view";
    }

    @GetMapping("/retail/all")
    public
    @ResponseBody
    DataTablesOutput<GlobalLimitDTO> getRetailGlobalLimits(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        List<GlobalLimitDTO> globalLimits = limitService.getRetailGlobalLimits();
        DataTablesOutput<GlobalLimitDTO> out = new DataTablesOutput<GlobalLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(globalLimits);
        out.setRecordsFiltered(globalLimits.size());
        out.setRecordsTotal(globalLimits.size());
        return out;
    }

    @GetMapping("/corporate")
    public String viewCorporateLimits() {
        return "/ops/limit/corporate/view";
    }

    @GetMapping("/corporate/all")
    public
    @ResponseBody
    DataTablesOutput<GlobalLimitDTO> getCorporateGlobalLimits(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        List<GlobalLimitDTO> globalLimits = limitService.getCorporateGlobalLimits();
        DataTablesOutput<GlobalLimitDTO> out = new DataTablesOutput<GlobalLimitDTO>();
        out.setDraw(input.getDraw());
        out.setData(globalLimits);
        out.setRecordsFiltered(globalLimits.size());
        out.setRecordsTotal(globalLimits.size());
        return out;
    }


    @GetMapping("/corporate/new")
    public String getCorporateGlobalLimit(Model model) {
        model.addAttribute("globalLimit", new GlobalLimitDTO());
        Iterable<CodeDTO> transferChannels = codeService.getCodesByType("TRANSFER_CHANNEL");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        Iterable<CodeDTO> frequencies = codeService.getCodesByType("FREQUENCY");
        model.addAttribute("transferChannels", transferChannels);
        model.addAttribute("currencies", currencies);
        model.addAttribute("frequencies", frequencies);
        return "/ops/limit/corporate/add";
    }

    @PostMapping("/corporate")
    public String createCorporateGlobalLimit(@ModelAttribute("globalLimit") GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/ops/limits/corporate/add";
        }
        globalLimitDTO.setCustomerType(UserType.CORPORATE.name());
        limitService.addGlobalLimit(globalLimitDTO);
        redirectAttributes.addFlashAttribute("message", "Corporate Global limit created successfully");
        return "redirect:/ops/limits/corporate";
    }


    @GetMapping("/corporate/{id}/edit")
    public String editCorporateLimit(Model model, @PathVariable Long id){
        GlobalLimitDTO globalLimit = limitService.getGlobalCorporateLimit(id);
        Iterable<CodeDTO> transferChannels = codeService.getCodesByType("TRANSFER_CHANNEL");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        Iterable<CodeDTO> frequencies = codeService.getCodesByType("FREQUENCY");
        model.addAttribute("globalLimit",globalLimit);
        model.addAttribute("transferChannels", transferChannels);
        model.addAttribute("currencies", currencies);
        model.addAttribute("frequencies", frequencies);
        return "/ops/limit/corporate/edit";
    }

    @PostMapping("/corporate/update")
    public String updateCorporateLimit(@ModelAttribute("globalLimit") GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes){

        if (result.hasErrors()) {
            return "/ops/limit/corporate/edit";
        }
        limitService.addGlobalLimit(globalLimitDTO);
        redirectAttributes.addFlashAttribute("message", "Corporate global limit updated successfully");
        return "redirect:/ops/limits/corporate";
    }

    @GetMapping("/retail/{id}/edit")
    public String editRetailLimit(Model model, @PathVariable Long id){
        GlobalLimitDTO globalLimit = limitService.getGlobalRetailLimit(id);
        Iterable<CodeDTO> transferChannels = codeService.getCodesByType("TRANSFER_CHANNEL");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        Iterable<CodeDTO> frequencies = codeService.getCodesByType("FREQUENCY");
        model.addAttribute("globalLimit",globalLimit);
        model.addAttribute("transferChannels", transferChannels);
        model.addAttribute("currencies", currencies);
        model.addAttribute("frequencies", frequencies);
        return "ops/limit/retail/edit";
    }

    @PostMapping("/retail/update")
    public String updateRetailLimit(@ModelAttribute("globalLimit") GlobalLimitDTO globalLimitDTO, BindingResult result, RedirectAttributes redirectAttributes){

        if (result.hasErrors()) {
            return "/ops/limit/retail/edit";
        }
        limitService.addGlobalLimit(globalLimitDTO);

        logger.info("The date received from client: {}",globalLimitDTO.getEffectiveDate());

        redirectAttributes.addFlashAttribute("message", "Retail global limit updated successfully");
        return "redirect:/ops/limits/retail";
    }
}
