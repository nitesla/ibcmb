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
    ModelMapper modelMapper;

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private VerificationRepo verificationRepo;

    @GetMapping("/new")
    public String addCode(CodeDTO codeDTO){
        return "adm/code/add";
    }


    @PostMapping("/new")
    public String createCode(@ModelAttribute("code") CodeDTO codeDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "adm/code/add";

        }

        logger.info("Code {}", codeDTO.toString());
        AdminUser adminUser = new AdminUser();
        adminUser.setDelFlag("N");
        adminUser.setEmail("nnasino2008@live.com");
        adminUser.setUserName("nnasino");
        adminUser.setFirstName("Chigozirim");
        adminUser.setLastName("Torti");
        adminUserRepo.save(adminUser);
        codeService.addCode(codeDTO, adminUser);

        redirectAttributes.addFlashAttribute("success", "Code added successfully");
        return "redirect:/admin/list";
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
    public CodeDTO getCode(@PathVariable Long codeId, Model model){
        CodeDTO code = codeService.getCode(codeId);
        model.addAttribute("code",code);
        return code;
    }


    @GetMapping
    public String getCodes(){

        return "adm/code/view";

    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<CodeDTO> getAllCodes(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CodeDTO> codes = codeService.getCodes(pageable);
        DataTablesOutput<CodeDTO> out = new DataTablesOutput<CodeDTO>();
        out.setDraw(input.getDraw());
        out.setData(codes.getContent());
        out.setRecordsFiltered(codes.getTotalElements());
        out.setRecordsTotal(codes.getTotalElements());
        return out;
    }

    @GetMapping("/{type}")
    public Iterable<CodeDTO> getCodesByType(@PathVariable String type, Model model){
        Iterable<CodeDTO> codeList = codeService.getCodesByType(type);
        model.addAttribute("codeList",codeList);
        return codeList;

    }

    @PostMapping("/{codeId}")
    public String updateCode(@ModelAttribute("codeForm") CodeDTO codeDTO,  BindingResult result, @PathVariable Long codeId,RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            return "add";
        }
        AdminUser adminUser = adminUserRepo.findOne(1l);
        logger.info("Code {}", codeDTO.toString());
        codeDTO.setId(codeId);
        codeService.updateCode(codeDTO, adminUser);
        redirectAttributes.addFlashAttribute("success", "Code updated successfully");
        return "redirect:/admin/codes";
    }

    @PostMapping("/{codeId}/delete")
    public String deleteCode(@PathVariable Long codeId, RedirectAttributes redirectAttributes){
        codeService.deleteCode(codeId);
        redirectAttributes.addFlashAttribute("success", "Code deleted successfully");
        return "redirect:/admin/codes";
    }
}
