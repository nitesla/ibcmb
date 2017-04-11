package longbridge.controllers.admin;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.services.ServiceReqConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
    public @ResponseBody String getConfigs( HttpServletResponse response){

        List<ServiceReqConfigDTO> configList = serviceReqConfigService.getServiceReqConfigs();

        JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
        ObjectNode result = nodeFactory.objectNode();
        result.put("recordsTotal", configList.size());
        result.put("recordsFiltered", configList.size());

        ArrayNode an = result.putArray("data");

        for(ServiceReqConfigDTO dto: configList){
            ObjectNode row = nodeFactory.objectNode();
            row.put("0", dto.getId());
            row.put("1", dto.getRequestName());
            row.put("2", "<a href='/admin/srconfig/"+ dto.getId()+ "/edit' class='btn btn-sm btn-success'><i class='fa fa-edit'></i> Edit</a>" +
                    "<a href='/admin/srconfig/"+ dto.getId()+ "/delete' class='btn btn-sm btn-danger'><i class='fa fa-trash'></i> Delete</a>");
            an.add(row);
        }
        response.setContentType("application/json");

        return result.toString();
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
