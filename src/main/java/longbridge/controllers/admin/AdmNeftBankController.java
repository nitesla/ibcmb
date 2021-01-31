package longbridge.controllers.admin;

import longbridge.dtos.NeftBankDTO;
import longbridge.dtos.NeftBankNameDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.NeftBankService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/admin/neftb")
public class AdmNeftBankController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private NeftBankService neftBankService;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	MessageSource messageSource;


	@GetMapping("/new")
	public String addNeftBank(NeftBankDTO neftBankDTO) {
		return "adm/neftbank/add";
	}

	@PostMapping()
	public String createNeftBank(@ModelAttribute("neftBankDTO") @Valid NeftBankDTO neftBankDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale){
		if(result.hasErrors()){
			result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
			return "adm/neftbank/add";
		}

		try {
			String message = neftBankService.addNeftBank(neftBankDTO);
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/admin/neftb/banks/" + neftBankDTO.getBankName() + "/edit";
		}
		catch (InternetBankingException ibe){
			result.addError(new ObjectError("error",ibe.getMessage()));
			logger.error("Error creating code",ibe);
			return "adm/neftbank/add";
		}
	}



	@GetMapping("/{bankId}")
	public NeftBankDTO getNeftBank(@PathVariable Long bankId, Model model) {
		NeftBankDTO neftBankDTO = neftBankService.getNeftBank(bankId);
		model.addAttribute("neftBank",neftBankDTO);
		return neftBankDTO;
	}

	@GetMapping
	public String getNeftBanks() {
		return "adm/neftbank/view";
	}

	@GetMapping("/banks")
	public String getNeftBankNames() {
		return "adm/neftbank/type-view";
	}

	@GetMapping("/banks/{bankName}/edit")
	public String getNeftBankName(@PathVariable String bankName, Model model) {
		model.addAttribute("bankName", bankName);
		return "adm/neftbank/type-code-view";
	}


	@GetMapping("/banks/{bankName}/new")
	public String addNeftBank(@PathVariable String bankName, Model model) {
		NeftBankDTO neftBank = new NeftBankDTO();
		neftBank.setBankName(bankName);
		model.addAttribute("bankName", bankName);
		model.addAttribute("neftBankDTO", neftBank);
		return addNeftBank(neftBank);
	}


	@GetMapping(path = "/bank_name")
	public @ResponseBody DataTablesOutput<NeftBankNameDTO> getAllNeftBankNames(DataTablesInput input, @RequestParam("csearch") String search) {
		Page<NeftBankNameDTO> banks = null;
				Pageable pageable = DataTablesUtils.getPageable(input);
		if (StringUtils.isNoneBlank(search)) {
			banks = neftBankService.getNeftBankNames(search, pageable);
		}
		else{
			banks = neftBankService.getNeftBankNames(pageable);
		}

		DataTablesOutput<NeftBankNameDTO> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(banks.getContent());
		out.setRecordsFiltered(banks.getTotalElements());
		out.setRecordsTotal(banks.getTotalElements());
		return out;
	}

	@GetMapping(path = "/all")
	public @ResponseBody DataTablesOutput<NeftBankDTO> getAllNeftBanks(DataTablesInput input) {

		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<NeftBankDTO> neftBanks = neftBankService.getNeftBanks(pageable);
		DataTablesOutput<NeftBankDTO> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(neftBanks.getContent());
		out.setRecordsFiltered(neftBanks.getTotalElements());
		out.setRecordsTotal(neftBanks.getTotalElements());
		return out;
	}

	@GetMapping(path = "/allBank")
	public @ResponseBody DataTablesOutput<NeftBankDTO> getAllNeftBankOfBankName(@RequestParam(name="bankName") String bankName,DataTablesInput input) {
		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<NeftBankDTO> codes = neftBankService.getNeftBranchesByBankName(bankName, pageable);
		DataTablesOutput<NeftBankDTO> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(codes.getContent());
		out.setRecordsFiltered(codes.getTotalElements());
		out.setRecordsTotal(codes.getTotalElements());
		return out;
	}

	@GetMapping("/{bankName}")
	public Iterable<NeftBankDTO> getNeftBanksByBankName(@PathVariable String bankName, Model model) {
		Iterable<NeftBankDTO> neftBankList = neftBankService.getNeftBranchesByBankName(bankName);
		model.addAttribute("neftBankList", neftBankList);
		return neftBankList;
	}

	@GetMapping("/{id}/edit")
	public String editCode(@PathVariable Long id, Model model) {
		NeftBankDTO neftBank = neftBankService.getNeftBank(id);
		model.addAttribute("bank", neftBank);
		return "adm/neftbank/edit";
	}

	@PostMapping("/update")
	public String updateCode(@ModelAttribute("neftBank") @Valid NeftBankDTO neftBankDTO, BindingResult result, Principal principal,RedirectAttributes redirectAttributes,Locale locale) {
		if(result.hasErrors()){
			logger.error("Error occurred creating code{}", result);
			result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
			return "adm/neftbank/edit";

		}
		try {
			String message = neftBankService.updateNeftBank(neftBankDTO);
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/admin/neftb/banks/" +neftBankDTO.getBankName()+ "/edit";
		}
		catch (InternetBankingException ibe){
			result.addError(new ObjectError("error",ibe.getMessage()));
			logger.error("Error updating Code",ibe);
			return "adm/neftbank/edit";

		}
	}

	@GetMapping("/{bankId}/delete")
	public String deleteNeftBank(@PathVariable Long bankId, RedirectAttributes redirectAttributes) {

		try {
			String message = neftBankService.deletNeftBank(bankId);
			redirectAttributes.addFlashAttribute("message", message);
		}

		catch (InternetBankingException ibe){
			logger.error("Error deleting Code",ibe);
			redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

		}
		return "redirect:/admin/neftb/banks";
	}
}