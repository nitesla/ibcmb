package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.models.Code;
import longbridge.services.CodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fortune on 4/5/2017.
 */
@RestController
@RequestMapping("admin/codes")
public class AdmCodeController {

    @Autowired
    private CodeService codeService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/new")
    public String addCode(){
        return "add-code";
    }

    @PostMapping
    public String createCode(@ModelAttribute("code") CodeDTO codeDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add";
        }
        Code code = modelMapper.map(codeDTO,Code.class);
        codeService.addCode(code);
        model.addAttribute("success", "Code added successfully");
        return "/admin/list";
    }

    @GetMapping("/{codeId}/details")
    public Code getCode(@PathVariable Long codeId, Model model){
        Code code = codeService.getCode(codeId);
        model.addAttribute("code",code);
        return code;
    }


    @GetMapping
    public Iterable<Code> getCodes(Model model){
        Iterable<Code> codeList = codeService.getCodes();
        model.addAttribute("codeList",codeList);
        return codeList;

    }

    @GetMapping("/{type}")
    public Iterable<Code> getCodesByType(@PathVariable String type, Model model){
        Iterable<Code> codeList = codeService.getCodesByType(type);
        model.addAttribute("codeList",codeList);
        return codeList;

    }



    @PostMapping("/{codeId}")
    public String updateCode(@ModelAttribute("code") Code code, @PathVariable Long codeId, BindingResult result, Model model){

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
