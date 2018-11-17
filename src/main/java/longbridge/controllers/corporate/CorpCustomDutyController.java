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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/assessment/details")
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
//        CorpPaymentRequest request = new CorpPaymentRequest();
        try {
//            request.setAmount(new BigDecimal(
//                    assessmentDetail.getResponseInfo().getTotalAmount(), MathContext.DECIMAL64));
//            request.setBeneficiaryAccountNumber(assessmentDetail.getAccount());
//                FinancialInstitution financialInstitution =
//                financialInstitutionService.getFinancialInstitutionByBankCode(
//                        assessmentDetail.getResponseInfo().getBankCode().substring(2));
//            request.setFinancialInstitution(financialInstitution);
//            request.setCustomerAccountNumber(assessmentDetail.getAccount());
//            request.setBeneficiaryAccountName(
//                        accountService.getAccountByAccountNumber(assessmentDetail.getAccount()).getAccountName());
//    //            model.addAttribute("corpTransferRequest", request);
//            request.setTransferType(TransferType.CUSTOM_DUTY);
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = user.getCorporate();
//            request.setCorporate(corporate);

//            CustomDutyPayment customDutyPayment = new CustomDutyPayment();
//            customDutyPayment.setTotalAmount(request.getAmount());
//            customDutyPayment.setBankCode(assessmentDetail.getResponseInfo().getBankCode());
//            customDutyPayment.setCompanyCode(assessmentDetail.getResponseInfo().getCompanyCode());
//            customDutyPayment.setDeclarantCode(assessmentDetail.getResponseInfo().getDeclarantCode());
//            customDutyPayment.setDeclarantName(assessmentDetail.getResponseInfo().getDeclarantName());
//            customDutyPayment.setCompanyName(assessmentDetail.getResponseInfo().getCompanyName());
//            customDutyPayment.setApprovalStatus(assessmentDetail.getResponseInfo().getApprovalStatus());
//            customDutyPayment.setApprovalStatusDescription(assessmentDetail.getResponseInfo().getApprovalStatusDescription());
//            customDutyPayment.setCollectionAccount(assessmentDetail.getResponseInfo().getCollectionAccount());
//            customDutyPayment.setFormMNumber(assessmentDetail.getResponseInfo().getFormMNumber());
//            customDutyPayment.setSGDAssessmentDate(assessmentDetail.getResponseInfo().getSGDAssessmentDate());
//            //customDutyPayment.setSGDAssessment(assessmentDetail.);
//            customDutyPayment.setTranId(assessmentDetail.getResponseInfo().getTranId());
//            customDutyPayment.setAccount(assessmentDetail.getAccount());
//            customDutyPayment.setCode(assessmentDetail.getCode());
//            customDutyPayment.setMessage(assessmentDetail.getMessage());
//            request.setCustomDutyPayment(customDutyPayment);
            //customDutyPayment.setCommandDutyArea();
            if (corporate.getCorporateType().equalsIgnoreCase("MULTI")) {
                customDutyService.saveCustomPaymentRequestForAuthorization(assessmentDetail,principal,corporate);
            } else if (corporate.getCorporateType().equalsIgnoreCase("SOLE")) {
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

    @GetMapping("/requests")
    public
    @ResponseBody
    DataTablesOutput<CorpPaymentRequest> getCustomPaymentRequests(DataTablesInput input){
        LOGGER.debug("Fetching requests:{}",input);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpPaymentRequest> requests = customDutyService.getPaymentRequests(pageable);
        DataTablesOutput<CorpPaymentRequest> out = new DataTablesOutput<CorpPaymentRequest>();
        LOGGER.debug("Fetching requests:{}",out);
        out.setDraw(input.getDraw());
        out.setData(requests.getContent());
        out.setRecordsFiltered(requests.getTotalElements());
        out.setRecordsTotal(requests.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/authorizations")
    public String getAuthorizations(@PathVariable Long id, ModelMap modelMap) {

        CorpPaymentRequest corpPaymentRequest = customDutyService.getPayment(id);
        CorpTransferAuth corpTransferAuth = customDutyService.getAuthorizations(corpPaymentRequest);
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(corpPaymentRequest);
        boolean userCanAuthorize = customDutyService.userCanAuthorize(corpPaymentRequest);
        modelMap.addAttribute("authorizationMap", corpTransferAuth)
                .addAttribute("corpTransRequest", corpPaymentRequest)
                .addAttribute("corpTransReqEntry", new CorpTransReqEntry())
                .addAttribute("corpTransRule", corpTransRule)
                .addAttribute("userCanAuthorize", userCanAuthorize);

        List<CorporateRole> rolesNotInAuthList = new ArrayList<>();
        List<CorporateRole> rolesInAuth = new ArrayList<>();

        if (corpTransferAuth.getAuths() != null) {
            for (CorpTransReqEntry transReqEntry : corpTransferAuth.getAuths()) {
                rolesInAuth.add(transReqEntry.getRole());
            }
        }

        if (corpTransRule != null) {
            for (CorporateRole role : corpTransRule.getRoles()) {
                if (!rolesInAuth.contains(role)) {
                    rolesNotInAuthList.add(role);
                }
            }
        }
        LOGGER.info("Roles not In Auth List..{}", rolesNotInAuthList.toString());
        modelMap.addAttribute("rolesNotAuth", rolesNotInAuthList);

        return "corp/transfer/request/summary";
    }
}
