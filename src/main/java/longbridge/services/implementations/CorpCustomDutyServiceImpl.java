package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.StatusCode;
import longbridge.utils.TransferAuthorizationStatus;
import longbridge.utils.TransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class CorpCustomDutyServiceImpl implements CorpCustomDutyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpCustomDutyServiceImpl.class);

    @Autowired
    private CustomDutyRepo customDutyRepo;

    private IntegrationService integrationService;
    private TransactionLimitServiceImpl limitService;
    private AccountService accountService;
    private ConfigurationService configService;
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
    public CorpCustomDutyServiceImpl( IntegrationService integrationService, TransactionLimitServiceImpl limitService, AccountService accountService, ConfigurationService configService) {
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.accountService = accountService;
        this.configService = configService;
    }

    @Override
    public CustomAssessmentDetail getAssessmentDetails(CustomSADAsmt sadAsmt) {
        CustomAssessmentDetailsRequest assessmentDetailsRequest =  new CustomAssessmentDetailsRequest();
        assessmentDetailsRequest.setSadAsmt(sadAsmt);
       return integrationService.getAssessmentDetails(assessmentDetailsRequest);
    }

    @Override
    public CustomsAreaCommand getCustomsAreaCommands() {
        CustomsAreaCommandRequest customsAreaCommandRequest = new CustomsAreaCommandRequest();
        customsAreaCommandRequest.setAppId("test");
        customsAreaCommandRequest.setHash("ad1222bgfghj22j33m333");
        return integrationService.getCustomsAreaCommands(customsAreaCommandRequest);
    }

    @Override
    public void paymentNotification(CustomAssessmentDetail assessmentDetail) {
        CustomPaymentNotificationRequest paymentNotificationRequest = new CustomPaymentNotificationRequest();
        integrationService.paymentNotification(paymentNotificationRequest);
    }

    @Override
    public CustomTransactionStatus paymentStatus(CustomTransactionStatus customTransactionStatus) {
        return integrationService.paymentStatus(customTransactionStatus);
    }

    public boolean isAccountBalanceEnough(String acctNumber, BigDecimal amount){
        BigDecimal availableBalance =  integrationService.getAvailableBalance(acctNumber);
        LOGGER.info("the availableBalance {}",availableBalance);
        return availableBalance.compareTo(amount) > 0;
    }

    @Override
    public String saveCustomPaymentRequestForAuthorization(CorpPaymentRequest corpPaymentRequest) {
        logger.trace("Saving Corp Custom Payment request", corpPaymentRequest);
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
            CorpPaymentRequest transfer = customDutyRepo.save(corpPaymentRequest);
            if (userCanAuthorize(transfer)) {
                CorpTransReqEntry transReqEntry = new CorpTransReqEntry();
                transReqEntry.setTranReqId(transfer.getId());
                transReqEntry.setAuthStatus(TransferAuthorizationStatus.APPROVED);
                return addAuthorization(transReqEntry);
            }
        } catch (TransferAuthorizationException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("bulk.save.failure", null, null), e);
        }
        return messageSource.getMessage("bulk.save.success", null, null);
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
    public String addAuthorization(CorpTransReqEntry transReqEntry) {

        CorporateUser corporateUser = getCurrentUser();
        CorpPaymentRequest corpPaymentRequest = customDutyRepo.findOne(transReqEntry.getTranReqId());
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
//            if (TransferAuthorizationStatus.DECLINED.equals(transReqEntry.getAuthStatus())) {
//                transferAuth.setStatus("X"); //cancelled
//                corpPaymentRequest.setStatus(StatusCode.CANCELLED.toString());
//                corpPaymentRequest.setStatusDescription("Cancelled");
//                transferAuthRepo.save(transferAuth);
//                List<CreditRequest> creditRequests = corpPaymentRequest.getCrRequestList();
//                creditRequests.forEach(i -> {
//                    i.setStatus("CANCELLED");
//                    creditRequestRepo.save(i);
//                });
//
//                return messageSource.getMessage("transfer.auth.decline", null, locale);
//            }
//
//            transferAuthRepo.save(transferAuth);
//            if (isAuthorizationComplete(bulkTransfer)) {
//                transferAuth.setStatus("C");
//                transferAuth.setLastEntry(new Date());
//                transferAuthRepo.save(transferAuth);
//                return makeBulkTransferRequest(bulkTransfer);
//            }

            return messageSource.getMessage("transfer.auth.success", null, locale);
        } catch (InternetBankingTransferException transferException) {
            throw transferException;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }
    }

}
