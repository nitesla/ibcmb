package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.NEnquiryDetails;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/20/2017.
 */

@Service
public class CorpTransferServiceImpl implements CorpTransferService {

    private CorpTransferRequestRepo corpTransferRequestRepo;
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
    private EntityManager entityManager;

    @Autowired
    public CorpTransferServiceImpl(CorpTransferRequestRepo corpTransferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, AccountService accountService, ConfigurationService configService) {
        this.corpTransferRequestRepo = corpTransferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.accountService = accountService;
        this.configService = configService;
    }


    @Override
    public Object addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

        CorpTransRequest transferRequest = convertDTOToEntity(transferRequestDTO);
        String userRefereneceNumber = "CORP_"+getCurrentUser().getId().toString();
        transferRequest.setUserReferenceNumber(userRefereneceNumber);
        transferRequestDTO.setUserReferenceNumber(userRefereneceNumber);

        if ("SOLE".equals(transferRequest.getCorporate().getCorporateType())) {
            CorpTransferRequestDTO requestDTO = makeTransfer(transferRequestDTO);
            if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) { // Transfer successful
                logger.debug("returning payment Request Sent for sole:{}",transferRequest.getCorporate().getCorporateType());
                return requestDTO;
            } else {
                throw new InternetBankingTransferException(requestDTO.getStatusDescription());
            }
        }

        if (corporateService.getApplicableTransferRule(transferRequest) == null) {
            throw new TransferRuleException(messageSource.getMessage("rule.unapplicable", null, locale));
        }

        try {
            transferRequest.setStatus(StatusCode.PENDING.toString());
            transferRequest.setStatusDescription("Pending Authorization");
            CorpTransferAuth transferAuth = new CorpTransferAuth();
            transferAuth.setStatus("P");
            transferRequest.setTransferAuth(transferAuth);
            CorpTransRequest corpTransRequest = corpTransferRequestRepo.save(transferRequest);
            logger.info("Transfer request saved for authorization");
            if (userCanAuthorize(corpTransRequest)) {
                CorpTransReqEntry transReqEntry = new CorpTransReqEntry();
                transReqEntry.setTranReqId(corpTransRequest.getId());
                transReqEntry.setAuthStatus(TransferAuthorizationStatus.APPROVED);
                return addAuthorization(transReqEntry);
            }
        } catch (TransferAuthorizationException ex) {
            throw ex;
        } catch (InternetBankingTransferException te) {
            throw te;
        } catch (Exception e) {
            throw new InternetBankingTransferException(messageSource.getMessage("transfer.add.failure", null, locale), e);
        }
        return messageSource.getMessage("transfer.add.success", null, locale);
    }

    private CorpTransferRequestDTO makeTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(corpTransferRequestDTO);
        logger.trace("Initiating {} transfer to {} by {}", corpTransferRequestDTO.getTransferType(), corpTransferRequestDTO.getBeneficiaryAccountName(),corpTransferRequestDTO.getUserReferenceNumber());
        CorpTransRequest corpTransRequest = persistTransfer(corpTransferRequestDTO);
        logger.info("the corporate transfer request {}",corpTransRequest);
        CorpTransRequest corpTransRequestNew = (CorpTransRequest) integrationService.makeTransfer(corpTransRequest);//name change by GB
        logger.trace("Transfer Details {} by {}", corpTransRequest.toString(),corpTransRequest.getUserReferenceNumber());

        if (corpTransRequestNew != null) {
            CorpTransRequest corpTransRequest1=corpTransferRequestRepo.findOne(corpTransRequest.getId());
            entityManager.detach(corpTransRequest1);
            corpTransRequest1.setReferenceNumber(corpTransRequestNew.getReferenceNumber());
            corpTransRequest1.setNarration(corpTransRequestNew.getNarration());
            corpTransRequest1.setStatus(corpTransRequestNew.getStatus());
            corpTransRequest1.setStatusDescription(corpTransRequestNew.getStatusDescription());

            corpTransferRequestRepo.save(corpTransRequest1);
            corpTransferRequestDTO = convertEntityToDTO(corpTransRequest1);

            if (corpTransRequest1.getStatus() != null) {
                return corpTransferRequestDTO;
            }
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException(messageSource.getMessage("transfer.failed",null,locale));
    }

    @Override
    public Page<CorpTransRequest> getCompletedTransfers(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        logger.info("corporate:{}",corporate);
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateAndStatusInAndTranDateNotNullOrderByTranDateDesc(corporate, Arrays.asList("00", "000"), pageDetails);
        logger.info("Page<CorpTransRequest> count:{}",pageDetails.getPageSize());
        List<CorpTransRequest> corpTransRequests = page.getContent().stream()
                .filter(transRequest -> !accountConfigService.isAccountRestrictedForViewFromUser(accountService.getAccountByAccountNumber(transRequest.getCustomerAccountNumber()).getId(),corporateUser.getId())).collect(Collectors.toList());
        return new PageImpl<CorpTransRequest>(corpTransRequests,pageDetails,page.getTotalElements());
    }

    @Override
    public CorpTransRequest getTransfer(Long id) {
        return corpTransferRequestRepo.findById(id);
    }


    @Override
    public CorpTransferRequestDTO saveTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        CorpTransferRequestDTO result = new CorpTransferRequestDTO();
        try {
            CorpTransRequest transRequest = convertDTOToEntity(corpTransferRequestDTO);
            result = convertEntityToDTO(corpTransferRequestRepo.save(transRequest));

        } catch (Exception e) {
            logger.error("Exception occurred saving transfer request", e);
        }
        return result;
    }


    public CorpTransRequest persistTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        CorpTransRequest transRequest = convertDTOToEntity(corpTransferRequestDTO);
        return corpTransferRequestRepo.save(transRequest);
//        try {
//
////            result = convertEntityToDTO(corpTransferRequestRepo.save(transRequest));
//
//        } catch (Exception e) {
//            logger.error("Exception occurred saving transfer request", e);
//        }
//        return transRequest;
    }


    private void validateBalance(CorpTransferRequestDTO corpTransferRequest) throws InternetBankingTransferException {

        BigDecimal balance = integrationService.getAvailableBalance(corpTransferRequest.getCustomerAccountNumber());
        if (balance != null) {
            if (!(balance.compareTo(corpTransferRequest.getAmount()) == 0 || (balance.compareTo(corpTransferRequest.getAmount()) == 1))) {
                throw new InternetBankingTransferException(TransferExceptions.BALANCE.toString());
            }
        }

    }

    @Override
    public void validateTransfer(CorpTransferRequestDTO dto) throws InternetBankingTransferException {
        if (dto.getBeneficiaryAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber())) {
            throw new InternetBankingTransferException(TransferExceptions.SAME_ACCOUNT.toString());
        }
        validateAccounts(dto);
        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                dto.getTransferType(),
                UserType.CORPORATE,
                dto.getCustomerAccountNumber(),
                dto.getAmount()

        );
        if (limitExceeded) throw new InternetBankingTransferException(TransferExceptions.LIMIT_EXCEEDED.toString());

       Corporate corporate = corporateRepo.findOne(getCurrentUser().getCorporate().getId());
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(corporate.getAccounts()).spliterator(), false)
                .anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber()));


        if (!acctPresent) {
            throw new InternetBankingTransferException(TransferExceptions.NO_DEBIT_ACCOUNT.toString());
        }


        if (validateBalance()) {
            validateBalance(dto);
        }
    }

    private boolean validateBalance() {
        SettingDTO setting = configService.getSettingByName("ACCOUNT_BALANCE_VALIDATION");
        if (setting != null && setting.isEnabled()) {
            return ("YES".equals(setting.getValue()));
        }
        return true;
    }

    @Override
    public Page<CorpTransRequest> getTransferRequests(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateOrderByStatusAscTranDateDesc(corporate, pageDetails);
        List<CorpTransRequest> corpTransRequests = page.getContent().stream()
                .filter(transRequest -> !accountConfigService.isAccountRestrictedForViewFromUser(accountService.getAccountByAccountNumber(transRequest.getCustomerAccountNumber()).getId(),corporateUser.getId())).collect(Collectors.toList());
        return new PageImpl<CorpTransRequest>(corpTransRequests,pageDetails,page.getTotalElements());

    }

    @Override
    public int countPendingRequest() {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        return corpTransferRequestRepo.countByCorporateAndStatus(corporate, StatusCode.PENDING.toString());
    }

    public CorpTransferRequestDTO convertEntityToDTO(CorpTransRequest corpTransRequest) {
        CorpTransferRequestDTO transferRequestDTO = new CorpTransferRequestDTO();
        transferRequestDTO.setId(corpTransRequest.getId());
        transferRequestDTO.setVersion(corpTransRequest.getVersion());
        transferRequestDTO.setCustomerAccountNumber(corpTransRequest.getCustomerAccountNumber());
        transferRequestDTO.setTransferType(corpTransRequest.getTransferType());
        transferRequestDTO.setFinancialInstitution(corpTransRequest.getFinancialInstitution());
        transferRequestDTO.setBeneficiaryAccountNumber(corpTransRequest.getBeneficiaryAccountNumber());
        transferRequestDTO.setBeneficiaryAccountName(corpTransRequest.getBeneficiaryAccountName());
        transferRequestDTO.setRemarks(corpTransRequest.getRemarks());
        transferRequestDTO.setStatus(corpTransRequest.getStatus());
        transferRequestDTO.setReferenceNumber(corpTransRequest.getReferenceNumber());
        transferRequestDTO.setUserReferenceNumber(corpTransRequest.getUserReferenceNumber());
        transferRequestDTO.setNarration(corpTransRequest.getNarration());
        transferRequestDTO.setStatusDescription(corpTransRequest.getStatusDescription());
        transferRequestDTO.setAmount(corpTransRequest.getAmount());
        transferRequestDTO.setTranDate(corpTransRequest.getTranDate());
        transferRequestDTO.setCorporateId(corpTransRequest.getCorporate().getId().toString());
        if (corpTransRequest.getTransferAuth() != null) {
            transferRequestDTO.setTransAuthId(corpTransRequest.getTransferAuth().getId().toString());
        }
        return transferRequestDTO;
    }


    public CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO transferRequestDTO) {
        CorpTransRequest corpTransRequest = new CorpTransRequest();
        corpTransRequest.setId(transferRequestDTO.getId());
        corpTransRequest.setVersion(transferRequestDTO.getVersion());
        corpTransRequest.setCustomerAccountNumber(transferRequestDTO.getCustomerAccountNumber());
        corpTransRequest.setTransferType(transferRequestDTO.getTransferType());
        corpTransRequest.setFinancialInstitution(transferRequestDTO.getFinancialInstitution());
        corpTransRequest.setBeneficiaryAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
        corpTransRequest.setBeneficiaryAccountName(transferRequestDTO.getBeneficiaryAccountName());
        corpTransRequest.setRemarks(transferRequestDTO.getRemarks());
        corpTransRequest.setStatus(transferRequestDTO.getStatus());
        corpTransRequest.setReferenceNumber(transferRequestDTO.getReferenceNumber());
        corpTransRequest.setNarration(transferRequestDTO.getNarration());
        corpTransRequest.setStatusDescription(transferRequestDTO.getStatusDescription());
        corpTransRequest.setAmount(transferRequestDTO.getAmount());
        corpTransRequest.setUserReferenceNumber(transferRequestDTO.getUserReferenceNumber());
        Corporate corporate = corporateRepo.findOne(Long.parseLong(transferRequestDTO.getCorporateId()));
        corpTransRequest.setCorporate(corporate);
        if (transferRequestDTO.getTransAuthId() != null) {
            CorpTransferAuth transferAuth = transferAuthRepo.findOne(Long.parseLong(transferRequestDTO.getTransAuthId()));
            corpTransRequest.setTransferAuth(transferAuth);
        }
        return corpTransRequest;
    }

    public List<CorpTransferRequestDTO> convertEntitiesToDTOs(Iterable<CorpTransRequest> corpTransferRequests) {
        List<CorpTransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (CorpTransRequest transRequest : corpTransferRequests) {
            CorpTransferRequestDTO transferRequestDTO = convertEntityToDTO(transRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }

    private void validateAccounts(CorpTransferRequestDTO dto) throws InternetBankingTransferException {

        accountService.validateAccount(dto.getCustomerAccountNumber());

        String bvn = integrationService.viewCustomerDetails(dto.getCustomerAccountNumber()).getBvn();
        if (bvn == null || bvn.isEmpty() || bvn.equalsIgnoreCase(""))
            throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());


        if (!integrationService.viewAccountDetails(dto.getCustomerAccountNumber()).getAcctStatus().equalsIgnoreCase("A"))
            throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());


        if (!NumberUtils.isNumber(dto.getAmount().toString()) || dto.getAmount().compareTo(new BigDecimal(0)) <= 0)
            throw new InternetBankingTransferException(TransferExceptions.INVALID_AMOUNT.toString());


        if (dto.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || dto.getTransferType().equals(TransferType.CORONATION_BANK_TRANSFER)) {
            AccountDetails sourceAccount = integrationService.viewAccountDetails(dto.getCustomerAccountNumber());
            AccountDetails destAccount = integrationService.viewAccountDetails(dto.getBeneficiaryAccountNumber());
            if (sourceAccount == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
            if (destAccount == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
            if ((("NGN").equalsIgnoreCase(sourceAccount.getAcctCrncyCode())) && !("NGN").equalsIgnoreCase(destAccount.getAcctCrncyCode()))

                throw new InternetBankingTransferException(TransferExceptions.NOT_ALLOWED.toString());

        }
        if (dto.getTransferType().equals(TransferType.INTER_BANK_TRANSFER)) {
            NEnquiryDetails details = integrationService.doNameEnquiry(dto.getFinancialInstitution().getInstitutionCode(), dto.getBeneficiaryAccountNumber());
          /*  if (details != null && details.getAccountName() == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());*/

            if (integrationService.viewAccountDetails(dto.getCustomerAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
        }

    }


    @Override
    public CorpTransferAuth getAuthorizations(CorpTransRequest transRequest) {
        CorpTransRequest corpTransRequest = corpTransferRequestRepo.findOne(transRequest.getId());
        return corpTransRequest.getTransferAuth();

    }


    public boolean userCanAuthorize(TransRequest transRequest) {
        CorporateUser corporateUser = getCurrentUser();
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(transRequest);
        CorpTransRequest transferRequest = (CorpTransRequest)transRequest;

        if (transferRule != null) {
            List<CorporateRole> roles = getExistingRoles(transferRule.getRoles());
            for (CorporateRole corporateRole : roles) {
                if (corpRoleRepo.countInRole(corporateRole, corporateUser) > 0 && !reqEntryRepo.existsByTranReqIdAndRole(transRequest.getId(), corporateRole) && "P".equals(transferRequest.getTransferAuth().getStatus())) {
                    return true;
                }
            }
        }
        return false;
    }



    @Override
    public String addAuthorization(CorpTransReqEntry transReqEntry) {

        CorporateUser corporateUser = getCurrentUser();
        CorpTransRequest corpTransRequest = corpTransferRequestRepo.findOne(transReqEntry.getTranReqId());
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(corpTransRequest);
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

        CorpTransferAuth transferAuth = corpTransRequest.getTransferAuth();

        if (!"P".equals(transferAuth.getStatus())) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.complete", null, locale));
        }

        if (reqEntryRepo.existsByTranReqIdAndRole(corpTransRequest.getId(), userRole)) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.exist", null, locale));
        }

        if(TransferAuthorizationStatus.DECLINED.equals(transReqEntry.getAuthStatus())){
            transReqEntry.setEntryDate(new Date());
            transReqEntry.setRole(userRole);
            transReqEntry.setUser(corporateUser);
            transferAuth.getAuths().add(transReqEntry);
            transferAuth.setStatus("X");//cancelled
            corpTransRequest.setStatus(StatusCode.CANCELLED.toString());
            corpTransRequest.setStatusDescription("Cancelled");
            transferAuthRepo.save(transferAuth);
            return messageSource.getMessage("transfer.auth.decline", null, locale);

        }

        SettingDTO setting = configService.getSettingByName("RETRY_FAILED_TRANSFER");

        if (setting != null && setting.isEnabled() && "YES".equalsIgnoreCase(setting.getValue())) {

            try {// RETRY_FAILED_TRANSFER is enabled, the entry is not added until we check for completion of authorizations
                transReqEntry.setEntryDate(new Date());
                transReqEntry.setRole(userRole);
                transReqEntry.setUser(corporateUser);

                if (isAuthorizationComplete(corpTransRequest)) {
                    CorpTransferRequestDTO requestDTO = makeTransfer(convertEntityToDTO(corpTransRequest));

                    if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) {//successful transaction

                        transferAuth.setStatus("C");
                        transferAuth.setLastEntry(new Date());
                        transferAuth.getAuths().add(transReqEntry);// the entry is added to the list of other entries
                        transferAuthRepo.save(transferAuth);
                        return requestDTO.getStatusDescription();

                    } else {//failed transaction
                        throw new InternetBankingTransferException(String.format(messageSource.getMessage("transfer.auth.failure.reason", null, locale), requestDTO.getStatusDescription()));
                    }
                }
                transferAuth.getAuths().add(transReqEntry);// the entry is added, as authorizations are not yet completed
                transferAuthRepo.save(transferAuth);
                return messageSource.getMessage("transfer.auth.success", null, locale);
            } catch (InternetBankingTransferException te) {
                throw te;
            } catch (Exception e) {
                throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
            }
        } else {// RETRY_FAILED_TRANSFER is disabled, so we add the entry before checking for authorization completion
            try {
                transReqEntry.setEntryDate(new Date());
                transReqEntry.setRole(userRole);
                transReqEntry.setUser(corporateUser);
                transferAuth.getAuths().add(transReqEntry);
                transferAuthRepo.save(transferAuth);

                if (isAuthorizationComplete(corpTransRequest)) {
                    transferAuth.setStatus("C"); //completed
                    transferAuth.setLastEntry(new Date());
                    transferAuthRepo.save(transferAuth);
                    CorpTransferRequestDTO requestDTO = makeTransfer(convertEntityToDTO(corpTransRequest));
                    if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) { //successful transaction
                        return requestDTO.getStatusDescription();
                    } else {
                        throw new InternetBankingTransferException(requestDTO.getStatusDescription());
                    }
                }
                return messageSource.getMessage("transfer.auth.success", null, locale);
            } catch (InternetBankingTransferException transferException) {
                throw transferException;
            } catch (Exception e) {
                throw new InternetBankingException(messageSource.getMessage("transfer.auth.success", null, locale), e);
            }
        }
    }


    private boolean isAuthorizationComplete(CorpTransRequest transRequest) {
        CorpTransferAuth transferAuth = transRequest.getTransferAuth();
        Set<CorpTransReqEntry> transReqEntries = transferAuth.getAuths();
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(transRequest);
        List<CorporateRole> roles = getExistingRoles(corpTransRule.getRoles());
        boolean anyCanAuthorize = false;
        int approvalCount = 0;

        SettingDTO transferSetting = configService.getSettingByName("RETRY_FAILED_TRANSFER");

        boolean retryTransfer = transferSetting != null && transferSetting.isEnabled() && "YES".equalsIgnoreCase(transferSetting.getValue());


        if (retryTransfer) {
            approvalCount = 1; //this is changed to 1 because the user authorization has to be added before counting all authorizations
        }

        if (corpTransRule.isAnyCanAuthorize()) {
            anyCanAuthorize = true;
        }

        int numAuthorizers = 1;
        SettingDTO authSetting = configService.getSettingByName("MIN_AUTHORIZER_LEVEL");
        if (authSetting != null && authSetting.isEnabled()) {

            numAuthorizers = NumberUtils.toInt(authSetting.getValue());
        }

        for (CorporateRole role : roles) {
            for (CorpTransReqEntry corpTransReqEntry : transReqEntries) {
                if (corpTransReqEntry.getRole().equals(role)) {
                    approvalCount++;
                    if (anyCanAuthorize && (approvalCount >= numAuthorizers))
                        return true;
                }
            }
        }

        if (retryTransfer) {
            if (anyCanAuthorize && (approvalCount >= numAuthorizers))
                return true;
        }

        return approvalCount >= roles.size();

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


}
