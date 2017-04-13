package longbridge.controllers.retail;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.RetailUser;
import longbridge.services.CodeService;
import longbridge.services.RequestService;
import longbridge.services.ServiceReqConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */

@Controller
@RequestMapping("retail/requests")
public class ServiceRequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    @Autowired
    private CodeService codeService;

    private RetailUser retailUser = new RetailUser();//TODO user must be authenticated

    @PostMapping
    public String createServiceRequest(@ModelAttribute("requestForm") ServiceRequestDTO requestDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add";
        }
        requestService.addRequest(requestDTO);
        model.addAttribute("success", "Request added successfully");
        return "/retail/requests";
    }

    @GetMapping("/{reqId}")
    public String makeRequest(@PathVariable Long reqId, Model model){
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        for (ServiceReqFormFieldDTO field : serviceReqConfig.getFormFields()){
            if(field.getFieldType() != null && field.getFieldType().equals("Code")){
                //System.out.println(field.getTypeData());
                //System.out.println(codeService.getCodesByType(field.getTypeData()));
                field.setCodeDTOs(codeService.getCodesByType(field.getTypeData()));
            }

            if(field.getFieldType() != null && field.getFieldType().equals("List")){
                String list = field.getTypeData();
                String myList [] =list.split(",");
                List<String> fieldList = Arrays.asList(myList);
                System.out.println(fieldList);
                model.addAttribute("fixedList", fieldList);
            }
        }
        //System.out.println(serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "cust/servicerequest/add";
    }

    @GetMapping
    public Iterable<ServiceRequestDTO> getServiceRequests(Model model){
        Iterable<ServiceRequestDTO> requestList = requestService.getRequests(retailUser);
        model.addAttribute("requestList",requestList);
        return requestList;

    }

}
