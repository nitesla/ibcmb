package longbridge.controllers.admin;

import longbridge.dtos.CategoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Merchant;
import longbridge.services.MerchantService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Created by Yemi Dalley on 6/7/2018.
 */
@Controller
@RequestMapping("/admin/merchants")
public class AdmMerchantController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MerchantService merchantService;

	@Autowired
	MessageSource messageSource;

	@GetMapping("/new")
	public String addMerchant(Merchant merchant) {
		return "adm/merchant/edit";
	}

	@PostMapping
	public String updateMerchant(@ModelAttribute("merchant") @Valid Merchant merchant, BindingResult result,
			Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
		if (result.hasErrors()) {
			result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
			return "adm/merchant/edit";
		}

		try {
			String message = "";
			if (merchant.getId() == null)
				message = merchantService.addMerchant(merchant);
			else
				message = merchantService.updateMerchant(merchant);
			redirectAttributes.addFlashAttribute("message", message);
			String redirectUrl = "/admin/merchants/category/" + merchant.getCategory() + "/edit";
			logger.info("redirecting to :" + redirectUrl);
			return "redirect:" + redirectUrl;
		} catch (InternetBankingException ibe) {
			result.addError(new ObjectError("error", ibe.getMessage()));
			logger.error("Error creating merchant", ibe);
			return "adm/merchant/add";
		}
	}

	@GetMapping("/{merchantId}")
	public Merchant getMerchant(@PathVariable Long id, Model model) {
		Merchant merchant = merchantService.getMerchant(id);
		model.addAttribute("merchant", merchant);
		return merchant;
	}

	@GetMapping
	public String getMerchant() {
		return "adm/merchant/view";
	}

	@GetMapping("/categories/all")
	public String getMerchantCategories() {
		return "adm/merchant/category-view";
	}

	@PostMapping("/statusupdate")
	@ResponseBody
	public ResponseEntity<HttpStatus> updateMerchant(@RequestBody Merchant merchant ) {
		merchantService.updateMerchantStatus(merchant);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/category/{category}/edit")
	public String getMerchantCategory(@PathVariable String category, Model model) {
		model.addAttribute("category", category);
		return "adm/merchant/category-merchant-view";
	}

	@GetMapping("/category/{category}/new")
	public String addMerchant(@PathVariable String category, Model model) {
		Merchant merchant = new Merchant();
		merchant.setCategory(category);
		model.addAttribute("category", category);
		model.addAttribute("merchant", merchant);
		return addMerchant(merchant);
	}

	@GetMapping(path = "/categories")
	public @ResponseBody DataTablesOutput<CategoryDTO> getAllMerchantCategories(DataTablesInput input) {
		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<CategoryDTO> categories = merchantService.getMerchantCategories(pageable);
		DataTablesOutput<CategoryDTO> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(categories.getContent());
		out.setRecordsFiltered(categories.getTotalElements());
		out.setRecordsTotal(categories.getTotalElements());
		return out;
	}

//	@GetMapping(path = "/all")
//	public @ResponseData DataTablesOutput<Merchant> getAllMerchants(DataTablesInput input) {
//		Pageable pageable = DataTablesUtils.getPageable(input);
//		Page<Merchant> merchants = merchantService.getMerchants(pageable);
//		DataTablesOutput<Merchant> out = new DataTablesOutput<Merchant>();
//		out.setDraw(input.getDraw());
//		out.setData(merchants.getContent());
//		out.setRecordsFiltered(merchants.getTotalElements());
//		out.setRecordsTotal(merchants.getTotalElements());
//		return out;
//	}

	@GetMapping(path = "/allcats")
	public @ResponseBody DataTablesOutput<Merchant> getAllMerchantsOfCategory(
			@RequestParam(name = "category") String category, DataTablesInput input) {

		Pageable pageable = DataTablesUtils.getPageable(input);
		Page<Merchant> merchants = merchantService.getMerchantsByCategory(category, pageable);
		DataTablesOutput<Merchant> out = new DataTablesOutput<>();
		out.setDraw(input.getDraw());
		out.setData(merchants.getContent());
		out.setRecordsFiltered(merchants.getTotalElements());
		out.setRecordsTotal(merchants.getTotalElements());
		return out;
	}

	@GetMapping("/{id}/edit")
	public String editMerchant(@PathVariable Long id, Model model) {
		Merchant merchant = merchantService.getMerchant(id);
		model.addAttribute("merchant", merchant);
		return "adm/merchant/edit";
	}

	@GetMapping("/{merchantId}/delete")
	public String deleteMerchant(@PathVariable Long merchantId, RedirectAttributes redirectAttributes) {
		try {
			String message = merchantService.deleteMerchant(merchantId);
			redirectAttributes.addFlashAttribute("message", message);
		} catch (InternetBankingException ibe) {
			logger.error("Error deleting merchant", ibe);
			redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

		}
		return "redirect:/admin/merchant/categories/all";
	}
}
