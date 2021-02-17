package longbridge.controllers.retail;

import longbridge.servicerequests.client.ServiceRequestDTO;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.services.CodeService;
import longbridge.services.InvestmentRateService;
import longbridge.services.TreasurybillService;
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


@Controller
@RequestMapping("/retail/treasurybill")
public class TreasuryBillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    MessageSource messageSource;
    @Autowired
    TreasurybillService treasurybillService;
    @Autowired
    private RequestConfigService requestConfigService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private InvestmentRateService investmentRateService;

    @GetMapping("/view")
    public String viewTreasuryBills() {

        return "cust/treasurybills/view";
    }

    @GetMapping("/new")
    public String newTreasuryBill(Model model, Locale locale) {


        model.addAttribute("primary", investmentRateService.getInvestmentRateByInvestmentName("TREASURY-BILLS-PRIMARY"));
        model.addAttribute("secondary", investmentRateService.getInvestmentRateByInvestmentName("TREASURY-BILLS-SECONDARY"));
        model.addAttribute("markets", codeService.getCodesByType("MARKET-TYPE"));
        model.addAttribute("requestConfig", requestConfigService.getRequestConfigByName("TREASURY-BILL"));
        model.addAttribute("requestDTO", new ServiceRequestDTO());
        return "cust/treasurybills/new";
    }

    @PostMapping("/balance/check")
    @ResponseBody
    public String checkBalance(WebRequest webRequest, Locale locale) {
        int amount = Integer.parseInt(webRequest.getParameter("amount"));
        String accountNumber = webRequest.getParameter("accountNumber");
        logger.info("amount {}", amount);
        if (!treasurybillService.isBalanceOk(amount, accountNumber)) {

            return messageSource.getMessage("deposit.balance.insufficient", null, locale);
        } else {
            return "";
        }

    }

}
