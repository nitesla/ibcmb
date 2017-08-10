package longbridge.services.implementations;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.TransferType;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;
    private AccountService accountService;
    private FinancialInstitutionService financialInstitutionService;
    private ConfigurationService configService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private CorpTransferAuthRepo transferAuthRepo;

    @Autowired
    private PendingAuthorizationRepo pendAuthRepo;
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
    public CorpTransferServiceImpl(CorpTransferRequestRepo corpTransferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService, ConfigurationService configService) {
        this.corpTransferRequestRepo = corpTransferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
        this.configService = configService;
    }


    @Override
    @Transactional
    public String addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

        CorpTransRequest transferRequest = convertDTOToEntity(transferRequestDTO);


        if ("SOLE".equals(transferRequest.getCorporate().getCorporateType())) {
            makeTransfer(transferRequestDTO);
            return messageSource.getMessage("transaction.success", null, locale);
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
            corpTransferRequestRepo.save(transferRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternetBankingTransferException();
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
                if (corpTransRequest.getStatus().equals("000") || corpTransRequest.getStatus().equals("00"))
                    return corpTransferRequestDTO;
                throw new InternetBankingTransferException(corpTransRequest.getStatusDescription());
            }
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
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
            logger.error("Exception occurred {}", e.getMessage());
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
        Page<CorpTransRequest> corpTransRequests = corpTransferRequestRepo.findByCorporateAndStatus(corporate,"P", pageDetails);
        return corpTransRequests;
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
        if (dto.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || dto.getTransferType().equals(TransferType.CORONATION_BANK_TRANSFER)) {
            if (integrationService.viewAccountDetails(dto.getBeneficiaryAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
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

        List<CorporateRole> roles = transferRule.getRoles();

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
            throw new InternetBankingException("User is not authorized to approve the transaction");
        }

        CorpTransferAuth transferAuth = corpTransRequest.getTransferAuth();

        if (reqEntryRepo.existsByTranReqIdAndRole(corpTransRequest.getId(), userRole)) {
            throw new InternetBankingException("User has already authorized the transaction");
        }

        if (!"P".equals(transferAuth.getStatus())) {
            throw new InternetBankingException("Transaction is not pending");
        }
        transReqEntry.setEntryDate(new Date());
        transReqEntry.setRole(userRole);
        transReqEntry.setUser(corporateUser);
        transReqEntry.setStatus("Approved");
        transferAuth.getAuths().add(transReqEntry);
        transferAuthRepo.save(transferAuth);
        if (isAuthorizationComplete(corpTransRequest)) {
            transferAuth.setStatus("C");
            transferAuth.setLastEntry(new Date());
            transferAuthRepo.save(transferAuth);
            CorpTransferRequestDTO dto=    makeTransfer(convertEntityToDTO(corpTransRequest));
            return dto.getStatusDescription();

        }

        return "Authorization added successfully to the transaction request";
    }

    private boolean isAuthorizationComplete(CorpTransRequest transRequest) {
        CorpTransferAuth transferAuth = transRequest.getTransferAuth();
        Set<CorpTransReqEntry> transReqEntries = transferAuth.getAuths();
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(transRequest);
        List<CorporateRole> roles = getExistingRoles(corpTransRule.getRoles());
        boolean any = false;
        int approvalCount = 0;

        if (corpTransRule.isAnyCanAuthorize()) {
            any = true;
        }

        for (CorporateRole role : roles) {
            for (CorpTransReqEntry corpTransReqEntry : transReqEntries) {
                if (corpTransReqEntry.getRole().equals(role)) {
                    approvalCount++;
                    if (any) return true;
                }
            }
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
