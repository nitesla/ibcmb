package longbridge.controllers.admin;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.models.AdminUser;
import longbridge.models.ServiceReqConfig;
import longbridge.services.ServiceReqConfigService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @GetMapping("/new")
    public String addConfig(){
        return "adm/serviceReqConfig/add";
    }

    @PostMapping
    public String createConfig(@ModelAttribute("serviceRequestConfig") ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "admin/srconfig/new";
        }

        logger.info("Received form post request: {}", serviceReqConfigDTO.toString());

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

<<<<<<< HEAD
//    @PostMapping
//    public String editConfig(@ModelAttribute("serviceRequestConfig") ServiceReqConfigDTO serviceReqConfigDTO, BindingResult result, Model model) throws Exception{
//        if(result.hasErrors()){
//            return "admin/srconfig/new";
//        }
//
//        serviceReqConfigService.addSeviceReqConfig(serviceReqConfigDTO);
//        model.addAttribute("success","Service Request Config  created successfully");
//        return "redirect:/admin/srconfig";
//    }

    @PostMapping("/{userId}")
    public String updateConfig(@ModelAttribute("serviceRequestConfig") @Valid ServiceReqConfigDTO serviceReqConfigDTO, @PathVariable Long reqId, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()) {
            return "addUser";
        }
        serviceReqConfigDTO.setId(reqId);
        serviceReqConfigService.updateServiceReqConfig(serviceReqConfigDTO);
        model.addAttribute("success", "Admin user updated successfully");
        return "redirect:/admin/users";
    }

}
