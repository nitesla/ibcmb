package longbridge.controllers.admin;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import longbridge.repositories.AdminUserRepo;
import longbridge.services.CodeService;
import longbridge.services.VerificationService;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("/admin/codes")
public class AdmCodeController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CodeService codeService;
	@Autowired
	private VerificationService verificationService;
	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private AdminUserRepo adminUserRepo;

	@GetMapping("/new")
	public String addCode(CodeDTO codeDTO) {
		return "adm/code/add";
	}

	@PostMapping
    public String createCode(@ModelAttribute("codeDTO") CodeDTO codeDTO, BindingResult result,  RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            //return "add";
            logger.error("Error occurred creating code{}", result.toString());
            return "adm/code/add";

        }

        logger.info("Code {}", codeDTO.toString());
//        codeService.updateCode(codeDTO,adminUser);//
//        codeService.addObject(code);
        codeService.addCode(codeDTO);

        redirectAttributes.addFlashAttribute("success", "Code added successfully");
        return "redirect:/admin/codes";
    }

	@GetMapping("/{id}/edit")
	public String editCode(@PathVariable Long id, Model model) {
		CodeDTO code = codeService.getCode(id);
		model.addAttribute("code", code);
		return "adm/code/edit";
	}

	@GetMapping("/{codeId}")
	public CodeDTO getCode(@PathVariable Long codeId, Model model) {
		CodeDTO code = codeService.getCode(codeId);
		model.addAttribute("code", code);
		return code;
	}

	@GetMapping
	public String getCodes() {

		return "adm/code/view";

	}

	@GetMapping(path = "/all")
	public @ResponseBody DataTablesOutput<CodeDTO> getAllCodes(DataTablesInput input) {

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
	public Iterable<CodeDTO> getCodesByType(@PathVariable String type, Model model) {
		Iterable<CodeDTO> codeList = codeService.getCodesByType(type);
		model.addAttribute("codeList", codeList);
		return codeList;

	}

	@PostMapping("/update")
	public String updateCode(@ModelAttribute("code") CodeDTO codeDTO, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "add";
		}
		AdminUser adminUser = adminUserRepo.findOne(1l);
		logger.info("Code {}", codeDTO.toString());
		codeService.updateCode(codeDTO, adminUser);
		// codeService.modify(codeDTO, adminUser);
		redirectAttributes.addFlashAttribute("success", "Code updated successfully");
		return "redirect:/admin/codes";
		// codeService.addCode(code);
	}

	@GetMapping("/{codeId}/delete")
	public String deleteCode(@PathVariable Long codeId, RedirectAttributes redirectAttributes) {
		codeService.deleteCode(codeId);
		redirectAttributes.addFlashAttribute("success", "Code deleted successfully");
		return "redirect:/admin/codes";
	}
}
