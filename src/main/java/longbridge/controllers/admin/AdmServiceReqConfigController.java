package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.services.CodeService;
import longbridge.services.ServiceReqConfigService;
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

import javax.validation.Valid;

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
    private ModelMapper modelMapper;


    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/new")
    public String addConfig(){
        return "adm/servicereqconfig/add";
    }

    @PostMapping
    public String createConfig(@ModelAttribute("serviceRequestConfig") ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "admin/srconfig/new";
        }

        serviceReqConfigService.addServiceReqConfig(serviceReqConfigDTO);
        model.addAttribute("success","Service Request Config  created successfully");
        return "redirect:/admin/srconfig";
    }

    @GetMapping
    public String getAllConfigs(Model model){

        return "adm/servicereqconfig/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<ServiceReqConfigDTO> getConfigs(DataTablesInput input){

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
    public String  editConfig(@PathVariable Long reqId, Model model){
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        Iterable<CodeDTO> fieldTypes = codeService.getCodesByType("SERVICE_REQUEST");
        model.addAttribute("serviceReqConfig", serviceReqConfig);
        model.addAttribute("requestFieldType",fieldTypes);
        //return "adm/serviceReqConfig/edit";
        return "/adm/serviceReqConfig/edit";

    }

    @PostMapping("/{reqId}/update")
    public String updateConfig(@ModelAttribute("serviceReqConfig") ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result,@PathVariable Long reqId, Model model) throws Exception{
        if(result.hasErrors()) {
            return "admin/srconfig/new";
        }
       serviceReqConfigDTO.setId(reqId);
        logger.info("My service req : {}",serviceReqConfigDTO.toString());
        serviceReqConfigService.updateServiceReqConfig(serviceReqConfigDTO);
        model.addAttribute("success", "Service Request Configuration updated successfully");
        return "redirect:/admin/srconfig";
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable  Long reqId, Model model){
        ServiceReqConfigDTO serviceReq = serviceReqConfigService.getServiceReqConfig(reqId);
        model.addAttribute("requestDetails",serviceReq);
        return "adm/servicereqconfig/add";
    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long reqId) {
        serviceReqConfigService.delServiceReqConfig(reqId);
        return "redirect:/retail/users";
    }

}
