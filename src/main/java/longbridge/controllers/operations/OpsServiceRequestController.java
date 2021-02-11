package longbridge.controllers.operations;

import longbridge.dtos.RequestHistoryDTO;
import longbridge.models.OperationsUser;
import longbridge.servicerequests.client.RequestService;
import longbridge.servicerequests.client.ServiceRequestDTO;
import longbridge.services.CodeService;
import longbridge.services.OperationsUserService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


/**
 * Created by Fortune on 4/3/2017.
 */


@Controller
@RequestMapping("/ops/requests")
public class OpsServiceRequestController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CodeService codeService;
    @Autowired
    private OperationsUserService opsUserService;
    @Autowired
    private RequestService requestService;

    @GetMapping
    public String getRequests() {
        return "/ops/request/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getRequests(DataTablesInput input, Principal principal) {
        logger.debug("DEBUGGING!!");
        OperationsUser opsUser = opsUserService.getUserByName(principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getOpRequests(opsUser, pageable);
        logger.info("DEBUGGING-2!! {}", serviceRequests.get().findFirst());
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }


    @GetMapping("/{reqId}/details")
    public String getRequest(Model model, @PathVariable Long reqId) {
        ServiceRequestDTO requestDTO = requestService.getRequest(reqId);


        model.addAttribute("serviceRequest", requestDTO);
        model.addAttribute("statuses", codeService.getCodesByType("REQUEST_STATUS"));
        model.addAttribute("requestHistory", new RequestHistoryDTO());
        return "/ops/request/details";

    }


    @GetMapping("/history")
    public String getRequestHistory(Model model) {
        return "/ops/request/history/view";

    }


    @GetMapping("/history/new")
    public String addRequestHistory(Model model) {
        return "/ops/request/history/add";
    }

//Todo : Add back
//    @PostMapping("/history")
//    public String createRequestHistory(@ModelAttribute("requestHistory") RequestHistoryDTO requestHistoryDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes) {
//
//        if (result.hasErrors()) {
//            return "";
//        }
//
//        requestHistoryDTO.setCreatedBy(principal.getName());
//        logger.info("RequestHistoryDTO received: {}", requestHistoryDTO);
//        requestService.addRequestHistory(requestHistoryDTO);
//
//        redirectAttributes.addFlashAttribute("message", "Request updated successfully");
//        return "redirect:/ops/requests/" + requestHistoryDTO.getServiceRequestId() + "/details";
//    }

    @PostMapping
    @ResponseBody
    public String processRequest(@RequestParam String jsonData) {

        logger.info("The received data: {}", jsonData);
        return "redirect:/ops/request/history/view";

    }


}
