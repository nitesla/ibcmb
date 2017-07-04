package longbridge.controllers.admin;

import longbridge.exception.InternetBankingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import longbridge.dtos.SettingDTO;
import longbridge.services.ConfigurationService;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Controller for maintaining application {@link longbridge.models.Setting}
 * Created by LB-PRJ-020 on 4/5/2017.
 */
@Controller
@RequestMapping("/admin/settings")
public class AdmSettingController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping()
	public String listSettings(Model model) {
		return "adm/setting/view";
	}

	@GetMapping("/{id}/edit")
	public String editSetting(@PathVariable Long id, Model model) {
		SettingDTO dto = configurationService.getSetting(id);
		model.addAttribute("setting", dto);
		return "adm/setting/edit";
	}

	@GetMapping("/new")
	public String addSetting(Model model) {
		model.addAttribute("setting",new SettingDTO());
		return "adm/setting/add";
	}

	@PostMapping("/update")
	public String updateSetting(@ModelAttribute("setting") @Valid SettingDTO dto, BindingResult result,
			RedirectAttributes redirectAttributes,Locale locale) {
		if (result.hasErrors()) {//TODO
			result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.require",null,locale)));
			return "adm/setting/edit";
		}
		try {
			String message = configurationService.updateSetting(dto);
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/admin/settings";
		}
		catch (InternetBankingException ibe){
			logger.error("Error updating setting",ibe);
			result.addError(new ObjectError("exception",ibe.getMessage()));
			return "adm/setting/edit";

		}
	}

	@PostMapping()
	public String createSetting(@ModelAttribute("setting") @Valid SettingDTO dto, BindingResult result,
								RedirectAttributes redirectAttributes, Locale locale) {

		if (result.hasErrors()) {//TODO
			result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
			return "adm/setting/add";
		}
		try {
			String message = configurationService.addSetting(dto);
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/admin/settings";
		}
		catch (InternetBankingException ibe){
			logger.error("Error creating setting",ibe);
			result.addError(new ObjectError("exception",ibe.getMessage()));
			return "adm/setting/add";

		}
	}
	
	  @GetMapping(path = "/all")
	    public @ResponseBody DataTablesOutput<SettingDTO> getAllCodes(DataTablesInput input, @RequestParam("csearch") String search){
	        Pageable pageable = DataTablesUtils.getPageable(input);
	        Page<SettingDTO> settings = null;
	        if (StringUtils.isNoneBlank(search)) {
	        	 settings = configurationService.findSetting(search,pageable);
			}else{
				 settings = configurationService.getSettings(pageable);
			}
	       
	        DataTablesOutput<SettingDTO> out = new DataTablesOutput<SettingDTO>();
	        out.setDraw(input.getDraw());
	        out.setData(settings.getContent());
	        out.setRecordsFiltered(settings.getTotalElements());
	        out.setRecordsTotal(settings.getTotalElements());
	        return out;
	    }

	@GetMapping("/{id}/delete")
	public String deleteSetting(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			String message = configurationService.deleteSetting(id);
			redirectAttributes.addFlashAttribute("message", message);

		}
		catch (InternetBankingException ibe){
			logger.error("Error deleting setting",ibe);
			redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

		}
		return "redirect:/admin/settings";
	}

}
