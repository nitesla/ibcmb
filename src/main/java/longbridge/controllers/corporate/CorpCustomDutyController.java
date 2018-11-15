package longbridge.controllers.corporate;

import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.TransferValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.Principal;

@Controller
@RequestMapping("/corporate/custom")
public class CorpCustomDutyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpCustomDutyController.class);

    @Autowired
    private CorpCustomDutyService customDutyService;

    @Value("060001")
    private String bankCode;

    private CorporateUserService corporateUserService;
    private CorpTransferService corpTransferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private TransferErrorService transferErrorService;
    private CorporateService corporateService;
    private TransferUtils transferUtils;

    @Autowired
    public CorpCustomDutyController(
            CorporateUserService corporateUserService, AccountService accountService, CorpTransferService corpTransferService,
             LocaleResolver localeResolver,
            FinancialInstitutionService financialInstitutionService, TransferValidator validator,
            TransferErrorService transferErrorService, CorporateService corporateService,
            TransferUtils transferUtils) {
        this.accountService = accountService;
        this.corporateUserService = corporateUserService;
        this.corpTransferService = corpTransferService;
        this.localeResolver = localeResolver;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.transferErrorService = transferErrorService;
        this.corporateService = corporateService;
        this.transferUtils = transferUtils;
    }

    @GetMapping
    public String getCustomDuty(Model model, Principal principal) {
        return "corp/custom/customduty";
    }

    @GetMapping("/payment")
    public String dutyPayment(Model model, Principal principal) {
        model.addAttribute("commands",getCustomsAreaCommands().getCommands());
        model.addAttribute("assessmentDetailsRequest",new CustomAssessmentDetailsRequest());
        model.addAttribute("paymentNotificationRequest",new CustomPaymentNotificationRequest());
        model.addAttribute("assessmentDetail",new CustomAssessmentDetail());
        return "corp/custom/custompayment";
    }

    @PostMapping("/accessment/details")
    @ResponseBody
    public CustomAssessmentDetail getAssessment(WebRequest webRequest,@ModelAttribute("assessmentDetailsRequest") @Valid CustomSADAsmt customSADAsmt){
        LOGGER.info("the assessmentDetailsRequest {}",customSADAsmt);
        return customDutyService.getAssessmentDetails(customSADAsmt);
    }

    @PostMapping("/commands")
    public CustomsAreaCommand getCustomsAreaCommands() {

        try {
            return customDutyService.getCustomsAreaCommands();
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return null;
    }

    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("assessmentDetail")  @Valid  CustomAssessmentDetail assessmentDetail,
                                  BindingResult result, Model model, HttpServletRequest servletRequest, Principal principal) {
        CorpPaymentRequest request = new CorpPaymentRequest();
        try {
        request.setAmount(new BigDecimal(
                assessmentDetail.getResponseInfo().getTotalAmount(), MathContext.DECIMAL64));
        request.setBeneficiaryAccountNumber(assessmentDetail.getAccount());
            FinancialInstitution financialInstitution =
            financialInstitutionService.getFinancialInstitutionByBankCode(
                    assessmentDetail.getResponseInfo().getBankCode().substring(2));
        request.setFinancialInstitution(financialInstitution);
        request.setCustomerAccountNumber(assessmentDetail.getAccount());
        request.setBeneficiaryAccountName(
                    accountService.getAccountByAccountNumber(assessmentDetail.getAccount()).getAccountName());
//            model.addAttribute("corpTransferRequest", request);
            request.setTransferType(TransferType.CUSTOM_DUTY);
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = user.getCorporate();

            if (corporate.getCorporateType().equalsIgnoreCase("MULTI")) {
                customDutyService.saveCustomPaymentRequestForAuthorization(request);
            } else if (corporate.getCorporateType().equalsIgnoreCase("SOLE")) {
                //customDutyService.makeBulkTransferRequest(bulkTransfer);
            } else {
                return "redirect:/login/corporate";
            }
        } catch (InternetBankingTransferException exception)
        {


        }
        return "";
    }

    @PostMapping("/payment")
    public void customPayment(
            @ModelAttribute("assessmentDetail")  @Valid  CustomAssessmentDetail assessmentDetail,
            @ModelAttribute("account") String account, BindingResult result){

        LOGGER.debug("Assessment Details: {}",assessmentDetail);
        boolean isAccountBalanceEnough = customDutyService.isAccountBalanceEnough(account,
                new BigDecimal(
                        assessmentDetail.getResponseInfo().getTotalAmount(), MathContext.DECIMAL64));
        if(!isAccountBalanceEnough){
            return;
        }
        CustomPaymentNotificationRequest paymentNotificationRequest = new CustomPaymentNotificationRequest();
        try {
            LOGGER.debug("making payment requests:{}",paymentNotificationRequest);
            try {
                transferUtils.validateTransferCriteria();
            } catch (InternetBankingTransferException e) {
                String errorMessage = transferErrorService.getMessage(e);
                //redirectAttributes.addFlashAttribute("failure", errorMessage);
            }
            customDutyService.paymentNotification(assessmentDetail);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
    }

    public CustomTransactionStatus paymentStatus(@ModelAttribute @Valid CustomTransactionStatus customTransactionStatus){

        try {
            return customDutyService.paymentStatus(customTransactionStatus);
        }
        catch (Exception e){
            LOGGER.error("Error calling coronation service rest service",e);
        }
        return null;

    }
}
