package longbridge.controllers.retail;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.RetailUser;
import longbridge.servicerequests.client.AddRequestCmd;
import longbridge.servicerequests.client.RequestService;
import longbridge.servicerequests.client.ServiceRequestDTO;
import longbridge.servicerequests.config.RequestConfig;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.servicerequests.config.RequestField;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.RetailUserService;
import longbridge.utils.DataTablesUtils;
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
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/retail/requests")
public class RetailServiceRequestController {

    public static final String SUCCESSFULLY = "Submitted Successfully";
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
    private RetailUserService userService;


    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private AccountService accountService;

    //TODO :change or cache
    @GetMapping
    public String getServiceRequests(Model model) {
        model.addAttribute("requestList", requestConfigService.getRequestConfigs());
        return "cust/servicerequest/list";
    }

    @PostMapping
    public String processRequest(@ModelAttribute("requestDTO") AddRequestCmd cmd, @RequestParam Map<String, String> requestParams, HttpSession session, RedirectAttributes redirectAttributes, Locale locale) throws JsonProcessingException {
        RequestConfig requestConfig = requestConfigService.getRequestConfig(cmd.getServiceReqConfigId());
        Map<String, Object> body = requestParams.entrySet().stream().filter((k) -> !k.getKey().equals("serviceReqConfigId"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        cmd.setBody(body);
        try {

            if (requestConfig.isAuthRequired()) {
                if (session.getAttribute("authenticated") == null) {
                    session.setAttribute("requestDTO", cmd);
                    session.setAttribute("redirectURL", "/retail/requests/process");
                    return "redirect:/retail/token/authenticate";
                } else {
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                }
            }

            requestService.addRequest(cmd);
            redirectAttributes.addFlashAttribute("message", SUCCESSFULLY);

        } catch (InternetBankingException e) {
            logger.error("Service Request Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/retail/requests/" + requestConfig.getId();
        } catch (Exception e) {
            logger.error("Service Request Error", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
            return "redirect:/retail/requests/" + requestConfig.getId();
        }
        return "redirect:/retail/requests/track";

    }


    @GetMapping("/process")
    public String processRequest(HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        if (session.getAttribute("requestDTO") != null) {
            AddRequestCmd requestDTO = (AddRequestCmd) session.getAttribute("requestDTO");

            if (session.getAttribute("authenticated") != null) {

                try {
                    requestService.addRequest(requestDTO);
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", SUCCESSFULLY);
                } catch (InternetBankingException e) {
                    logger.error("Service Request Error", e);
                    model.addAttribute("failure", e.getMessage());
                    return "redirect:/retail/requests/" + requestDTO.getServiceReqConfigId();
                } catch (Exception e) {
                    logger.error("Service Request Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/retail/requests/" + requestDTO.getServiceReqConfigId();
                }
            }

        }
        return "redirect:/retail/requests/track";
    }

    @ModelAttribute("statuses")
    Map<String, String> getCodeMaps() {
        return codeService.getCodesByType("REQUEST_STATUS").stream()
                .collect(Collectors.toMap(CodeDTO::getCode, CodeDTO::getDescription));
    }


    @GetMapping("/{reqId}")
    public String makeRequest(@PathVariable Long reqId, Model model, Principal principal) {
        RetailUser user = userService.getUserByName(principal.getName());
        RequestConfig serviceReqConfig = requestConfigService.getRequestConfig(reqId);
        for (RequestField field : serviceReqConfig.getFields()) {
            switch (field.getType()) {
                case CODE:
                    model.addAttribute("codes", codeService.getCodesByType(field.getData()));
                    break;
                case ACCOUNT:
                    model.addAttribute("accts", accountService.getAccountsForDebit(user.getCustomerId()));
                    break;
                case LIST:
                    model.addAttribute("fixedList", field.getData().split(","));
            }
        }
        logger.debug("{}", serviceReqConfig);
        model.addAttribute("requestConfig", serviceReqConfig);
        return "cust/servicerequest/add";
    }

    @GetMapping("/track")
    public String trackRequests(Model model, Principal principal) {
        model.addAttribute("requestConfigInfo", requestConfigService.getRequestConfigs());
        return "cust/servicerequest/track";
    }

    //TODO
    @GetMapping(path = "/track/all")
    public @ResponseBody
    DataTablesOutput<ServiceRequestDTO> getUsers(DataTablesInput input, Principal principal) {
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
