package longbridge.controllers.corporate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitutionType;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.NameValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/corporate/requests")
public class CorpServiceRequestController {
    @Autowired
    private RequestService requestService;

    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CodeService codeService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CorporateUserService userService;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private AccountService accountService;


    @GetMapping
    public String getServiceRequests(Model model) {
        Iterable<ServiceReqConfigDTO> requestList = serviceReqConfigService.getServiceReqConfigs();
        model.addAttribute("requestList", requestList);
        return "corp/servicerequest/list";
    }

    @PostMapping
    public String processRequest(@ModelAttribute("requestDTO") ServiceRequestDTO requestDTO, HttpSession session, Principal principal, RedirectAttributes redirectAttributes,Locale locale) {

        String requestBody = requestDTO.getRequestName();
        Long serviceReqConfigId = 0L;
        ObjectMapper objectMapper = new ObjectMapper();
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        try {
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(requestBody, new TypeReference<>() {
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

            CorporateUser user = userService.getUserByName(principal.getName());
            serviceRequestDTO.setBody(requestBody);
            serviceRequestDTO.setRequestStatus("S");
            serviceRequestDTO.setCorpId(user.getCorporate().getId());
            serviceRequestDTO.setDateRequested(new Date());

            if (serviceReqConfigDTO.isAuthenticate()) {
                if (session.getAttribute("authenticated") == null) {
                    session.setAttribute("requestDTO", serviceRequestDTO);
                    session.setAttribute("redirectURL", "/corporate/requests/process");
                    return "redirect:/corporate/token/authenticate";
                }
            }

            String message = requestService.addCorpRequest(serviceRequestDTO);
            redirectAttributes.addFlashAttribute("message", message);

        }

        catch (InternetBankingException e){
            logger.error("Service Request Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/corporate/requests/"+serviceReqConfigId;
        }
        catch (Exception e) {
            logger.error("Service Request Error", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
            return "redirect:/corporate/requests/"+serviceReqConfigId;
        }
        return "redirect:/corporate/requests/track";

    }



    @GetMapping("/process")
    public String processRequest(HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale){

        if(session.getAttribute("requestDTO")!=null){
            ServiceRequestDTO requestDTO = (ServiceRequestDTO)session.getAttribute("requestDTO");

            if(session.getAttribute("authenticated")!=null){

                try {
                    String message = requestService.addCorpRequest(requestDTO);
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                }
                catch (InternetBankingException e){
                    logger.error("Service Request Error", e);
                    model.addAttribute("failure", e.getMessage());
                    return "redirect:/corporate/requests/"+requestDTO.getServiceReqConfigId();
                } catch (Exception e) {
                    logger.error("Service Request Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/corporate/requests/"+requestDTO.getServiceReqConfigId();
                }
            }

        }
        return "redirect:/corporate/requests/track";
    }

    @GetMapping("/{reqId}")
    public String makeRequest(@PathVariable Long reqId, Model model, Principal principal) {
        CorporateUser user = userService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(user.getCorporate().getId());
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfig(reqId);
        for (ServiceReqFormFieldDTO field : serviceReqConfig.getFormFields()) {
            if (field.getFieldType() != null && field.getFieldType().equals("CODE")) {
                List<CodeDTO> codeList = codeService.getCodesByType(field.getTypeData());
                model.addAttribute("codes", codeList);
            }

            if (field.getFieldType() != null && field.getFieldType().equals("ACCT")) {
                List<AccountDTO> acctList = accountService.getAccountsForDebitAndCredit(corporate.getCustomerId());
                model.addAttribute("accts", acctList);
            }

            if (field.getFieldType() != null && field.getFieldType().equals("FI")) {
                List<FinancialInstitutionDTO> fiList = financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL);
                model.addAttribute("banks", fiList);
            }

            if (field.getFieldType() != null && field.getFieldType().equals("LIST")) {
                String list = field.getTypeData();
                String[] myList = list.split(",");
                model.addAttribute("fixedList", myList);
            }
        }
        System.out.println(serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "corp/servicerequest/add";
    }

    @GetMapping("/track")
    public String trackRequests(Model model, Principal principal){
        return "corp/servicerequest/track";
    }

    @GetMapping(path = "/track/all")
    public @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getUsers(DataTablesInput input, Principal principal){
        CorporateUser user = userService.getUserByName(principal.getName());
        Corporate corporate = user.getCorporate();
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getRequests(corporate, pageable);
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }
}
