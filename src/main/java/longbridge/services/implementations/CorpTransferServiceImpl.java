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
import longbridge.utils.TransferType;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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
    private FinancialInstitutionService financialInstitutionService;
    private ConfigurationService configService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private TransferService transferService;
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
    public CorpTransferServiceImpl(CorpTransferRequestRepo corpTransferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, AccountService accountService, FinancialInstitutionService financialInstitutionService, ConfigurationService configService) {
        this.corpTransferRequestRepo = corpTransferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
        this.configService = configService;
    }


    @Override
    public String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

        CorpTransRequest transferRequest = convertDTOToEntity(transferRequestDTO);


        if ("SOLE".equals(transferRequest.getCorporate().getCorporateType())) {
            CorpTransferRequestDTO requestDTO = makeTransfer(transferRequestDTO);
            return requestDTO.getStatusDescription();
        }

        if (corporateService.getApplicableTransferRule(transferRequest) == null) {
            throw new TransferRuleException(messageSource.getMessage("rule.unapplicable", null, locale));
        }


        try {
            transferRequest.setStatus("P");
            transferRequest.setStatusDescription("Pending");
            CorpTransferAuth transferAuth = new CorpTransferAuth();
            transferAuth.setStatus("P");
            transferRequest.setTransferAuth(transferAuth);
            CorpTransRequest corpTransRequest = corpTransferRequestRepo.save(transferRequest);
            logger.info("Transfer request saved for authorization");
            if (userCanAuthorize(corpTransRequest)) {
                CorpTransReqEntry transReqEntry = new CorpTransReqEntry();
                transReqEntry.setTranReqId(corpTransRequest.getId());
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
        logger.trace("Transfer details valid {}", corpTransferRequestDTO);
        CorpTransRequest corpTransRequest = (CorpTransRequest) integrationService.makeTransfer(convertDTOToEntity(corpTransferRequestDTO));
        if (corpTransRequest != null) {
            logger.trace("params {}", corpTransRequest);
            corpTransferRequestDTO = saveTransfer(convertEntityToDTO(corpTransRequest));


            if (corpTransRequest.getStatus() != null) {
//                if (corpTransRequest.getStatus().equals("000") || corpTransRequest.getStatus().equals("00"))
                    return corpTransferRequestDTO;
//                throw new InternetBankingTransferException(corpTransRequest.getStatusDescription());
            }
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
    }

    @Override
    public Page<CorpTransRequest> getCompletedTransfers(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateAndStatusInAndTranDateNotNullOrderByTranDateDesc(corporate, Arrays.asList("00", "000"), pageDetails);
        //List<CorpTransferRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//        long t = page.getTotalElements();
//        Page<CorpTransferRequestDTO> pageImpl = new PageImpl<CorpTransferRequestDTO>(dtOs, pageDetails, t);
        return page;
    }

    @Override
    public Page<CorpTransRequest> findCompletedTransfers(String pattern, Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateAndStatusInAndTranDateNotNullOrderByTranDateDesc(corporate, Arrays.asList("00", "000"), pageDetails);
        return page;
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
            logger.error("Exception occurred", e);
        }
        return result;
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

        String cif = accountService.getAccountByAccountNumber(dto.getCustomerAccountNumber()).getCustomerId();
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
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
        Page<CorpTransRequest> corpTransRequests = corpTransferRequestRepo.findByCorporateOrderByTranDateDesc(corporate, pageDetails);
        return corpTransRequests;
    }

    @Override
    public int countPendingRequest(){
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        return corpTransferRequestRepo.countByCorporateAndStatus(corporate, "P");
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
        String bvn = integrationService.viewCustomerDetails(dto.getCustomerAccountNumber()).getBvn();
        if (bvn == null || bvn.isEmpty() || bvn.equalsIgnoreCase(""))
            throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());


        if (!integrationService.viewAccountDetails(dto.getCustomerAccountNumber()).getAcctStatus().equalsIgnoreCase("A"))
            throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());


        if (!NumberUtils.isNumber(dto.getAmount().toString()) || dto.getAmount().compareTo(new BigDecimal(0)) == 0)
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

        if (transferRule == null) {
            return false;
        }

        List<CorporateRole> roles = getExistingRoles(transferRule.getRoles());

        for (CorporateRole corporateRole : roles) {
            if (corpRoleRepo.countInRole(corporateRole, corporateUser) > 0) {
                return true;
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


        try {

            transReqEntry.setEntryDate(new Date());
            transReqEntry.setRole(userRole);
            transReqEntry.setUser(corporateUser);
            transReqEntry.setStatus("Approved");


            if (isAuthorizationComplete(corpTransRequest)) {
                CorpTransferRequestDTO requestDTO = makeTransfer(convertEntityToDTO(corpTransRequest));

                if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) {//successful transaction

                    transferAuth.setStatus("C");
                    transferAuth.setLastEntry(new Date());
                    transferAuth.getAuths().add(transReqEntry);
                    transferAuthRepo.save(transferAuth);
                    return requestDTO.getStatusDescription();

                } else {//failed transaction
                    logger.debug("TransReqEntry {}",transReqEntry.toString());
                    throw new InternetBankingTransferException(String.format(messageSource.getMessage("transfer.auth.failure.reason", null, locale), requestDTO.getStatusDescription()));
                }
            }
            transferAuth.getAuths().add(transReqEntry);
            transferAuthRepo.save(transferAuth);
            return messageSource.getMessage("transfer.auth.success", null, locale);
        } catch (InternetBankingTransferException te) {
            throw te;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.success", null, locale), e);
        }
    }



    private boolean isAuthorizationComplete(CorpTransRequest transRequest) {
        CorpTransferAuth transferAuth = transRequest.getTransferAuth();
        Set<CorpTransReqEntry> transReqEntries = transferAuth.getAuths();
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(transRequest);
        List<CorporateRole> roles = getExistingRoles(corpTransRule.getRoles());
        boolean any = false;
        int approvalCount = 1;

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

        if (any && (approvalCount >= numAuthorizers)) return true;

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
