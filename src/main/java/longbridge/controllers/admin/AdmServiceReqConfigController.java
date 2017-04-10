package longbridge.controllers.admin;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.services.ServiceReqConfigService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Showboy on 08/04/2017.
 */
@Controller
@RequestMapping("admin/srconfig")
public class AdmServiceReqConfigController {

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/new")
    public String addConfig(){
        return "adm/serviceReqConfig/add";
    }

    @PostMapping
    public String createConfig(@ModelAttribute("serviceRequestConfig") ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "admin/srconfig/new";
        }

        serviceReqConfigService.addSeviceReqConfig(serviceReqConfigDTO);
        model.addAttribute("success","Service Request Config  created successfully");
        return "redirect:/admin/srconfig";
    }

    @GetMapping
    public String getAllConfigs(Model model){
        return "adm/serviceReqConfig/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<ServiceReqConfigDTO> getConfigs(){

        Iterable<ServiceReqConfigDTO> configList = serviceReqConfigService.getServiceReqConfigs();

        return configList;
    }

    @GetMapping("/{reqId}/edit")
    public String editConfig(@PathVariable Long reqId, Model model){
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        model.addAttribute("requestDetails", serviceReqConfig);
        return "adm/serviceReqConfig/";
    }

    @PostMapping
    public String editConfig(@ModelAttribute("serviceRequestConfig") ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "admin/srconfig/new";
        }

        serviceReqConfigService.addSeviceReqConfig(serviceReqConfigDTO);
        model.addAttribute("success","Service Request Config  created successfully");
        return "redirect:/admin/srconfig";
    }

}
