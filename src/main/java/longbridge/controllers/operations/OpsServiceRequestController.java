package longbridge.controllers.operations;

import longbridge.dtos.CodeDTO;
import longbridge.models.OperationsUser;
import longbridge.servicerequests.client.AddCommentCmd;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;


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
        OperationsUser opsUser = opsUserService.getUserByName(principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getOpRequests(opsUser, pageable);
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }


    @GetMapping("/{reqId}/details")
    public String getRequest(Model model, @PathVariable Long reqId) {
        model.addAttribute("request", requestService.getRequest(reqId));
        return "/ops/request/details";

    }

    @ModelAttribute("statuses")
    Map<String, String> getCodeMaps(){
        return codeService.getCodesByType("REQUEST_STATUS").stream()
                .collect(Collectors.toMap(CodeDTO::getCode, CodeDTO::getDescription));
    }



    @ResponseBody
    @PostMapping
    public ResponseEntity<ServiceRequestDTO.CommentDTO> commentOnRequest(@RequestBody AddCommentCmd cmd) {
        try {
            return ResponseEntity.ok(requestService.addRequestComment(cmd));
        }catch (Exception e){
            logger.error("Error modifying request {} with {}",cmd,e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
