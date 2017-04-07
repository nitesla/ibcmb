package longbridge.controllers.admin;

import longbridge.models.Code;
import longbridge.services.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("admin/codes")
public class AdmCodeController {

    @Autowired
    private CodeService codeService;

    @GetMapping("/new")
    public String addCode(){
        return "add-code";
    }

    @PostMapping
    public String createCode(@ModelAttribute("code") Code code, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add";
        }
        codeService.addCode(code);
        model.addAttribute("success", "Code added successfully");
        return "/admin/list";
    }

    @GetMapping("/{codeId}")
    public Code getCode(@PathVariable Long codeId, Model model){
        Code code = codeService.getCode(codeId);
        model.addAttribute("code",code);
        return code;
    }

    @GetMapping("/{type}")
    public Iterable<Code> getCodesByType(@PathVariable String type, Model model){
        Iterable<Code> codeList = codeService.getCodesofType(type);
        model.addAttribute("codeList",codeList);
        return codeList;

    }

    @GetMapping
    public Iterable<Code> getCodes(Model model){
        Iterable<Code> codeList = codeService.getCodes();
        model.addAttribute("codeList",codeList);
        return codeList;

    }

    @PostMapping("/{codeId}")
    public String updateCode(@ModelAttribute("codeForm") Code code, @PathVariable Long codeId, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add-code";
        }
        code.setId(codeId);
        codeService.addCode(code);
        model.addAttribute("success", "Code updated successfully");
        return "/admin/codes";
    }

    @PostMapping("/{codeId}/delete")
    public String deleteCode(@PathVariable Long codeId, Model model){
        codeService.deleteCode(codeId);
        model.addAttribute("success", "Code deleted successfully");
        return "redirect:/admin/codes";
    }
}
