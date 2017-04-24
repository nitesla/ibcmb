package longbridge.controllers.operations;

import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.services.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by Fortune on 4/3/2017.
 */


@Controller
@RequestMapping("/ops/requests")
public class OpsServiceRequestController {


    private Logger logger= LoggerFactory.getLogger(this.getClass());


    @Autowired
    RequestService requestService;

    @GetMapping
    public  String getRequests(){
        return "/ops/request/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getRequests(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getRequests(new RetailUser(),pageable);
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<ServiceRequestDTO>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }


    @GetMapping("/{reqId}/details")
    public String getRequest(Model model, @PathVariable Long reqId){
        ServiceRequestDTO requestDTO = requestService.getRequest(reqId);
        model.addAttribute("serviceRequest", requestDTO);
        return "/ops/request/details";

    }


    @GetMapping("/history")
    public String getRequestHistory(Model model){
        return "/ops/request/history/view";

    }

    @GetMapping("/{reqId}/history/all")
    public DataTablesOutput<RequestHistoryDTO> getRequestHistory(DataTablesInput input,@PathVariable Long reqId){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<RequestHistoryDTO> requestHistories = requestService.getRequestHistories(reqId,pageable);
        DataTablesOutput<RequestHistoryDTO> out = new DataTablesOutput<RequestHistoryDTO>();
        out.setDraw(input.getDraw());
        out.setData(requestHistories.getContent());
        out.setRecordsFiltered(requestHistories.getTotalElements());
        out.setRecordsTotal(requestHistories.getTotalElements());
        return out;
    }


    @GetMapping("/history/new")
    public  String addRequestHistory(Model model){
        return "/ops/request/history/add";
    }


    @PostMapping
    @ResponseBody
    public String processRequest(@RequestParam String jsonData) {

        logger.info("The received data: {}",jsonData);
        return "redirect:/ops/request/history/view";

    }

//    @PostMapping
//    public String createRequestHistory(@ModelAttribute("requestHistory")RequestHistoryDTO requestHistoryDTO, BindingResult result, RedirectAttributes redirectAttributes){
//
//        if(result.hasErrors()){
//            return "/ops/request/history/add";
//        }
//        requestService.addRequestHistory(requestHistoryDTO);
//        redirectAttributes.addFlashAttribute("message","Request updated successfully");
//        return "redirect:/ops/request/history/view";
//    }
}
