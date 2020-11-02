package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.CodeTypeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.CodeService;
import longbridge.utils.DataTablesUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Fortune on 4/5/2017.
 */
@Controller
@RequestMapping("/admin/codes")
public class AdmCodeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CodeService codeService;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	MessageSource messageSource;


	@GetMapping("/new")
	public String addCode(CodeDTO codeDTO) {
		return "adm/code/add";
	}

	@PostMapping()
	public String createCode(@ModelAttribute("codeDTO") @Valid CodeDTO codeDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
		if(result.hasErrors()){
			result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
			return "adm/code/add";
		}


		try {

			String message = codeService.addCode(codeDTO);
			redirectAttributes.addFlashAttribute("message", message);


			return "redirect:/admin/codes/alltypes";
		}
		catch (InternetBankingException ibe){
			result.addError(new ObjectError("error",ibe.getMessage()));
			logger.error("Error creating code",ibe);
			return "adm/code/add";
		}
	}



	@GetMapping("/{codeId}")
	public CodeDTO getCode(@PathVariable Long codeId, Model model) {
		CodeDTO code = codeService.getCode(codeId);
		model.addAttribute("code",code);
		return code;
	}

	@GetMapping
	public String getCodes() {
		return "adm/code/view";
	}

	@GetMapping("/alltypes")
	public String getCodeTypes() {
		return "adm/code/type-view";
	}

	@GetMapping("/type/{type}/edit")
	public String getCodeType(@PathVariable String type, Model model) {
		model.addAttribute("codeType", type);
		return "adm/code/type-code-view";
	}


	@GetMapping("/type/{type}/new")
	public String addCode(@PathVariable String type, Model model) {
		CodeDTO code = new CodeDTO();
		code.setType(type);
		model.addAttribute("codeType", type);
		model.addAttribute("codeDTO", code);
		return addCode(code);
	}


	@GetMapping(path = "/type")
	public @ResponseBody DataTablesOutput<CodeTypeDTO> getAllCodeTypes(DataTablesInput input) {

		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<CodeTypeDTO> codeTypes = codeService.getCodeTypes(pageable);
		DataTablesOutput<CodeTypeDTO> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(codeTypes.getContent());
		out.setRecordsFiltered(codeTypes.getTotalElements());
		out.setRecordsTotal(codeTypes.getTotalElements());
		return out;
	}

	@GetMapping(path = "/all")
	public @ResponseBody DataTablesOutput<CodeDTO> getAllCodes(DataTablesInput input) {

		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<CodeDTO> codes = codeService.getCodes(pageable);
		DataTablesOutput<CodeDTO> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(codes.getContent());
		out.setRecordsFiltered(codes.getTotalElements());
		out.setRecordsTotal(codes.getTotalElements());
		return out;
	}

	@GetMapping(path = "/alltype")
	public @ResponseBody DataTablesOutput<CodeDTO> getAllCodesOfType(@RequestParam(name="codeType") String codeType,DataTablesInput input) {
		System.out.println(codeType);
		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<CodeDTO> codes = codeService.getCodesByType(codeType, pageable);
		DataTablesOutput<CodeDTO> out = new DataTablesOutput<>();
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

	@GetMapping("/{id}/edit")
	public String editCode(@PathVariable Long id, Model model) {
		CodeDTO code = codeService.getCode(id);
		model.addAttribute("code", code);
		return "adm/code/edit";
	}

	@PostMapping("/update")
	public String updateCode(@ModelAttribute("code") @Valid CodeDTO codeDTO, BindingResult result, Principal principal,RedirectAttributes redirectAttributes,Locale locale) {
		if(result.hasErrors()){
			logger.error("Error occurred creating code{}", result);
			result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
			return "adm/code/edit";

		}
		try {
			String message = codeService.updateCode(codeDTO);
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/admin/codes/alltypes";
		}
		catch (InternetBankingException ibe){
			result.addError(new ObjectError("error",ibe.getMessage()));
			logger.error("Error updating Code",ibe);
			return "adm/code/edit";

		}
	}

	@GetMapping("/{codeId}/delete")
	public String deleteCode(@PathVariable Long codeId, RedirectAttributes redirectAttributes) {

		try {
			String message = codeService.deleteCode(codeId);
			redirectAttributes.addFlashAttribute("message", message);
		}

		catch (InternetBankingException ibe){
			logger.error("Error deleting Code",ibe);
			redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

		}
		return "redirect:/admin/codes/alltypes";
	}
}