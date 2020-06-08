package longbridge.controllers.retail;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.SettingDTO;
import longbridge.services.ChequeService;
import longbridge.services.CodeService;
import longbridge.services.ConfigurationService;
import longbridge.services.ServiceReqConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;

/**
 * Created by mac on 29/08/2018.
 */
@Controller
@RequestMapping("/retail/cheque")
public class ChequeController {

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
    ConfigurationService configurationService;

    @GetMapping("/chequebook")
    public String requestChequeBook( Model model) {

        Iterable<CodeDTO> chequebooks = codeService.getCodesByType("CHEQUEBOOK");
        Iterable<CodeDTO> pickUpBranch = codeService.getCodesByType("CMB_BRANCH");
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("CHEQUE-REQUEST");
        model.addAttribute("requestConfig", serviceReqConfig);

        model.addAttribute("leaves", chequebooks);
        model.addAttribute("branches", pickUpBranch);
        return "cust/cheque/chequebook";
    }

    @PostMapping("/balance/check")
    @ResponseBody
    public String checkBalance(WebRequest webRequest, Locale locale) {
        int charge = Integer.parseInt(webRequest.getParameter("charge"));
        String accountNumber = webRequest.getParameter("accountNumber");
       // logger.info("charge {}",charge);
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
        return "cust/cheque/stopcheque";
    }

    @GetMapping("/confirm")
    public String confirmCheque(Model model) {
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("CONFIRM-CHEQUE");
        model.addAttribute("requestConfig", serviceReqConfig);
        return "cust/cheque/confirmcheque";
    }

    @GetMapping("/draft")
    public String requestDraft(Model model,Locale locale) {
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("DRAFT-REQUEST");
        Iterable<CodeDTO> pickUpBranch = codeService.getCodesByType("CMB_BRANCH");
        SettingDTO draftCharge = configurationService.getSettingByName("DRAFT-REQUEST");

        model.addAttribute("requestConfig", serviceReqConfig);
        model.addAttribute("branches", pickUpBranch);
        model.addAttribute("draftMessage",messageSource.getMessage("cheque.draft",null,locale));
        model.addAttribute("charge",draftCharge.getValue());

        return "cust/cheque/draftrequest";
    }


}
