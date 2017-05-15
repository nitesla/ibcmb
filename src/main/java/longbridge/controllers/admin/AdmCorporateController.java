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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/admin/corporates")
public class AdmCorporateController {

    @Autowired
    private CorporateService corporateService;

    @GetMapping("/new")
    public String addCorporate(Model model){
        model.addAttribute("corporate", new CorporateDTO());
        return "adm/corporate/add";
    }

    @PostMapping
    public String createCorporate(@ModelAttribute("corporate") CorporateDTO corporate, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/corporate/add";
        }
        String message = corporateService.addCorporate(corporate);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/admin/corporates";
    }

    /**
     * Edit an existing user
     * @return
     */
    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        CorporateDTO corporate = corporateService.getCorporate(id);
        model.addAttribute("corporate", corporate);
        return "adm/corporate/edit";
    }

    @GetMapping("/{corporateId}")
    public String getCorporate(@PathVariable Long corporateId, Model model){
        CorporateDTO corporate = corporateService.getCorporate(corporateId);
        model.addAttribute("corporate",corporate);
        return "adm/corporates/details";
    }

    @GetMapping
    public String getAllCorporates(){
        return "adm/corporate/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<CorporateDTO> getCorporates(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorporateDTO> corps = corporateService.getCorporates(pageable);
        DataTablesOutput<CorporateDTO> out = new DataTablesOutput<CorporateDTO>();
        out.setDraw(input.getDraw());
        out.setData(corps.getContent());
        out.setRecordsFiltered(corps.getTotalElements());
        out.setRecordsTotal(corps.getTotalElements());
        return out;
    }

    @PostMapping("/update")
    public String updateCorporate(@ModelAttribute("corporate") CorporateDTO corporate, BindingResult result,RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/corporate/new";
        }
        corporateService.addCorporate(corporate);
        redirectAttributes.addFlashAttribute("message", "Corporate updated successfully");
        return "redirect:/admin/corporates";
    }

    @GetMapping("/{corporateId}/delete")
    public String deleteCorporate(@PathVariable Long corporateId, RedirectAttributes redirectAttributes){
        String message = corporateService.deleteCorporate(corporateId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/admin/corporates";
    }
}

