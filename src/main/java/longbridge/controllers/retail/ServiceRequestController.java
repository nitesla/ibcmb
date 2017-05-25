package longbridge.controllers.retail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.*;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.services.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */

@Controller
@RequestMapping("/retail/requests")
public class ServiceRequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    @Autowired
    private CodeService codeService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserService userService;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private AccountService accountService;

    //private RetailUser retailUser = new RetailUser();//TODO user must be authenticated

    @GetMapping
    public String getServiceRequests(Model model) {
        Iterable<ServiceReqConfigDTO> requestList = serviceReqConfigService.getServiceReqConfigs();
        model.addAttribute("requestList", requestList);
        return "cust/servicerequest/list";
    }

    @PostMapping
    public String processRequest(@ModelAttribute("requestDTO") ServiceRequestDTO requestDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes) {

        String requestBody = requestDTO.getRequestName();
        Long serviceReqConfigId = 0L;
        ObjectMapper objectMapper = new ObjectMapper();
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        try {
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(requestBody, new TypeReference<ArrayList<NameValue>>() {
            });
            Iterator<NameValue> iterator = myFormObjects.iterator();
            while (iterator.hasNext()) {
                NameValue nameValue = iterator.next();
                String name = nameValue.getName();
                String value = nameValue.getValue();
                if (name.equals("requestName")) {
                    serviceRequestDTO.setRequestName(value);
                    iterator.remove();
                }
                if (name.equals("serviceReqConfigId")) {
                    serviceReqConfigId = Long.parseLong(nameValue.getValue());
                    serviceRequestDTO.setServiceReqConfigId(serviceReqConfigId);
                    iterator.remove();
                }
            }
            ServiceReqConfigDTO serviceReqConfigDTO = serviceReqConfigService.getServiceReqConfig(serviceReqConfigId);
            List<ServiceReqFormFieldDTO> formFieldDTOs = serviceReqConfigDTO.getFormFields();

            if (myFormObjects.size() == formFieldDTOs.size()) {
                int num = myFormObjects.size();

                for (int i = 0; i < num; i++) {
                    if (myFormObjects.get(i).getName().equals(formFieldDTOs.get(i).getFieldName())) {
                        myFormObjects.get(i).setName(formFieldDTOs.get(i).getFieldLabel());
                    }
                }
            }

            requestBody = objectMapper.writeValueAsString(myFormObjects);

            RetailUser user = userService.getUserByName(principal.getName());
            serviceRequestDTO.setBody(requestBody);
            serviceRequestDTO.setRequestStatus("S");
            serviceRequestDTO.setUserId(user.getId());
            serviceRequestDTO.setDateRequested(new Date());
            requestService.addRequest(serviceRequestDTO);

        } catch (Exception e) {
            logger.error("Could not process the request: {}",e.toString());
        }
        redirectAttributes.addFlashAttribute("message", "Request sent successfully");
        return "redirect:/retail/requests/track";

    }

    @GetMapping("/{reqId}")
    public String makeRequest(@PathVariable Long reqId, Model model, Principal principal) {
        RetailUser user = userService.getUserByName(principal.getName());
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        for (ServiceReqFormFieldDTO field : serviceReqConfig.getFormFields()) {
            if (field.getFieldType() != null && field.getFieldType().equals("CODE")) {
                List<CodeDTO> codeList = codeService.getCodesByType(field.getTypeData());
                model.addAttribute("codes", codeList);
            }

            if (field.getFieldType() != null && field.getFieldType().equals("ACCOUNT")) {
                List<AccountDTO> acctList = accountService.getAccountsForDebitAndCredit(user.getCustomerId());
                model.addAttribute("accts", acctList);
            }

            if (field.getFieldType() != null && field.getFieldType().equals("FI")) {
                List<FinancialInstitutionDTO> fiList = financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL);
                model.addAttribute("banks", fiList);
            }

            if (field.getFieldType() != null && field.getFieldType().equals("LIST")) {
                String list = field.getTypeData();
                String myList[] = list.split(",");
                model.addAttribute("fixedList", myList);
            }
        }
        System.out.println(serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "cust/servicerequest/add";
    }

    @GetMapping("/track")
    public String trackRequests(Model model, Principal principal){
//        RetailUser user = userService.getUserByName(principal.getName());
//        Iterable<ServiceRequestDTO> serviceRequests = requestService.getRequests(user);
//        model.addAttribute("requests", serviceRequests);
        return "cust/servicerequest/track";
    }

    @GetMapping(path = "/track/all")
    public @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getUsers(DataTablesInput input, Principal principal){
        RetailUser user = userService.getUserByName(principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getRequests(user, pageable);
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<ServiceRequestDTO>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }



}
