package longbridge.services.implementations;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateRoleRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.PendingAuthorizationRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.ResultType;
import longbridge.utils.TransferType;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
    private ModelMapper modelMapper;
    private AccountService accountService;
    private FinancialInstitutionService financialInstitutionService;
    private ConfigurationService configService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

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


        if (transferRequest.getCorporate().getCorporateType().equals("SOLE")) {
            makeTransfer(transferRequestDTO);
            return messageSource.getMessage("transaction.success", null, locale);
        }

        if (corporateService.getCorporateRules(transferRequest.getCorporate().getId()).isEmpty()) {
            throw new TransferRuleException(messageSource.getMessage("rule.unavailable", null, locale));
        }
        if (corporateService.getQualifiedRoles(transferRequest).isEmpty()) {
            throw new NoDefinedRoleException(transferRequestDTO.getAmount());
        }

        try {

            transferRequest.setStatus("PENDING");
            List<CorporateRole> roles = corporateService.getQualifiedRoles(transferRequest);
            List<PendAuth> pendAuths = new ArrayList<>();
            for (CorporateRole role : roles) {
                PendAuth pendAuth = new PendAuth();
                pendAuth.setRole(role);
                pendAuths.add(pendAuth);
            }

            transferRequest.setPendAuths(pendAuths);
            corpTransferRequestRepo.save(transferRequest);

        } catch (Exception e) {
            throw new InternetBankingTransferException();
        }
        return messageSource.getMessage("transfer.add.success", null, locale);
    }


    public CorpTransferRequestDTO makeTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(corpTransferRequestDTO);
        logger.trace("Transfer details valid {}", corpTransferRequestDTO);
        CorpTransRequest corpTransRequest = (CorpTransRequest) integrationService.makeTransfer(convertDTOToEntity(corpTransferRequestDTO));
        if (corpTransRequest != null) {
            logger.trace("params {}", corpTransRequest);
            saveTransfer(convertEntityToDTO(corpTransRequest));
            if (corpTransRequest.getStatus().equals("000")||corpTransRequest.getStatus().equals("00")) return convertEntityToDTO(corpTransRequest);
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
        }
        throw new InternetBankingTransferException();
    }


    @Override
    public CorpTransRequest getTransfer(Long id) {
        return corpTransferRequestRepo.findById(id);
    }


    @Override

    public boolean saveTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        boolean result = false;
        try {
            CorpTransRequest transRequest = convertDTOToEntity(corpTransferRequestDTO);
            corpTransferRequestRepo.save(transRequest);
            result = true;

        } catch (Exception e) {
            logger.error("Exception occurred {}", e.getMessage());
        }
        return result;
    }




    public List<PendAuth> getPendingAuthorizations(){
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Set<CorporateRole> roles = corporate.getCorporateRoles();
        List<PendAuth> pendAuths = new ArrayList<>();
        for (CorporateRole role : roles) {
            if (!role.getPendAuths().isEmpty()) {
                Set<CorporateUser> users = role.getUsers();
                if (users.contains(corporateUser)) {
                    pendAuths = role.getPendAuths();
                }
            }
        }
        return pendAuths;
    }

    @Override
    @Transactional
    public String authorizeTransfer(Long authId) throws InternetBankingException {
        PendAuth pendAuth = pendAuthRepo.findOne(authId);

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CorporateUser corporateUser = (CorporateUser)principal.getUser();


        //TODO: get role user belongs to
//        if (!role.getPendAuths().contains(pendAuth)) {
//            throw new InvalidAuthorizationException(messageSource.getMessage("transfer.auth.invalid", null, locale));
//        }

        try {
            CorpTransRequest transferRequest = pendAuth.getCorpTransferRequest();

            CorpTransRule corporateTransferRule = corporateService.getApplicableTransferRule(transferRequest);

            if (corporateTransferRule.isAnyCanAuthorize()) {
                makeTransfer(convertEntityToDTO(transferRequest));
                transferRequest.getPendAuths().clear();
            }
            else{
                transferRequest.getPendAuths().remove(pendAuth);
                if (transferRequest.getPendAuths().isEmpty()) {
                    makeTransfer(convertEntityToDTO(transferRequest));
                }
            }
            corpTransferRequestRepo.save(transferRequest);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }

        return messageSource.getMessage("transfer.auth.success", null, locale);

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
        if(validateBalance()) {
            validateBalance(dto);
        }


    }
    private boolean validateBalance() {
        SettingDTO setting = configService.getSettingByName("ACCOUNT_BALANCE_VALIDATION");
        if(setting!=null&&setting.isEnabled()) {
            return ("YES".equals(setting.getValue()));
        }
        return true;
    }

    @Override
    public Page<CorpTransRequest> getTransfers( Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpTransRequest> corpTransRequests = corpTransferRequestRepo.findByCorporate(corporate,pageDetails);
        return corpTransRequests;
    }

    public CorpTransferRequestDTO convertEntityToDTO(CorpTransRequest corpTransRequest) {
        return modelMapper.map(corpTransRequest, CorpTransferRequestDTO.class);
    }


    public CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO corpTransferRequestDTO) {
        CorpTransRequest corpTransRequest =  modelMapper.map(corpTransferRequestDTO, CorpTransRequest.class);
        corpTransRequest.setCorporate(corporateRepo.findOne(Long.parseLong(corpTransferRequestDTO.getCorporateId())));
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

    private CorporateUser getCurrentUser(){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CorporateUser corporateUser = (CorporateUser)principal.getUser();
        return corporateUser;
    }
}
