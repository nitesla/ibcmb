package longbridge.controllers.admin;

import longbridge.models.Corporate;
import longbridge.services.CorporateService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String addCorporate(Corporate corporate){
        return "adm/corporate/add";
    }

    @PostMapping("/new")
    public String createCorporate(@ModelAttribute("corporateForm") Corporate corporate, BindingResult result, Model model){
        if(result.hasErrors()){
            return "adm/corporate/add";
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
    public String getAllCorporates(Model model){
        //Iterable<OperationsUser> operationsUserList= operationsUserService.getUsers();
        //model.addAttribute("operationsUserList",operationsUserList);
        return "adm/corporate/view";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Corporate> getCorporates(){
        Iterable<Corporate> corporateList= corporateService.getCorporates();
        //model.addAttribute("adminUserList",adminUserList);
        return corporateList;
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

