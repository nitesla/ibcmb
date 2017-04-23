package longbridge.controllers.admin;

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

import longbridge.dtos.SettingDTO;
import longbridge.services.ConfigurationService;

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
	public String addSetting(SettingDTO settingDTO) {
		return "adm/setting/add";
	}

	@PostMapping("/{settingId}/edit")
	public String updateSetting(@ModelAttribute("setting") SettingDTO dto, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {//TODO
			return "adm/setting/add";
		}
		configurationService.updateSetting(dto);
		redirectAttributes.addFlashAttribute("success", "Setting updated successfully");
		return "redirect:/admin/settings";
	}

	@PostMapping()
	public String createSetting(@ModelAttribute("setting") SettingDTO dto, BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {//TODO
			return "adm/setting/add";
		}
		configurationService.addSetting(dto);
		redirectAttributes.addFlashAttribute("success", "Setting created successfully");
		return "redirect:/admin/settings";
	}
	
	  @GetMapping(path = "/all")
	    public @ResponseBody DataTablesOutput<SettingDTO> getAllCodes(DataTablesInput input){
	        Pageable pageable = DataTablesUtils.getPageable(input);
	        Page<SettingDTO> settings = configurationService.getSettings(pageable);
	        DataTablesOutput<SettingDTO> out = new DataTablesOutput<SettingDTO>();
	        out.setDraw(input.getDraw());
	        out.setData(settings.getContent());
	        out.setRecordsFiltered(settings.getTotalElements());
	        out.setRecordsTotal(settings.getTotalElements());
	        return out;
	    }

}
