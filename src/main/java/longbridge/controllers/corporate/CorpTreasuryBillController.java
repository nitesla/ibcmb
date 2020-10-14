package longbridge.controllers.corporate;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.InvestmentRate;
import longbridge.services.CodeService;
import longbridge.services.InvestmentRateService;
import longbridge.services.ServiceReqConfigService;
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
    @RequestMapping("/corporate/treasurybill")
    public class CorpTreasuryBillController {

        @Autowired
        private ServiceReqConfigService serviceReqConfigService;
        @Autowired
        private CodeService codeService;

        @Autowired
        private InvestmentRateService investmentRateService;

        @Autowired
        MessageSource messageSource;

        @Autowired
        TreasurybillService treasurybillService;

        private final Logger logger= LoggerFactory.getLogger(this.getClass());


        @GetMapping("/view")
        public String viewTreasuryBills() {

            return "corp/treasurybills/view";
        }
        /*@GetMapping("/details")
        @ResponseBody
        public DataTablesOutput<FixedDepositDTO> getStatementDataByState(DataTablesInput input, Principal principal) {
    //       logger.info("the username  {}",principal.getName());
            Pageable pageable = DataTablesUtils.getPageable(input);
            Page<FixedDepositDTO> fixedDepositDTOS = null;
            fixedDepositDTOS = fixedDepositService.getFixedDepositDetials(principal.getName(),pageable);
            DataTablesOutput<FixedDepositDTO> out = new DataTablesOutput<FixedDepositDTO>();
            out.setDraw(input.getDraw());
            out.setData(fixedDepositDTOS.getContent());
            out.setRecordsFiltered(fixedDepositDTOS.getTotalElements());
            out.setRecordsTotal(fixedDepositDTOS.getTotalElements());
            return out;
        }*/
        @GetMapping("/new")
        public String newTreasuryBill(Model model, Locale locale) {

            Iterable<CodeDTO> marketType = codeService.getCodesByType("MARKET-TYPE");
            Iterable<InvestmentRate> primaryRates = investmentRateService.getInvestmentRateByInvestmentName("TREASURY-BILLS-PRIMARY");
            Iterable<InvestmentRate> secondaryRates = investmentRateService.getInvestmentRateByInvestmentName("TREASURY-BILLS-SECONDARY");
            ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("TREASURY-BILL");

            model.addAttribute("primary",primaryRates);
            model.addAttribute("secondary",secondaryRates);
            model.addAttribute("markets",marketType);
            model.addAttribute("requestConfig", serviceReqConfig);
            model.addAttribute("requestDTO", new ServiceRequestDTO());
            return "corp/treasurybills/new";
        }

        @PostMapping("/balance/check")
        @ResponseBody
        public String checkBalance(WebRequest webRequest, Locale locale) {
            int amount = Integer.parseInt(webRequest.getParameter("amount"));
            String accountNumber = webRequest.getParameter("accountNumber");
            logger.info("amount {}",amount);
            if (!treasurybillService.isBalanceOk(amount, accountNumber)) {

                return messageSource.getMessage("deposit.balance.insufficient", null, locale);
            } else {
                return "";
            }

        }

}
