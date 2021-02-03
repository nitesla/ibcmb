package longbridge.controllers.corporate;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.SettingDTO;
import longbridge.services.ChequeService;
import longbridge.services.CodeService;
import longbridge.services.ServiceReqConfigService;
import longbridge.services.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/corporate/cheque")
public class CorpChequeController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CodeService codeService;

    @Autowired
    ChequeService chequeService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ServiceReqConfigService serviceReqConfigService;
    @Autowired
    SettingsService configurationService;


    @GetMapping("/chequebook")
    public String requestChequeBook(Principal principal, Model model) {
        List<CodeDTO> chequebooks = codeService.getCodesByType("CHEQUEBOOK");
        List<CodeDTO> pickUpBranch = codeService.getCodesByType("BANK_BRANCH");
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("CHEQUE-REQUEST");
        model.addAttribute("requestConfig", serviceReqConfig);
        model.addAttribute("leaves", chequebooks);
        model.addAttribute("branches", pickUpBranch);
        return "corp/cheque/chequebook";
    }



    @PostMapping("/balance/check")
    @ResponseBody
    public String checkBalance(WebRequest webRequest, Locale locale) {
        int charge = Integer.parseInt(webRequest.getParameter("charge"));
        String accountNumber = webRequest.getParameter("accountNumber");

        if (!chequeService.isBalanceOk(charge, accountNumber)) {

            return messageSource.getMessage("deposit.balance.insufficient", null, locale);
        } else {
            return "";
        }

    }

    @GetMapping("/stop")
    public String stopCheque(Model model,Locale locale) {

        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("STOP-CHEQUE");
        SettingDTO stopChequeCharge = configurationService.getSettingByName("STOP-CHEQUE");

        model.addAttribute("requestConfig", serviceReqConfig);
        model.addAttribute("chargeMessage",messageSource.getMessage("stop.cheque.charge",null,locale));
        model.addAttribute("charge",stopChequeCharge.getValue());
        return "corp/cheque/stopcheque";
    }

    @GetMapping("/confirm")
    public String confirmCheque(Model model) {
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("CONFIRM-CHEQUE");
        model.addAttribute("requestConfig", serviceReqConfig);
        return "corp/cheque/confirmcheque";
    }

    @GetMapping("/draft")
    public String requestDraft(Model model,Locale locale) {
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("DRAFT-REQUEST");
        Iterable<CodeDTO> pickUpBranch = codeService.getCodesByType("BANK_BRANCH");
        SettingDTO draftCharge = configurationService.getSettingByName("DRAFT-REQUEST");

        model.addAttribute("requestConfig", serviceReqConfig);
        model.addAttribute("branches", pickUpBranch);
        model.addAttribute("draftMessage",messageSource.getMessage("cheque.draft",null,locale));
        model.addAttribute("charge",draftCharge.getValue());

        return "corp/cheque/draftrequest";
    }













    @Autowired
    CorpServiceRequestController serviceRequestController;
    @GetMapping("/{reqId}")
    public String reDirectRequest(@PathVariable Long reqId, Model model, Principal principal){
        return serviceRequestController.makeRequest(reqId,model,principal);
//        return "redirect:/retail/requests/"+id;

    }
}
