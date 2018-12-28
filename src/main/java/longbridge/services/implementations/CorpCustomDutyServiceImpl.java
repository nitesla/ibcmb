package longbridge.services.implementations;

import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CorpCustomDutyServiceImpl implements CorpCustomDutyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpCustomDutyServiceImpl.class);

    @Autowired
    private CorpPaymentRequestRepo corpPaymentRequestRepo;

    @Autowired
    private CustomDutyPaymentRepo customDutyPaymentRepo;

    @Value("${custom.duty.remark}")
    private String paymentRemark;

    @Value("${custom.appId}")
    private String appId;

    @Value("${custom.secretKey}")
    private String secretKey;

    @Value("${custom.beneficiaryAcct}")
    private String beneficiaryAcct;

    private CorpTransferServiceImpl CorpTransferServiceImpl;
    private IntegrationService integrationService;
    private TransactionLimitServiceImpl limitService;
    private AccountService accountService;
    private ConfigurationService configService;
    private FinancialInstitutionService financialInstitutionService;
    private CorporateUserService corporateUserService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private CorpTransferAuthRepo transferAuthRepo;

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CorporateRepo corporateRepo;

    @Autowired
    private CorporateRoleRepo corpRoleRepo;

    @Autowired
    private CorpTransReqEntryRepo reqEntryRepo;

    @Autowired
    public CorpCustomDutyServiceImpl( IntegrationService integrationService, TransactionLimitServiceImpl limitService, AccountService accountService, ConfigurationService configService, FinancialInstitutionService financialInstitution, CorporateUserService corporateUserService) {
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.accountService = accountService;
        this.configService = configService;
        this.financialInstitutionService = financialInstitution;
        this.corporateUserService =  corporateUserService;
    }

    @Override
    public CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt, String customCode) {
        CustomAssessmentDetailsRequest assessmentDetailsRequest =  new CustomAssessmentDetailsRequest();
        assessmentDetailsRequest.setCustomsCode(customCode);
        assessmentDetailsRequest.setSadAsmt(sadAsmt);
       return integrationService.getAssessmentDetails(assessmentDetailsRequest);
    }

    @Override
    public CustomsAreaCommand getCustomsAreaCommands() {
        CustomsAreaCommandRequest customsAreaCommandRequest = new CustomsAreaCommandRequest();
        customsAreaCommandRequest.setAppId(appId);
        customsAreaCommandRequest.setHash(EncryptionUtil.getSHA512(appId+" "+secretKey,null));
        return integrationService.getCustomsAreaCommands(customsAreaCommandRequest);
    }

    @Override
    public void paymentNotification(CustomAssessmentDetail assessmentDetail) {
        CustomPaymentNotificationRequest paymentNotificationRequest = new CustomPaymentNotificationRequest();
//        integrationService.paymentNotification(paymentNotificationRequest);
    }

    @Override
    public CustomTransactionStatus getPaymentStatus(CorpPaymentRequest corpPaymentRequest) {
        return integrationService.paymentStatus(corpPaymentRequest);
    }

    @Override
    public CustomTransactionStatus updatePayamentStatus(Long id) {
        CorpPaymentRequest corpPaymentRequest = getPayment(id);
//        CustomTransactionStatus customTransactionStatus = extractStatusFromPayment(corpPaymentRequest);
        CustomTransactionStatus customTransactionStatus= getPaymentStatus(corpPaymentRequest);
        corpPaymentRequest = updatePaymentFields(customTransactionStatus,corpPaymentRequest);
        corpPaymentRequestRepo.save(corpPaymentRequest);
        return customTransactionStatus;
    }
    private CustomTransactionStatus extractStatusFromPayment(CorpPaymentRequest corpPaymentRequest){
        CustomTransactionStatus customTransactionStatus =  new CustomTransactionStatus();
        customTransactionStatus.setCode(corpPaymentRequest.getCustomDutyPayment().getCode());
        customTransactionStatus.setMessage(corpPaymentRequest.getCustomDutyPayment().getMessage());
        customTransactionStatus.setPaymentRef(corpPaymentRequest.getCustomDutyPayment().getPaymentRef());
        customTransactionStatus.setNotificationStatus(corpPaymentRequest.getCustomDutyPayment().getNotificationStatus());
        customTransactionStatus.setApprovalStatus(corpPaymentRequest.getCustomDutyPayment().getApprovalStatus());
        customTransactionStatus.setPaymentStatus(corpPaymentRequest.getCustomDutyPayment().getPaymentStatus());
        return  customTransactionStatus;
    }

    private CorpPaymentRequest updatePaymentFields(CustomTransactionStatus customTransactionStatus,CorpPaymentRequest corpPaymentRequest){
//        CorpPaymentRequest corpPaymentRequest =  new CorpPaymentRequest();
        corpPaymentRequest.getCustomDutyPayment().setCode(customTransactionStatus.getCode());
        corpPaymentRequest.getCustomDutyPayment().setMessage(customTransactionStatus.getMessage());
        corpPaymentRequest.getCustomDutyPayment().setPaymentRef(customTransactionStatus.getPaymentRef());
        corpPaymentRequest.getCustomDutyPayment().setNotificationStatus(customTransactionStatus.getNotificationStatus());
        corpPaymentRequest.getCustomDutyPayment().setApprovalStatus(customTransactionStatus.getApprovalStatus());
        corpPaymentRequest.getCustomDutyPayment().setPaymentStatus(customTransactionStatus.getPaymentStatus());
        return  corpPaymentRequest;
    }

    public boolean isAccountBalanceEnough(String acctNumber, BigDecimal amount){
        BigDecimal availableBalance =  integrationService.getAvailableBalance(acctNumber);
        LOGGER.info("the availableBalance {}",availableBalance);
        return availableBalance.compareTo(amount) > 0;
    }

    @Override
    public String saveCustomPaymentRequestForAuthorization(CorpPaymentRequest corpPaymentRequest) {
        accountService.validateAccount(corpPaymentRequest.getCustomerAccountNumber());
        if (corporateService.getApplicableTransferRule(corpPaymentRequest) == null) {
            throw new TransferRuleException(messageSource.getMessage("rule.unapplicable", null, locale));
        }
        try {
            corpPaymentRequest.setStatus(StatusCode.PENDING.toString());
            corpPaymentRequest.setStatusDescription("Pending Authorization");
            CorpTransferAuth transferAuth = new CorpTransferAuth();
            transferAuth.setStatus("P");
            corpPaymentRequest.setTransferAuth(transferAuth);
            CorpPaymentRequest transfer = corpPaymentRequestRepo.save(corpPaymentRequest);
            if (userCanAuthorize(transfer)) {
                CorpTransReqEntry transReqEntry = new CorpTransReqEntry();
                transReqEntry.setTranReqId(transfer.getId());
                transReqEntry.setAuthStatus(TransferAuthorizationStatus.APPROVED);
                return addAuthorization(transReqEntry,null);
            }
        } catch (TransferAuthorizationException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("bulk.save.failure", null, null), e);
        }
        return messageSource.getMessage("bulk.save.success", null, null);
    }

    @Override
    @Transactional
    public String saveCustomPaymentRequestForAuthorization(CustomAssessmentDetail assessmentDetail, CustomAssessmentDetailsRequest assessmentDetailsRequest, Principal principal, Corporate corporate ) {

        if(assessmentDetail == null || principal == null) {
            //return messageSource.getMessage("bulk.save.success", null, null);
            return null;
        }

        CustomDutyPayment customDutyPayment = saveCustomDutyPayment(assessmentDetail, assessmentDetailsRequest,principal);
        CorpPaymentRequest request = saveCorpPaymentRequest( customDutyPayment, corporate,principal, false);

            if (userCanAuthorize(request)) {
                CorpTransReqEntry transReqEntry = new CorpTransReqEntry();
                transReqEntry.setTranReqId(request.getId());
                transReqEntry.setAuthStatus(TransferAuthorizationStatus.APPROVED);
                return addAuthorization(transReqEntry, principal);
            }
        return messageSource.getMessage("custom.payment.save.success", null, null);
    }

    public CustomDutyPayment saveCustomDutyPayment(CustomAssessmentDetail assessmentDetail, CustomAssessmentDetailsRequest assessmentDetailsRequest, Principal principal){
        CustomDutyPayment customDutyPayment = new CustomDutyPayment();
        customDutyPayment.setSADYear(assessmentDetailsRequest.getSadAsmt().getSADYear());
        customDutyPayment.setCommandDutyArea(assessmentDetailsRequest.getCustomsCode());
        customDutyPayment.setSGDAssessmentDate(assessmentDetail.getResponseInfo().getSGDAssessmentDate());
        customDutyPayment.setSADAssessmentNumber(assessmentDetailsRequest.getSadAsmt().getSADAssessmentNumber());
        customDutyPayment.setSADAssessmentSerial(assessmentDetailsRequest.getSadAsmt().getSADAssessmentSerial());
        customDutyPayment.setTotalAmount(new BigDecimal(
                assessmentDetail.getResponseInfo().getTotalAmount(), MathContext.DECIMAL64));
        customDutyPayment.setTaxs(assessmentDetail.getResponseInfo().getTaxDetails());
        customDutyPayment.setBankCode(assessmentDetail.getResponseInfo().getBankCode());
        customDutyPayment.setCompanyCode(assessmentDetail.getResponseInfo().getCompanyCode());
        customDutyPayment.setDeclarantCode(assessmentDetail.getResponseInfo().getDeclarantCode());
        customDutyPayment.setDeclarantName(assessmentDetail.getResponseInfo().getDeclarantName());
        customDutyPayment.setCompanyName(assessmentDetail.getResponseInfo().getCompanyName());
        customDutyPayment.setApprovalStatus(assessmentDetail.getResponseInfo().getApprovalStatus());
        customDutyPayment.setApprovalStatusDescription(assessmentDetail.getResponseInfo().getApprovalStatusDescription());
        customDutyPayment.setCollectionAccount(assessmentDetail.getResponseInfo().getCollectionAccount());
        customDutyPayment.setFormMNumber(assessmentDetail.getResponseInfo().getFormMNumber());
        customDutyPayment.setSGDAssessmentDate(assessmentDetail.getResponseInfo().getSGDAssessmentDate());
        customDutyPayment.setTranId(assessmentDetail.getResponseInfo().getTranId());
        customDutyPayment.setAccount(assessmentDetail.getAccount());
        customDutyPayment.setCode(assessmentDetail.getCode());
        customDutyPayment.setMessage(CustomDutyCode.getCustomDutyCodeByCode(assessmentDetail.getCode()).replace("_"," "));
        customDutyPayment.setInitiatedBy(principal.getName());
        CustomDutyPayment resp = customDutyPaymentRepo.save(customDutyPayment);
        LOGGER.info("customDutyPayment:{}",resp);
        return resp;
    }

    public CorpPaymentRequest saveCorpPaymentRequest(CustomDutyPayment customDutyPayment, Corporate corporate, Principal principal, boolean isSole){
        CorpPaymentRequest request = new CorpPaymentRequest();
        try {
                request.setCustomDutyPayment(customDutyPayment);
                request.setAmount(customDutyPayment.getTotalAmount());
                request.setBeneficiaryAccountNumber(customDutyPayment.getAccount());
                FinancialInstitution financialInstitution =
                        financialInstitutionService.getFinancialInstitutionByBankCode("459");
                request.setFinancialInstitution(financialInstitution);
                request.setCustomerAccountNumber(customDutyPayment.getAccount());
                request.setBeneficiaryAccountName(
                        accountService.getAccountByAccountNumber(customDutyPayment.getAccount()).getAccountName());
                request.setTransferType(TransferType.CUSTOM_DUTY);
                request.setCorporate(corporate);
                request.setBeneficiaryAccountNumber(beneficiaryAcct);
                accountService.validateAccount(request.getCustomerAccountNumber());
                CorpTransferAuth transferAuth = new CorpTransferAuth();
                if(isSole){
                    request.setStatus(StatusCode.SUCCESSFUL.toString());
                    request.setStatusDescription("Transaction Scuccessful");
                    transferAuth.setStatus("C");
                    request.setTransferAuth(transferAuth);
                    request.setRemarks(customDutyPayment.getSADAssessmentNumber());
                    try {
                        CorpPaymentRequest transRequest  = (CorpPaymentRequest) integrationService.makeCustomDutyPayment(request);

                    transRequest.setTransferType(TransferType.CUSTOM_DUTY);
                    transRequest = corpPaymentRequestRepo.save(transRequest);
                        request = transRequest;
                    }catch (ResourceAccessException e) {
                        throw new InternetBankingException(messageSource.getMessage("transfer.failed", null, null));

                    }catch (Exception e){
                        throw new InternetBankingException(messageSource.getMessage(e.getMessage(), null, null));

                    }
                    if (null != request.getStatus() && ("00".equals(request.getStatus()) || "000".equals(request.getStatus()))) { // Transfer successful
                        CustomPaymentNotificationRequest notificationRequest = new CustomPaymentNotificationRequest();
                        notificationRequest.setTranId(request.getCustomDutyPayment().getTranId());
                        notificationRequest.setAmount(request.getAmount().toString());
                        notificationRequest.setPaymentRef(request.getReferenceNumber());
                        notificationRequest.setCustomerAccountNo(request.getCustomerAccountNumber());
                        notificationRequest.setLastAuthorizer(principal.getName());
                        notificationRequest.setInitiatedBy(request.getCustomDutyPayment().getInitiatedBy());

                        notificationRequest.setAppId(appId);
                        LOGGER.debug(appId + request.getCustomDutyPayment().getTranId() + request.getAmount() + secretKey);
                        notificationRequest.setHash(EncryptionUtil.getSHA512(
                                appId + request.getCustomDutyPayment().getTranId() + request.getAmount() + secretKey, null));
                        CustomPaymentNotification customPaymentNotification = integrationService.paymentNotification(request,principal.getName());
                        logger.debug("returning payment Request Sent for sole: {}", request.getCorporate().getCorporateType());
                        CustomDutyPayment dutyPayment = request.getCustomDutyPayment();
                        dutyPayment.setPaymentStatus(customPaymentNotification.getCode());
                        dutyPayment.setMessage(CustomDutyCode.getCustomDutyCodeByCode(customPaymentNotification.getCode()));
                        dutyPayment.setPaymentRef(customPaymentNotification.getPaymentRef());
                        LOGGER.debug("dutyPayment:{}", dutyPayment);
                        request.setCustomDutyPayment(dutyPayment);
                        request.setStatusDescription(dutyPayment.getMessage());
                        request.setStatus(customPaymentNotification.getCode());
                        request = corpPaymentRequestRepo.save(request);
                        LOGGER.info("CorpPaymentRequest SOLE: {}",request);
                        return request;
                    }
                        throw new InternetBankingTransferException(messageSource.getMessage(request.getStatusDescription(), null, null));


                }else{
                    if (corporateService.getApplicableTransferRule(request) == null) {
                        throw new TransferRuleException(messageSource.getMessage("rule.unapplicable", null, locale));
                    }
                    request.setStatus(StatusCode.PENDING.toString());
                    request.setStatusDescription("Pending Authorization");
                    transferAuth.setStatus("P");
                    request.setTransferAuth(transferAuth);
                    request = corpPaymentRequestRepo.save(request);
                    LOGGER.info("CorpPaymentRequest COPR: {}",request);
                }
                return request;
            } catch (TransferAuthorizationException ex) {
                throw ex;
            } catch (Exception e) {
                throw new InternetBankingException(messageSource.getMessage("custom.payment.save.failure", null, null), e);
            }
    }

    @Override
    public Page<CorpPaymentRequest> findEntities(String filter, String search, Pageable pageable) {
        Corporate corporate = getCurrentUser().getCorporate();
        logger.info("filter type {}",filter);
        if(filter.equals("Status")){
            return corpPaymentRequestRepo.findUsingPattern(search,pageable);
        }else if(filter.equals("Date")){
            try {
                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(search);
                LOGGER.info("startDate : {}",startDate.toString());
                Date endDate = addDays(startDate,1);
                LOGGER.info("endDate : ",endDate);
            return corpPaymentRequestRepo.findCorpPaymentRequestByCorporateAndTranDateBetween(corporate,pageable,startDate,endDate);
            }catch (ParseException e){
                e.printStackTrace();
            }
            }else if(filter.equals("Amount")){
            LOGGER.info("filter : {} id {}",search, corporate.getId());
            return corpPaymentRequestRepo.filterByAmount(corporate.getId(),pageable,search);
        }
        return corpPaymentRequestRepo.findUsingPattern(search,pageable);
    }

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    @Override
    public Page<CorpPaymentRequest> getEntities(Pageable pageable) {
        return corpPaymentRequestRepo.findAll(pageable);
    }

    @Override
    public boolean userCanAuthorize(TransRequest transRequest) {
        CorporateUser corporateUser = getCurrentUser();
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(transRequest);
        CorpPaymentRequest corpPaymentRequest = (CorpPaymentRequest) transRequest;

        if (transferRule == null) {
            return false;
        }

        List<CorporateRole> roles = getExistingRoles(transferRule.getRoles());

        for (CorporateRole corporateRole : roles) {
            if (corpRoleRepo.countInRole(corporateRole, corporateUser) > 0 && !reqEntryRepo.existsByTranReqIdAndRole(transRequest.getId(), corporateRole) && "P".equals(corpPaymentRequest.getTransferAuth().getStatus())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Page<CorpPaymentRequest> getPaymentRequests(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpPaymentRequest> page = corpPaymentRequestRepo.findByCorporateOrderByTranDateDesc(corporate, pageDetails);
        List<CorpPaymentRequest> corpPaymentRequest = page.getContent().stream()
                .filter(transRequest -> !accountConfigService.isAccountRestrictedForViewFromUser(accountService.getAccountByAccountNumber(transRequest.getCustomerAccountNumber()).getId(),corporateUser.getId())).collect(Collectors.toList());
        return new PageImpl<CorpPaymentRequest>(corpPaymentRequest,pageDetails,page.getTotalElements());
    }

    @Override
    public CorpPaymentRequest getPayment(Long id) {
        return corpPaymentRequestRepo.findById(id);
    }

    @Override
    public Page<CorpPaymentRequest> getPayments(Pageable pageable,String search) {
        return corpPaymentRequestRepo.findAllByOrderByTranDateDesc(pageable);
//        return null;
    }

    private CorporateUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CorporateUser corporateUser = (CorporateUser) principal.getUser();
        return corporateUser;
    }

    private List<CorporateRole> getExistingRoles(List<CorporateRole> roles) {
        List<CorporateRole> existingRoles = new ArrayList<>();
        for (CorporateRole role : roles) {
            if ("N".equals(role.getDelFlag())) {
                existingRoles.add(role);
            }
        }
        return existingRoles;
    }

    @Override
    public CorpTransferAuth getAuthorizations(CorpPaymentRequest paymentRequest) {
        CorpPaymentRequest corpPaymentRequest = corpPaymentRequestRepo.findOne(paymentRequest.getId());
        return corpPaymentRequest.getTransferAuth();
    }

    @Override
    public String addAuthorization(CorpTransReqEntry transReqEntry, Principal principal) {
        CorporateUser corporateUser = getCurrentUser();
        CorpPaymentRequest corpPaymentRequest = corpPaymentRequestRepo.findOne(transReqEntry.getTranReqId());
        corpPaymentRequest.setUserReferenceNumber("CORP_"+getCurrentUser().getId().toString());
        LOGGER.info("corpPayment Request:{}",corpPaymentRequest);
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(corpPaymentRequest);
        List<CorporateRole> roles = getExistingRoles(transferRule.getRoles());
        CorporateRole userRole = null;

        for (CorporateRole corporateRole : roles) {
            if (corpRoleRepo.countInRole(corporateRole, corporateUser) > 0) {
                userRole = corporateRole;
                break;
            }
        }

        if (userRole == null) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.invalid", null, locale));
        }

        CorpTransferAuth transferAuth = corpPaymentRequest.getTransferAuth();

        if (!"P".equals(transferAuth.getStatus())) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.complete", null, locale));
        }

        if (reqEntryRepo.existsByTranReqIdAndRole(corpPaymentRequest.getId(), userRole)) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.exist", null, locale));
        }

        try {
            transReqEntry.setEntryDate(new Date());
            transReqEntry.setRole(userRole);
            transReqEntry.setUser(corporateUser);
            transferAuth.getAuths().add(transReqEntry);

            transferAuthRepo.save(transferAuth);
            if (isAuthorizationComplete(corpPaymentRequest)) {
                transferAuth.setStatus("C");
                transferAuth.setLastEntry(new Date());
                transferAuthRepo.save(transferAuth);
                LOGGER.info("corpPaymentRequest req:{}",corpPaymentRequest);
                corpPaymentRequest.setCustomerAccountNumber(corpPaymentRequest.getCustomerAccountNumber());
                corpPaymentRequest.setReferenceNumber(corpPaymentRequest.getReferenceNumber());
                corpPaymentRequest.setBeneficiaryAccountNumber(beneficiaryAcct);
                corpPaymentRequest.setBeneficiaryAccountName("Coronation");
                corpPaymentRequest.setAmount(corpPaymentRequest.getAmount());
                corpPaymentRequest.setTransferType(TransferType.CUSTOM_DUTY);
                corpPaymentRequest.setRemarks(paymentRemark);
                corpPaymentRequest.setRemarks(corpPaymentRequest.getCustomDutyPayment().getSADAssessmentNumber());
                CorpPaymentRequest paymentRequest = (CorpPaymentRequest)integrationService.makeCustomDutyPayment(corpPaymentRequest);
//                corpPaymentRequest.setTransferType(TransferType.CUSTOM_DUTY);
                corpPaymentRequest.setStatus(paymentRequest.getStatus());
                corpPaymentRequestRepo.save(corpPaymentRequest);
                logger.info("the payment status {}",paymentRequest);
                if ((paymentRequest.getStatus().equals("00") || paymentRequest.getStatus().equals("000"))) {
                    updatePaymentRequest(corpPaymentRequest,paymentRequest);
//                    CustomPaymentNotificationRequest notificationRequest = new CustomPaymentNotificationRequest();
//                    notificationRequest.setTranId(corpPaymentRequest.getCustomDutyPayment().getTranId());
//                    notificationRequest.setAmount(corpPaymentRequest.getAmount().toString());
//                    notificationRequest.setPaymentRef(corpPaymentRequest.getReferenceNumber());
//                    notificationRequest.setCustomerAccountNo(corpPaymentRequest.getCustomerAccountNumber());
//                    notificationRequest.setLastAuthorizer(principal.getName());
//                    notificationRequest.setInitiatedBy(corpPaymentRequest.getCustomDutyPayment().getInitiatedBy());
//                    notificationRequest.setAppId(appId);
//                    notificationRequest.setHash(EncryptionUtil.getSHA512(
//                            appId+corpPaymentRequest.getCustomDutyPayment().getTranId()+ corpPaymentRequest.getAmount()+secretKey,null));
//                    LOGGER.debug("Notification Request:{}",notificationRequest);
                    CustomPaymentNotification customPaymentNotification = integrationService.paymentNotification(corpPaymentRequest,principal.getName());
                    if(customPaymentNotification == null){
                        return messageSource.getMessage("custom.auth.failure.reason", null, locale);
                    }
                    LOGGER.debug("CustomPaymentNotification:{}",customPaymentNotification);
                        CustomDutyPayment dutyPayment = corpPaymentRequest.getCustomDutyPayment();
                        dutyPayment.setPaymentStatus(customPaymentNotification.getCode());
                        dutyPayment.setMessage(CustomDutyCode.getCustomDutyCodeByCode(customPaymentNotification.getCode()).replace("_"," "));
                        dutyPayment.setPaymentRef(customPaymentNotification.getPaymentRef());
                        LOGGER.debug("customPaymentNotification:{}",customPaymentNotification);
                        customDutyPaymentRepo.save(dutyPayment);
                    if("00".equals(customPaymentNotification.getCode()) || "000".equals(customPaymentNotification.getCode())){ // Transfer successful
                        return messageSource.getMessage(customPaymentNotification.getMessage(), null, locale);
                    }else{
//                        throw new InternetBankingTransferException(messageSource.getMessage("custom.payment.failed",null,locale));
                        return messageSource.getMessage(paymentRequest.getStatusDescription(), null, locale);
                    }
                }
                throw new InternetBankingException(messageSource.getMessage(paymentRequest.getStatusDescription(), null, null));

            }
         return messageSource.getMessage("payment.auth.success",null,locale);
         //throw new InternetBankingTransferException(messageSource.getMessage("payment.auth.success",null,locale));
        } catch (InternetBankingTransferException transferException) {
            throw transferException;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternetBankingException(messageSource.getMessage(e.getMessage(), null, locale), e);
        }
    }

    private boolean isAuthorizationComplete(CorpPaymentRequest corpPaymentRequest) {
        CorpTransferAuth transferAuth = corpPaymentRequest.getTransferAuth();
        Set<CorpTransReqEntry> transReqEntries = transferAuth.getAuths();
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(corpPaymentRequest);
        List<CorporateRole> roles = getExistingRoles(corpTransRule.getRoles());
        boolean any = false;
        int approvalCount = 0;

        if (corpTransRule.isAnyCanAuthorize()) {
            any = true;
        }

        int numAuthorizers = 0;
        SettingDTO setting = configService.getSettingByName("MIN_AUTHORIZER_LEVEL");
        if (setting != null && setting.isEnabled()) {

            numAuthorizers = Integer.parseInt(setting.getValue());
        }

        for (CorporateRole role : roles) {
            for (CorpTransReqEntry corpTransReqEntry : transReqEntries) {
                if (corpTransReqEntry.getRole().equals(role)) {
                    approvalCount++;
                    if (any && (approvalCount >= numAuthorizers)) return true;
                }
            }
        }
        return approvalCount >= roles.size();
    }

    public void updatePaymentRequest(CorpPaymentRequest originalPayment, CorpPaymentRequest newPaymentRequest) throws InternetBankingTransferException {
        try {
            originalPayment.setStatus(newPaymentRequest.getStatus());
            originalPayment.setTransferType(TransferType.CUSTOM_DUTY);
            originalPayment.setReferenceNumber(newPaymentRequest.getReferenceNumber());
            originalPayment.setStatusDescription(newPaymentRequest.getStatusDescription());
            logger.info("newPaymentRequest {}",newPaymentRequest);
            logger.info("originalPayment {}",originalPayment);
            corpPaymentRequestRepo.save(originalPayment);
        } catch (Exception e) {
            logger.error("Exception occurred saving transfer request", e);
        }
    }
}
