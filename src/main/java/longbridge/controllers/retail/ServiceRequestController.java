package longbridge.controllers.retail;

import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.services.RequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Fortune on 4/5/2017.
 */

@RestController
@RequestMapping("retail/requests")
public class ServiceRequestController {

    @Autowired
    private RequestService requestService;


    private RetailUser retailUser = new RetailUser();//TODO user must be authenticated

    @GetMapping("/new")
    public String addServiceRequest(){
        return "add";
    }

    @PostMapping
    public String createServiceRequest(@ModelAttribute("requestForm") ServiceRequestDTO requestDTO, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add";
        }
        requestService.addRequest(requestDTO);
        model.addAttribute("success", "Request added successfully");
        return "/retail/requests";
    }

    @GetMapping("/{requestId}")
    public ServiceRequestDTO getServiceRequest(@PathVariable Long requestId, Model model){
        ServiceRequestDTO request = requestService.getRequest(requestId);
        model.addAttribute("request",request);
        return request;
    }

    @GetMapping
    public Iterable<ServiceRequestDTO> getServiceRequests(Model model){
        Iterable<ServiceRequestDTO> requestList = requestService.getRequests(retailUser);
        model.addAttribute("requestList",requestList);
        return requestList;

    }

}
