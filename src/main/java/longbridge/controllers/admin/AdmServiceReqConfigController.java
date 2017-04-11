package longbridge.controllers.admin;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.services.ServiceReqConfigService;
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
 * Created by Showboy on 08/04/2017.
 */
@Controller
@RequestMapping("/admin/srconfig")
public class AdmServiceReqConfigController {

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

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

        logger.info("Received form post request: {}", serviceReqConfigDTO.toString());

        serviceReqConfigService.addSeviceReqConfig(serviceReqConfigDTO);
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
    public String editConfig(@PathVariable Long reqId, Model model){
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        model.addAttribute("requestDetails", serviceReqConfig);
        return "adm/servicereqconfig/edit";
    }


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
