package longbridge.controllers.corporate;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/corporate/custom")
public class CorpCustomDutyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpCustomDutyController.class);

    @Autowired
    private CorpCustomDutyService customDutyService;

    @Value("${bank.code}")
    private String bankCode;

    private final CorporateUserService corporateUserService;
    private final TransferErrorService transferErrorService;
    private final CorporateService corporateService;
    private final TransferUtils transferUtils;
    private final SecurityService securityService;
    @Autowired
    private SettingsService configService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    public CorpCustomDutyController(CorporateUserService corporateUserService,
            TransferErrorService transferErrorService, CorporateService corporateService,
            TransferUtils transferUtils,SecurityService securityService) {
        this.corporateUserService = corporateUserService;
        this.transferErrorService = transferErrorService;
        this.corporateService = corporateService;
        this.transferUtils = transferUtils;
        this.securityService = securityService;
    }

    @GetMapping
    public String getCustomDuty(Model model, Principal principal) {

        LOGGER.info("bankcode {}",bankCode);
        return "corp/custom/customduty";
    }

    @GetMapping("/payment")
    public String dutyPayment(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        try {
            transferUtils.validateTransferCriteria();
        } catch (InternetBankingTransferException e)
        {
            String errorMessage = transferErrorService.getMessage(e);
            model.addAttribute("failure", errorMessage);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/custom";
        }
        model.addAttribute("commands",getCustomsAreaCommands().getCommands());
        model.addAttribute("assessmentDetailsRequest",new CustomAssessmentDetailsRequest());
        model.addAttribute("paymentNotificationRequest",new CustomPaymentNotificationRequest());
        model.addAttribute("assessmentDetail",new CustomAssessmentDetail());
        model.addAttribute("corpTransReqEntry", new CorpTransReqEntry());
        model.addAttribute("taxDetails", "");
        model.addAttribute("customsCode", "");
        return "corp/custom/custompayment";
    }

    @PostMapping("/assessment/details")
    @ResponseBody
    public CustomAssessmentDetail getAssessment(WebRequest webRequest,@ModelAttribute("assessmentDetailsRequest") @Valid CustomSADAsmt customSADAsmt){
        String customCode = webRequest.getParameter("CommandCode");
        LOGGER.info("the assessmentDetailsRequest {}",customSADAsmt);
        return customDutyService.getAssessmentDetails(customSADAsmt,customCode);
    }

    @PostMapping("/commands")
    public CustomsAreaCommand getCustomsAreaCommands() {

        try {
            return customDutyService.getCustomsAreaCommands();
        }
        catch (Exception e){
            LOGGER.error("Error calling bank service rest service",e);
        }
        return new CustomsAreaCommand();
    }

    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("assessmentDetail")  @Valid  CustomAssessmentDetail assessmentDetail,
                                  @ModelAttribute("assessmentDetailsRequest")  @Valid  CustomAssessmentDetailsRequest assessmentDetailsRequest,
                                  WebRequest request,
                                  BindingResult result, Model model, HttpServletRequest servletRequest, Principal principal,RedirectAttributes redirectAttributes, Locale locale) {
       LOGGER.info("custom here{}");
        String responseMessage = "";
        String tokenCode = request.getParameter("TaxDetails");
        String mcommand = request.getParameter("mcommand");
        assessmentDetailsRequest.setCustomsCode(mcommand);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Tax> navigation = new ArrayList<>();
        try {
           navigation = objectMapper.readValue(tokenCode,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class, Tax.class));
            assessmentDetail.getResponseInfo().setTaxDetails(navigation);
        }catch(Exception e){

        }
        try {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = user.getCorporate();
            assessmentDetailsRequest.setCustomsCode(mcommand);
            if (corporate.getCorporateType().equalsIgnoreCase("MULTI")) {
                responseMessage = customDutyService.saveCustomPaymentRequestForAuthorization(assessmentDetail,assessmentDetailsRequest, principal,corporate);
                redirectAttributes.addFlashAttribute("message",responseMessage);
            } else if (corporate.getCorporateType().equalsIgnoreCase("SOLE")) {

                CustomDutyPayment customDutyPayment = customDutyService.saveCustomDutyPayment(assessmentDetail, assessmentDetailsRequest,principal);
                CorpPaymentRequest resp = customDutyService.saveCorpPaymentRequest(customDutyPayment, corporate,principal,true);
                redirectAttributes.addFlashAttribute("message",resp.getStatusDescription());
            } else {
                return "redirect:/corporate/custom";
            }
        } catch (InternetBankingException exception )
        {
            redirectAttributes.addFlashAttribute("failure",messageSource.getMessage(exception.getMessage(),null,locale));

        }
        return "redirect:/corporate/custom";
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

            }
        }
        catch (Exception e){
            LOGGER.error("Error calling bank service rest service",e);
        }
    }
    @GetMapping("/{id}/refresh")
    @ResponseBody
    public CustomTransactionStatus paymentStatus(@PathVariable Long id){

        try {
            LOGGER.info("the is {}",id);
            return customDutyService.updatePayamentStatus(id);
        }
        catch (Exception e){
            LOGGER.error("Error calling bank service rest service",e);
        }
        return null;
    }

    @GetMapping("/{id}/payment")
    @ResponseBody
    public String payment(@PathVariable Long id, Principal principal){

        CorpPaymentRequest corpPaymentRequest = customDutyService.getPayment(id);
//        CustomDutyPayment dutyPayment = corpPaymentRequest.getCustomDutyPayment();
//        LOGGER.info("dutyPayment:{}",dutyPayment);
        try {
            LOGGER.info("the id {}",id);
            return customDutyService.opsMakeCustomDutyPayment(corpPaymentRequest,principal);
        }
        catch (Exception e){
            LOGGER.error("Error calling bank service rest service",e);
        }
        return "";
    }

    @GetMapping("/requests")
    public
    @ResponseBody
    DataTablesOutput<CorpPaymentRequest> getCustomPaymentRequests(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpPaymentRequest> requests = customDutyService.getPaymentRequests(pageable);
        DataTablesOutput<CorpPaymentRequest> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(requests.getContent());
        out.setRecordsFiltered(requests.getTotalElements());
        out.setRecordsTotal(requests.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/authorizations")
    public String getAuthorizations(@PathVariable Long id, ModelMap modelMap) {
        CorpPaymentRequest corpPaymentRequest = customDutyService.getPayment(id);
        CustomDutyPayment dutyPayment = corpPaymentRequest.getCustomDutyPayment();
        LOGGER.info("dutyPayment:{}",dutyPayment);
        CorpTransferAuth corpTransferAuth = customDutyService.getAuthorizations(corpPaymentRequest);
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(corpPaymentRequest);

        boolean userCanAuthorize = customDutyService.userCanAuthorize(corpPaymentRequest);
        modelMap.addAttribute("authorizationMap", corpTransferAuth)
                .addAttribute("corpTransRequest", corpPaymentRequest)
                .addAttribute("corpTransReqEntry", new CorpTransReqEntry())
                .addAttribute("corpTransRule", corpTransRule)
                .addAttribute("dutyPayment", dutyPayment)
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
        LOGGER.info("Roles not In Auth List..{}", rolesNotInAuthList);
        modelMap.addAttribute("rolesNotAuth", rolesNotInAuthList);

        return "corp/custom/approval";
    }

    @PostMapping("/authorize")
    public String authorization(@ModelAttribute("corpTransReqEntry") CorpTransReqEntry corpTransReqEntry, @RequestParam("token") String tokenCode, RedirectAttributes redirectAttributes, Principal principal, Locale locale) {

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if (setting != null && setting.isEnabled()) {

            if (tokenCode != null && !tokenCode.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                    if (!result) {
                        LOGGER.error("Error authenticating token");
                        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                        return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/view";
                    }
                } catch (InternetBankingSecurityException se) {
                    LOGGER.error("Error authenticating token");
                    redirectAttributes.addFlashAttribute("failure", se.getMessage());
                    return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/view";
                }
            } else {
                redirectAttributes.addFlashAttribute("failure", "Token code is required");
                return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/view";
            }
        }

        try {
            String message = customDutyService.addAuthorization(corpTransReqEntry, principal);
            LOGGER.info("corpTransReqEntry:{}",corpTransReqEntry);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            LOGGER.error("Failed to authorize transfer", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/corporate/custom";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<CorpPaymentRequest> getAllEntities(@RequestParam("selectedStatus") String selectedStatus, DataTablesInput input, @RequestParam("csearch") String search){
        LOGGER.info("Searching by:{}",selectedStatus);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpPaymentRequest> paymentRequest= null;
        if (StringUtils.isNoneBlank(search)) {
            LOGGER.info("the search param {}",search);
            paymentRequest = customDutyService.findEntities(selectedStatus,search,pageable);
        }else{
            LOGGER.info("no search query");
            paymentRequest= customDutyService.getPaymentRequests(pageable);
        }
        DataTablesOutput<CorpPaymentRequest> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(paymentRequest.getContent());
        out.setRecordsFiltered(paymentRequest.getTotalElements());
        out.setRecordsTotal(paymentRequest.getTotalElements());
        return out;
    }

    @GetMapping("/{tranId}/receipt")
    public String generateReceipy(@PathVariable String tranId, Model model){
        LOGGER.info("the tranid {}",tranId);
        String receiptInString = integrationService.getReciept(tranId);
        String result = StringUtils.substringBetween(receiptInString, "<html>", "</html>");

        model.addAttribute("receiptPage", result);
        return "corp/custom/receipt";
    }

    @GetMapping("/{account}/{amount}/checkbal")
    public
    @ResponseBody
    String getBalStatusMessage(@PathVariable String account,@PathVariable String amount){

        if(!customDutyService.isAccountBalanceEnough(account,new BigDecimal(amount))) {
            return messageSource.getMessage("message.insufficient", null, null);
        }else return "";
    }


    @GetMapping("/refresh")
    @ResponseBody
    public void updatePendingPaymentsStatus(){
        try {
            List<CorpPaymentRequest> corpPaymentRequests= customDutyService.updatePendingStatus();


        }
        catch (Exception e){
            LOGGER.error("Error calling bank service rest service",e);
        }
    }

}
