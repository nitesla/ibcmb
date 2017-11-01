package longbridge.controllers.operations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.OperationsUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.CodeService;
import longbridge.services.OperationsUserService;
import longbridge.services.RequestService;
import longbridge.services.RetailUserService;

import longbridge.utils.NameValue;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


/**
 * Created by Fortune on 4/3/2017.
 */


@Controller
@RequestMapping("/ops/requests")
public class OpsServiceRequestController {


    @Autowired
    private CodeService codeService;

    @Autowired
    private OperationsUserService opsUserService;

    @Autowired
    private RequestService requestService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping
    public String getRequests() {
        return "/ops/request/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getRequests(DataTablesInput input,Principal principal) {
        OperationsUser opsUser = opsUserService.getUserByName(principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getRequests(opsUser,pageable);
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<ServiceRequestDTO>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }


    @GetMapping("/{reqId}/details")
    public String getRequest(Model model, @PathVariable Long reqId) {
        ServiceRequestDTO requestDTO = requestService.getRequest(reqId);

        ObjectMapper objectMapper = new ObjectMapper();
        List<NameValue> requestBody = null;
        try {
            requestBody = objectMapper.readValue(requestDTO.getBody(), new TypeReference<List<NameValue>>() {
            });
        } catch (Exception e) {
        }
        Iterable<CodeDTO> statusCodes = codeService.getCodesByType("REQUEST_STATUS");
        Iterable<RequestHistoryDTO> requestHistories = requestService.getRequestHistories(reqId);
        model.addAttribute("serviceRequest", requestDTO);
        model.addAttribute("histories", requestHistories);
        model.addAttribute("requestBody", requestBody);
        model.addAttribute("statuses", statusCodes);
        model.addAttribute("requestHistory", new RequestHistoryDTO());
        logger.info("Request Histories :{}", requestHistories.toString());
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


    @PostMapping("/history")
    public String createRequestHistory(@ModelAttribute("requestHistory") RequestHistoryDTO requestHistoryDTO, BindingResult result, Principal principal,RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "";
        }

        requestHistoryDTO.setCreatedBy(principal.getName());
        logger.info("RequestHistoryDTO received: {}", requestHistoryDTO);
        requestService.addRequestHistory(requestHistoryDTO);

        redirectAttributes.addFlashAttribute("message", "Request updated successfully");
        return "redirect:/ops/requests/" + requestHistoryDTO.getServiceRequestId() + "/details";
    }

    @PostMapping
    @ResponseBody
    public String processRequest(@RequestParam String jsonData) {

        logger.info("The received data: {}", jsonData);
        return "redirect:/ops/request/history/view";

    }


}
