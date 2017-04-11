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
    @Autowired
    private ModelMapper modelMapper;

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
        ServiceRequest request = modelMapper.map(requestDTO,ServiceRequest.class);
        requestService.addRequest(request);
        model.addAttribute("success", "Request added successfully");
        return "/retail/requests";
    }

    @GetMapping("/{requestId}")
    public ServiceRequest getServiceRequest(@PathVariable Long requestId, Model model){
        ServiceRequest request = requestService.getRequest(requestId);
        ServiceRequestDTO requestDTO = modelMapper.map(request,ServiceRequestDTO.class);
        model.addAttribute("request",requestDTO);
        return request;
    }

    @GetMapping
    public Iterable<ServiceRequest> getServiceRequests(Model model){
        Iterable<ServiceRequest> requestList = requestService.getRequests(retailUser);
        model.addAttribute("requestList",requestList);
        return requestList;

    }

}
