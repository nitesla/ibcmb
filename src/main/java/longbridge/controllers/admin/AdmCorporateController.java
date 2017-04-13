package longbridge.controllers.admin;

import longbridge.dtos.CorporateDTO;
import longbridge.models.Corporate;
import longbridge.services.CorporateService;
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

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/corporates")
public class AdmCorporateController {

    @Autowired
    private CorporateService corporateService;

    @GetMapping("/new")
    public String addCorporate(){
        return "add-corporate";
    }

    @PostMapping
    public String createCorporate(@ModelAttribute("corporateForm") Corporate corporate, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add-corporate";
        }
        corporateService.addCorporate(corporate);
        model.addAttribute("success", "Corporate added successfully");
        return "/admin/corporates";
    }

    @GetMapping("/{corporateId}")
    public Corporate getCorporate(@PathVariable Long corporateId, Model model){
        Corporate corporate = corporateService.getCorporate(corporateId);
        model.addAttribute("corporate",corporate);
        return corporate;
    }

    @GetMapping
    public String getAllCorporates(){
        return "adm/corporate/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<CorporateDTO> getOpsUsers(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateDTO> corps = corporateService.getCorporates(pageable);
        DataTablesOutput<CorporateDTO> out = new DataTablesOutput<CorporateDTO>();
        out.setDraw(input.getDraw());
        out.setData(corps.getContent());
        out.setRecordsFiltered(corps.getTotalElements());
        out.setRecordsTotal(corps.getTotalElements());
        return out;
    }

    @PostMapping("/{corporateId}")
    public String updateCorporate(@ModelAttribute("corporateForm") Corporate corporate, @PathVariable Long corporateId, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add-corporate";
        }
        corporate.setId(corporateId);
        corporateService.addCorporate(corporate);
        model.addAttribute("success", "Corporate updated successfully");
        return "/admin/corporates";
    }

    @PostMapping("/{corporateId}/delete")
    public String deleteCorporate(@PathVariable Long corporateId, Model model){
        corporateService.deleteCorporate(corporateId);
        model.addAttribute("success", "Corporate deleted successfully");
        return "redirect:/admin/corporates";
    }
}

