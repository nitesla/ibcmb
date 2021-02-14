package longbridge.controllers.customer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitutionType;
import longbridge.servicerequests.client.AddRequestCmd;
import longbridge.servicerequests.client.RequestService;
import longbridge.servicerequests.client.ServiceRequestDTO;
import longbridge.servicerequests.config.RequestConfig;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.servicerequests.config.RequestField;
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


@Controller
public class CustomerServiceRequestController {
    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestConfigService requestConfigService;

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

//TODO :change or cache
    @GetMapping
    public String getServiceRequests(Model model) {
        model.addAttribute("requestList", requestConfigService.getRequestConfigs());
        return "corp/servicerequest/list";
    }

    @PostMapping
    public String processRequest(@ModelAttribute("requestDTO") AddRequestCmd cmd, HttpSession session, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        RequestConfig requestConfig = requestConfigService.getRequestConfig(cmd.getServiceReqConfigId());
        try {

            if (requestConfig.isAuthRequired()) {
                if (session.getAttribute("authenticated") == null) {
                    session.setAttribute("requestDTO", cmd);
                    session.setAttribute("redirectURL", "/corporate/requests");
                    return "redirect:/corporate/token/authenticate";
                }else{
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                }
            }

            requestService.addRequest(cmd);
            redirectAttributes.addFlashAttribute("message", "Submitted Successfully");

        }

        catch (InternetBankingException e){
            logger.error("Service Request Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/corporate/requests/"+requestConfig.getId();
        }
        catch (Exception e) {
            logger.error("Service Request Error", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
            return "redirect:/corporate/requests/"+ requestConfig.getId();
        }
        return "redirect:/corporate/requests/track";

    }




    @GetMapping("/{reqId}")
    public String makeRequest(@PathVariable Long reqId, Model model, Principal principal) {
        CorporateUser user = userService.getUserByName(principal.getName());
        CorporateDTO corporate = corporateService.getCorporate(user.getCorporate().getId());
        RequestConfig serviceReqConfig = requestConfigService.getRequestConfig(reqId);
        for (RequestField field : serviceReqConfig.getFields()) {
            switch (field.getType()){
                case CODE:
                    model.addAttribute("codes", codeService.getCodesByType(field.getData()));
                    break;
                case ACCOUNT:
                    model.addAttribute("accts", accountService.getAccountsForDebitAndCredit(corporate.getCustomerId()));
                    break;
                case LIST:
                    model.addAttribute("fixedList",field.getData().split(","));
            }
        }
       logger.debug("{}",serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "corp/servicerequest/add";
    }

    @GetMapping("/track")
    public String trackRequests(Model model, Principal principal){
        return "corp/servicerequest/track";
    }

    //TODO
    @GetMapping(path = "/track/all")
    public @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getUsers(DataTablesInput input, Principal principal){
        CorporateUser user = userService.getUserByName(principal.getName());
        Corporate corporate = user.getCorporate();
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ServiceRequestDTO> serviceRequests = requestService.getUserRequests(pageable);
        DataTablesOutput<ServiceRequestDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(serviceRequests.getContent());
        out.setRecordsFiltered(serviceRequests.getTotalElements());
        out.setRecordsTotal(serviceRequests.getTotalElements());
        return out;
    }
}
