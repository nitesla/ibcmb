package longbridge.controllers.retail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 * Created by Fortune on 4/5/2017.
 */

@Controller
@RequestMapping("/retail/requests")
public class ServiceRequestController {

    private MessageSource messageSource;
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
    public String processRequest(@ModelAttribute("requestDTO") ServiceRequestDTO requestDTO, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpSession session) {

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

            if(serviceReqConfigDTO.isAuthenticate()){
                if(session.getAttribute("authenticated")!="authenticated"){
                    session.setAttribute("requestDTO",serviceRequestDTO);
                    session.setAttribute("redirectURL", "/retail/request/process");
                    return "redirect:/retail/token/authenticate";
                }
            }
            String message = requestService.addRequest(serviceRequestDTO);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException e){
            logger.error("Service Request Error", e);
            model.addAttribute("failure", e.getMessage());
            return "cust/servicerequest/add";
        } catch (Exception e) {
            logger.error("Service Request Error", e);
            model.addAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
            return "cust/servicerequest/add";
        }
        return "redirect:/retail/requests/track";

    }

    @GetMapping("/request/process")
    public String processRequest(HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale){

        if(session.getAttribute("requestDTO")!=null){
            ServiceRequestDTO requestDTO = (ServiceRequestDTO)session.getAttribute("requestDTO");

            if(session.getAttribute("authenticated")!="authenticated"){

                try {
                    String message = requestService.addRequest(requestDTO);
                    redirectAttributes.addFlashAttribute("message", message);
                }
            catch (InternetBankingException e){
                logger.error("Service Request Error", e);
                model.addAttribute("failure", e.getMessage());
                return "cust/servicerequest/add";
            } catch (Exception e) {
                logger.error("Service Request Error", e);
                model.addAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                return "cust/servicerequest/add";
            }
            }

        }
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
