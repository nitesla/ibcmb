package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.UserGroup;
import longbridge.services.CodeService;
import longbridge.services.ServiceReqConfigService;
import longbridge.services.UserGroupService;
import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Controller
@RequestMapping("/admin/srconfig")
public class AdmServiceReqConfigController {

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    @Autowired
    private CodeService codeService;

    @Autowired
    UserGroupService userGroupService;

    @Autowired
    MessageSource messageSource;


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @ModelAttribute
    public void init(Model model){
        Iterable<CodeDTO> requestTypes = codeService.getCodesByType("REQUEST_TYPE");
        Iterable<CodeDTO> fieldTypes = codeService.getCodesByType("SERVICE_REQUEST");
        Iterable<UserGroupDTO> requestGroups = userGroupService.getGroups();
        model.addAttribute("requestTypes", requestTypes);
        model.addAttribute("requestFieldTypes", fieldTypes);
        model.addAttribute("requestGroups", requestGroups);

    }

    @GetMapping("/new")
    public String addConfig(Model model) {
        model.addAttribute("serviceReqConfig", new ServiceReqConfigDTO());
             return "adm/servicereqconfig/add";
    }

    @PostMapping
    public String createConfig(@ModelAttribute("serviceReqConfig") @Valid ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "adm/servicereqconfig/add";
        }

        try {
            String message = serviceReqConfigService.addServiceReqConfig(serviceReqConfigDTO);
            redirectAttributes.addAttribute("message", message);
            return "redirect:/admin/srconfig";
        } catch (InternetBankingException ibe) {
            result.addError(new ObjectError("error", messageSource.getMessage("req.add.failure", null, locale)));
            logger.error("Error creating service request", ibe);
            return "adm/servicereqconfig/add";

        }
    }

    @GetMapping
    public String getAllConfigs(Model model) {
        return "adm/servicereqconfig/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<ServiceReqConfigDTO> getConfigs(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceReqConfigDTO> serviceReqConfigs = serviceReqConfigService.getServiceReqConfigs(pageable);
        DataTablesOutput<ServiceReqConfigDTO> out = new DataTablesOutput<ServiceReqConfigDTO>();
        out.setDraw(input.getDraw());
        out.setData(serviceReqConfigs.getContent());
        out.setRecordsFiltered(serviceReqConfigs.getTotalElements());
        out.setRecordsTotal(serviceReqConfigs.getTotalElements());
        return out;
    }

    @GetMapping("/{reqId}/edit")
    public String editConfig(@PathVariable Long reqId, Model model) {
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
         model.addAttribute("serviceReqConfig", serviceReqConfig);
         return "/adm/serviceReqConfig/edit";

    }

    @PostMapping("/update")
    public String updateConfig(@ModelAttribute("serviceReqConfig") @Valid ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, RedirectAttributes redirectAttributes,Locale locale) throws Exception {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/serviceReqConfig/edit";
        }

        try {
            String message = serviceReqConfigService.updateServiceReqConfig(serviceReqConfigDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/srconfig";
        }
        catch (InternetBankingException  ibe){
            result.addError(new ObjectError("error", messageSource.getMessage("req.update.failure", null, locale)));
            logger.error("Error creating service request", ibe);
            return "/adm/serviceReqConfig/edit";
        }
    }

    @GetMapping("/{userId}")
    public String getRequest(@PathVariable Long reqId, Model model) {
        ServiceReqConfigDTO serviceReq = serviceReqConfigService.getServiceReqConfig(reqId);
        model.addAttribute("requestDetails", serviceReq);
        return "adm/servicereqconfig/add";
    }

    @GetMapping("/{reqId}/delete")
    public String deleteRequest(@PathVariable Long reqId,RedirectAttributes redirectAttributes,Locale locale) {
        try {
            String message = serviceReqConfigService.delServiceReqConfig(reqId);
            redirectAttributes.addFlashAttribute("message",message);
        }
        catch (InternetBankingException ibe){
            logger.error("Error deleting service request",ibe);
            redirectAttributes.addFlashAttribute("message",messageSource.getMessage("req.delete.failure",null,locale));

        }
        return "redirect:/admin/srconfig";

    }

}
