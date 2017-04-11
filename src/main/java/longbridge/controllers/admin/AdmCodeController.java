package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import longbridge.models.Verification;
import longbridge.repositories.AdminUserRepo;
import longbridge.repositories.VerificationRepo;
import longbridge.services.CodeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("admin/codes")
public class AdmCodeController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CodeService codeService;

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/new")
    public String addCode(){
        return "code/add";
    }

    @PostMapping
    public String createCode(@ModelAttribute("code") CodeDTO codeDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add";
        }
        logger.info("Code {}", codeDTO.toString());
        AdminUser adminUser = new AdminUser();
        adminUser.setDelFlag("N");
        adminUser.setEmail("nnasino2008@live.com");
        adminUser.setUserName("nnasino");
        adminUser.setFirstName("Chigozirim");
        adminUser.setLastName("Torti");
        adminUserRepo.save(adminUser);
        Code code = modelMapper.map(codeDTO, Code.class);
        codeService.add(code, adminUser);
        model.addAttribute("success", "Code added successfully");
        return "/admin/list";
    }

    @PostMapping("/verify/{id}")
    public String verify(@PathVariable Long id){
        logger.info("id {}",id);

        //todo check verifier role
        AdminUser adminUser = adminUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);
        try {
            codeService.verify(verification, adminUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "code/add";
    }

    @PostMapping("/decline/{id}")
    public String decline(@PathVariable Long id){
        logger.info("id {}",id);

        //todo check verifier role
        AdminUser adminUser = adminUserRepo.findOne(1l);
        Verification verification = verificationRepo.findOne(id);
        codeService.decline(verification, adminUser, "todo get the  reason from the frontend");
        return "code/add";
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
    public String updateCode(@ModelAttribute("codeForm") CodeDTO codeDTO, @PathVariable Long codeId, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add-code";
        }
        if(result.hasErrors()){
            return "add";
        }
        AdminUser adminUser = adminUserRepo.findOne(1l);
        logger.info("Code {}", codeDTO.toString());
        Code code = modelMapper.map(codeDTO, Code.class);
        code.setId(codeId);
        codeService.modify(code, adminUser);
        model.addAttribute("success", "Code updated successfully");
//        codeService.addCode(code);
        return "/admin/codes";
    }

    @PostMapping("/{codeId}/delete")
    public String deleteCode(@PathVariable Long codeId, Model model){
        codeService.deleteCode(codeId);
        model.addAttribute("success", "Code deleted successfully");
        return "redirect:/admin/codes";
    }
}
