package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.CorpInternationalTransferRequestDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.apidtos.NeftResponseDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.response.NeftResponse;
import longbridge.security.IpAddressUtils;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.SessionUtil;
import longbridge.utils.StatusCode;
import longbridge.utils.TransferAuthorizationStatus;
import longbridge.utils.TransferType;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
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
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/20/2017.
 */

@Service
public class CorpTransferServiceImpl implements CorpTransferService {

    private final NeftTransferRepo neftTransferRepo;
    private final CorpTransferRequestRepo corpTransferRequestRepo;
    private final IntegrationService integrationService;
    private final TransactionLimitServiceImpl limitService;
    private final AccountService accountService;
    private final ConfigurationService configService;
    private final DirectDebitService directDebitService;
    private final NeftResponseRepo neftResponseRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private CorpNeftTransferRepo corpNeftTransferRepo;

    @Autowired
    private CorpTransferAuthRepo transferAuthRepo;

    @Autowired
    private QuickBeneficiaryRepo quickBeneficiaryRepo;

    @Autowired
    private QuickSenderRepo quickSenderRepo;

    @Autowired
    private QuickInitiationRepo quickInitiationRepo;

    @Autowired
    private QuickTerminationRepo quickTerminationRepo;

    @Autowired
    private AntiFraudRepo antiFraudRepo;

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
    private AccountRepo accountRepo;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransferErrorService transferErrorService;

    @Autowired
    private QuicktellerBankCodeService quicktellerBankCodeService;


    @Autowired
    IpAddressUtils ipAddressUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Value("${MICRRepairInd}")
    private String micrRepairInd;

    @Value("${bankSortCode}")
    private String bankSortCode;

    private final SessionUtil sessionUtil;

    @Autowired
    public CorpTransferServiceImpl(CorpTransferRequestRepo corpTransferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService,
                                   AccountService accountService, QuicktellerBankCodeService quicktellerBankCodeService, ConfigurationService configService,
                                   DirectDebitService directDebitService, SessionUtil sessionUtil, NeftTransferRepo neftTransferRepo, NeftResponseRepo neftResponseRepo, AccountRepo accountRepo) {
        this.corpTransferRequestRepo = corpTransferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.accountService = accountService;
        this.configService = configService;
        this.quicktellerBankCodeService =  quicktellerBankCodeService;
        this.sessionUtil = sessionUtil;
        this.directDebitService = directDebitService;
        this.neftTransferRepo = neftTransferRepo;
        this.neftResponseRepo = neftResponseRepo;
        this.accountRepo = accountRepo;
    }





    private String getUserBvn(String accountnumber){
        Account account = accountRepo.findByAccountNumber(accountnumber);
        String customerId = account.getCustomerId();
        logger.info("Customer Id is {}", customerId);
        Corporate corporateUser = corporateRepo.findByCustomerId(customerId);
        logger.info("Corporate Id is {}", corporateUser);
        return corporateUser.getBvn();
    }


    private NeftTransfer pfDataItemStore(CorpTransferRequestDTO neftTransferDTO){
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        NeftTransfer neftTransfer = new NeftTransfer();
        String bvn = getUserBvn(neftTransferDTO.getCustomerAccountNumber());
        logger.info("corporate user bvn = [{}]", bvn);
        neftTransfer.setAccountNo(neftTransferDTO.getCustomerAccountNumber());
        neftTransfer.setBeneficiaryAccountNo(neftTransferDTO.getBeneficiaryAccountNumber());
        neftTransfer.setBeneficiary(neftTransferDTO.getBeneficiaryAccountName());
        neftTransfer.setAmount(neftTransferDTO.getAmount());
        neftTransfer.setCurrency(neftTransferDTO.getCurrencyCode());
        neftTransfer.setNarration(neftTransferDTO.getNarration());
        neftTransfer.setSpecialClearing(false);
        neftTransfer.setBVNBeneficiary(neftTransferDTO.getBeneficiaryBVN());
        neftTransfer.setBankOfFirstDepositSortCode(bankSortCode);
        neftTransfer.setCollectionType(neftTransferDTO.getCollectionType());
        neftTransfer.setBVNPayer(bvn);
        neftTransfer.setPayerName(neftTransferDTO.getPayerName());
        neftTransfer.setInstrumentType(neftTransferDTO.getInstrumentType());
        neftTransfer.setMICRRepairInd(micrRepairInd);
        neftTransfer.setSettlementTime("not settled");
        neftTransfer.setCycleNo("01");
        neftTransfer.setPresentingBankSortCode(bankSortCode);
        neftTransfer.setSortCode(neftTransferDTO.getBeneficiarySortCode());
        neftTransfer.setTranCode("20");
        neftTransfer.setSerialNo("");
        neftTransfer.setCorporate(corporate);
        neftTransfer.setStatus("PENDING");
        neftTransfer.setBeneficiaryBank(neftTransferDTO.getBeneficiaryBank());
        return neftTransferRepo.save(neftTransfer);
    }

    @Override
    public CorpTransferRequestDTO makeNeftBulkTransfer(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {

            logger.info("transferType from service layer is {}", transferRequestDTO.getTransferType());
            pfDataItemStore(transferRequestDTO);
//            CorpTransRequest  transRequest2 = persistTransfer(transferRequestDTO);
            logger.info("uniqueid {}", transferRequestDTO);
        transferRequestDTO.setStatus("PENDING");
//            corpTransferRequestRepo.save(transRequest2);

            return transferRequestDTO;

    }


    @Override
    public Object addTransferRequest(CorpTransferRequestDTO transferRequestDTO) throws InternetBankingException {
        logger.info("TRANSFER TYPE {}", transferRequestDTO.getTransferType());

        CorpTransRequest transferRequest = convertDTOToEntity(transferRequestDTO);
        String userRefereneceNumber = "CORP_" + getCurrentUser().getId().toString();
        transferRequest.setUserReferenceNumber(userRefereneceNumber);
        transferRequestDTO.setUserReferenceNumber(userRefereneceNumber);
        String sessId = sessionUtil.generateSessionId();
        transferRequest.setReferenceNumber(sessId);
        transferRequestDTO.setReferenceNumber(sessId);


        if ("SOLE".equals(transferRequest.getCorporate().getCorporateType())) {
            CorpTransferRequestDTO requestDTO = makeTransfer(transferRequestDTO);
            if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) { // Transfer successful
                logger.debug("returning payment Request Sent for sole:{}", transferRequest.getCorporate().getCorporateType());
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
            logger.info("Corp-Transfer request details {}", transferRequest);
            CorpTransRequest corpTransRequest = corpTransferRequestRepo.save(transferRequest);
            logger.info("Corp-Transfer request response {}", corpTransRequest);
            logger.info("Transfer request saved for authorization");
            if (userCanAuthorize(corpTransRequest)) {
                CorpTransReqEntry transReqEntry = new CorpTransReqEntry();
                transReqEntry.setTranReqId(corpTransRequest.getId());
                transReqEntry.setAuthStatus(TransferAuthorizationStatus.APPROVED);
                return addAuthorization(transReqEntry);
            }
        } catch (TransferAuthorizationException | InternetBankingTransferException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternetBankingTransferException(messageSource.getMessage("transfer.add.failure", null, locale), e);
        }
        return messageSource.getMessage("transfer.add.success", null, locale);
    }


    private CorpTransferRequestDTO makeTransfer(CorpTransferRequestDTO corpTransferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(corpTransferRequestDTO);
        NeftTransfer neftTransfer = new NeftTransfer();

        logger.trace("Initiating {} transfer to {} by {}", corpTransferRequestDTO.getTransferType(), corpTransferRequestDTO.getBeneficiaryAccountName(), corpTransferRequestDTO.getUserReferenceNumber());
        CorpTransRequest corpTransRequest = persistTransfer(corpTransferRequestDTO);
        logger.info("the corporate transfer request {}", corpTransRequest);

        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        String sessionkey = ((WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails())
                .getSessionId();

        AntiFraudData antiFraudData = new AntiFraudData();
        antiFraudData.setIp(ipAddressUtils.getClientIP2());
        antiFraudData.setCountryCode("");
        antiFraudData.setSfactorAuthIndicator(user.getSfactorAuthIndicator());
        antiFraudData.setHeaderUserAgent(httpServletRequest.getHeader("User-Agent"));
        antiFraudData.setHeaderProxyAuthorization(httpServletRequest.getHeader("Proxy-Authorization"));
        if (antiFraudData.getHeaderProxyAuthorization() == null) {
            antiFraudData.setHeaderProxyAuthorization(httpServletRequest.getHeader("Proxy-Authenticate"));
            if (antiFraudData.getHeaderProxyAuthorization() == null) {
                antiFraudData.setHeaderProxyAuthorization("");
            }
        }

        antiFraudData.setLoginName(user.getUsername());
        antiFraudData.setDeviceNumber("");
        antiFraudData.setSessionkey(sessionkey);
        antiFraudData.setTranLocation("");
        antiFraudData.setChannel("INTERNET");
        corpTransRequest.setChannel("INTERNET");
        antiFraudRepo.save(antiFraudData);

        corpTransRequest.setAntiFraudData(antiFraudData);


//        corpTransRequest.getAntiFraudData().setChannel(corpTransRequest.getChannel());
        if (corpTransferRequestDTO.getTransferType() == TransferType.NEFT){
            neftTransfer = pfDataItemStore(corpTransferRequestDTO);
            try {
                NeftResponseDTO responseDTO = integrationService.submitInstantNeftTransfer(neftTransfer);
                NeftResponse neftResponse = neftResponseRepo.save(convertResponseToEntity(responseDTO));
                neftTransfer.setNeftResponse(neftResponse);
                neftTransferRepo.save(neftTransfer);
                getCorpTransferRequestDTO(corpTransRequest);
            }catch (InternetBankingTransferException ex){
                throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
            }


        }

        CorpTransRequest corpTransRequestNew = (CorpTransRequest) integrationService.makeTransfer(corpTransRequest);//name change by GB
        logger.trace("Transfer Details {} by {}", corpTransRequestNew.toString(), corpTransRequestNew.getUserReferenceNumber());

        if (corpTransRequestNew != null ) {
            CorpTransRequest corpTransRequest1 = corpTransferRequestRepo.findById(corpTransRequest.getId()).get();
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
        throw new InternetBankingTransferException(messageSource.getMessage("transfer.failed", null, locale));
    }

    private CorpTransferRequestDTO getCorpTransferRequestDTO(CorpTransRequest corpTransRequest) {
        CorpTransferRequestDTO corpTransferRequestDTO;
        CorpTransRequest corpTransRequest1 = corpTransferRequestRepo.findById(corpTransRequest.getId()).get();
        entityManager.detach(corpTransRequest1);
        corpTransRequest1.setReferenceNumber(corpTransRequest.getReferenceNumber());
        corpTransRequest1.setNarration(corpTransRequest.getNarration());
        corpTransRequest1.setStatus("00");
        corpTransRequest1.setStatusDescription("Transaction Authorized");

        corpTransferRequestRepo.save(corpTransRequest1);
        corpTransferRequestDTO = convertEntityToDTO(corpTransRequest1);

        if (corpTransRequest1.getStatus() != null) {
            return corpTransferRequestDTO;
        }
        throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
    }

    @Override
    public Page<CorpTransRequest> getCompletedTransfers(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        logger.info("corporate:{}", corporate);
//        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateAndStatusInAndTranDateNotNullOrderByTranDateDesc(corporate, Arrays.asList("00", "000"), pageDetails);
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByUserReferenceNumberAndTranDateNotNullOrderByTranDateDesc("CORP_" + corporateUser.getId(), pageDetails);
        logger.info("Page<CorpTransRequest> count:{}", pageDetails.getPageSize());
//        List<CorpTransRequest> corpTransRequests = page.getContent().stream()
//                .filter(transRequest -> !accountConfigService.isAccountRestrictedForViewFromUser(accountService.getAccountByAccountNumber(transRequest.getCustomerAccountNumber()).getId(), corporateUser.getId())).collect(Collectors.toList());
//        return new PageImpl<CorpTransRequest>(corpTransRequests, pageDetails, page.getTotalElements());
        return page;
    }

    @Override
    public Page<CorpTransferRequestDTO> getCompletedTransfer(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        logger.info("id for corpUser : {}",corporateUser.getId());
        logger.info("id for corp : {}",corporate.getId());
      // Page<CorpTransRequest> page = corpTransferRequestRepo.findByUserReferenceNumberAndTranDateNotNullOrderByTranDateDesc(corporate, pageDetails);
        Page<CorpTransRequest> page = corpTransferRequestRepo.findRequestByCorp(corporate, pageDetails);
        List<CorpTransferRequestDTO> corpTransferRequestDTOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(corpTransferRequestDTOs, pageDetails, t);
    }

    @Override
    public Page<CorpTransRequest> getCompletedTransfers(String pattern, Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        //        List<AdminUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//        long t = page.getTotalElements();
//        Page<AdminUserDTO> pageImpl = new PageImpl<AdminUserDTO>(dtOs, pageDetails, t);
        return corpTransferRequestRepo.findUsingPattern(corporate, pattern, pageDetails);
    }

    @Override
    public Page<CorpTransferRequestDTO> getCompletedTransfer(String pattern, Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpTransRequest> page = corpTransferRequestRepo.findUsingPattern(corporate, pattern, pageDetails);
        List<CorpTransferRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<CorpTransferRequestDTO>(dtOs, pageDetails, t);
    }


    @Override
    public CorpTransRequest getTransfer(Long id) {
        logger.info("transfer authorization details ================================== {} " , corpTransferRequestRepo.findById(id));
        return corpTransferRequestRepo.findById(id).get();
    }



    @Override
    public CorpTransferRequestDTO entityToDTO(CorpTransRequest corpTransRequest){
      return convertEntityToDTO(corpTransRequest);
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
    }


    private void validateBalance(CorpTransferRequestDTO corpTransferRequest) throws InternetBankingTransferException {

        BigDecimal balance = integrationService.getAvailableBalance(corpTransferRequest.getCustomerAccountNumber());
        if (balance != null) {
            if (!(balance.compareTo(corpTransferRequest.getAmount()) == 0 || (balance.compareTo(corpTransferRequest.getAmount()) > 0))) {
//                throw new InternetBankingTransferException(TransferExceptions.BALANCE.toString());
                throw new InternetBankingTransferException(messageSource.getMessage("transfer.balance.insufficient", null, locale));
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

        Corporate corporate = corporateRepo.findById(getCurrentUser().getCorporate().getId()).get();
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
//        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateOrderByStatusAscTranDateDesc(corporate, pageDetails);//GB
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateOrderByTranDateDesc(corporate, pageDetails);//GB
        List<CorpTransRequest> corpTransRequests = page.getContent().stream()
                .filter(transRequest -> !accountConfigService.isAccountRestrictedForViewFromUser(accountService.getAccountByAccountNumber(transRequest.getCustomerAccountNumber()).getId(), corporateUser.getId())).collect(Collectors.toList());

        return new PageImpl<>(corpTransRequests, pageDetails, page.getTotalElements());

    }
    @Override
    public Page<CorpTransferRequestDTO> getTransferRequest(Pageable pageDetails) {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        Page<CorpTransRequest> page = corpTransferRequestRepo.findByCorporateOrderByTranDateDesc(corporate, pageDetails);//GB

        List<CorpTransRequest> corpTransRequests = page.getContent().stream()
                .filter(transRequest -> !accountConfigService.isAccountRestrictedForViewFromUser(accountService.getAccountByAccountNumber(transRequest.getCustomerAccountNumber()).getId(),corporateUser.getId())).collect(Collectors.toList());
        return new PageImpl<>(convertEntitiesToDTOs(corpTransRequests), pageDetails, page.getTotalElements());

    }



    @Override
    public int countPendingRequest() {
        CorporateUser corporateUser = getCurrentUser();
        Corporate corporate = corporateUser.getCorporate();
        return corpTransferRequestRepo.countByCorporateAndStatus(corporate, StatusCode.PENDING.toString());
    }



    private CorpTransferRequestDTO convertEntityToDTO(CorpTransRequest corpTransRequest) {
        CorpTransferRequestDTO transferRequestDTO = new CorpTransferRequestDTO();
        transferRequestDTO.setId(corpTransRequest.getId());
        transferRequestDTO.setVersion(corpTransRequest.getVersion());
        transferRequestDTO.setCustomerAccountNumber(corpTransRequest.getCustomerAccountNumber());
        transferRequestDTO.setTransferType(corpTransRequest.getTransferType());
        transferRequestDTO.setFinancialInstitution(corpTransRequest.getFinancialInstitution());
        if (corpTransRequest.getBeneficiaryBank() != null) {
            transferRequestDTO.setBeneficiaryBank(corpTransRequest.getBeneficiaryBank());
        }else{
            transferRequestDTO.setBeneficiaryBank(corpTransRequest.getFinancialInstitution()!=null?corpTransRequest.getFinancialInstitution().getInstitutionName():"");

        }
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
        transferRequestDTO.setCurrencyCode(corpTransRequest.getCurrencyCode());


        if (corpTransRequest.getQuickBeneficiary() != null) {
            transferRequestDTO.setLastname(corpTransRequest.getQuickBeneficiary().getLastname());
            transferRequestDTO.setFirstname(corpTransRequest.getQuickBeneficiary().getOthernames());
//        transferRequestDTO.setAmount(corpTransRequest.getQuickInitiation().getAmount());
//            transferRequestDTO.setBeneficiaryAccountNumber(corpTransRequest.getQuickBeneficiary().getAccountNumber());
        }
        if (corpTransRequest.getTransferAuth() != null) {
            transferRequestDTO.setTransAuthId(corpTransRequest.getTransferAuth().getId().toString());
        }

        if(corpTransRequest instanceof CorpInternationalTransfer){
            CorpInternationalTransferRequestDTO internationalTransfer = modelMapper.map(transferRequestDTO,CorpInternationalTransferRequestDTO.class);
            CorpInternationalTransfer corpInternationalTransfer = (CorpInternationalTransfer)corpTransRequest;
            internationalTransfer.setBeneficiaryBank(corpInternationalTransfer.getBeneficiaryBank());
            internationalTransfer.setSwiftCode(corpInternationalTransfer.getSwiftCode());
            internationalTransfer.setSortCode(corpInternationalTransfer.getSortCode());
            internationalTransfer.setBeneficiaryAddress(corpInternationalTransfer.getBeneficiaryAddress());
            internationalTransfer.setIntermediaryBankAcctNo(corpInternationalTransfer.getIntermediaryBankAcctNo());
            internationalTransfer.setIntermediaryBankName(corpInternationalTransfer.getIntermediaryBankName());
            internationalTransfer.setCurrencyCode(corpInternationalTransfer.getCurrencyCode());
            internationalTransfer.setChargeFrom(corpInternationalTransfer.getChargeFrom());
            return internationalTransfer;
        }
        return transferRequestDTO;

    }

    /*public CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO transferRequestDTO) {
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
        Corporate corporate = corporateRepo.findById(Long.parseLong(transferRequestDTO.getCorporateId())).get();
        corpTransRequest.setCorporate(corporate);
        if (transferRequestDTO.getTransAuthId() != null) {
            CorpTransferAuth transferAuth = transferAuthRepo.findById(Long.parseLong(transferRequestDTO.getTransAuthId())).get();
            corpTransRequest.setTransferAuth(transferAuth);
        }
        corpTransRequest.setCorpDirectDebit(null);

        return corpTransRequest;
    }*/

    private CorpTransRequest convertDTOToEntity(CorpTransferRequestDTO transferRequestDTO) {
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
        corpTransRequest.setUserReferenceNumber(transferRequestDTO.getUserReferenceNumber());
        corpTransRequest.setNarration(transferRequestDTO.getNarration());
        corpTransRequest.setStatusDescription(transferRequestDTO.getStatusDescription());
        corpTransRequest.setAmount(transferRequestDTO.getAmount());
        corpTransRequest.setUserReferenceNumber(transferRequestDTO.getUserReferenceNumber());
        corpTransRequest.setCurrencyCode(transferRequestDTO.getCurrencyCode());
        corpTransRequest.setBeneficiaryBank((transferRequestDTO.getBeneficiaryBank()));

        if (transferRequestDTO.getTransferType() == TransferType.QUICKTELLER) {
            QuickBeneficiary quickBeneficiary = new QuickBeneficiary();
        quickBeneficiary.setLastname(transferRequestDTO.getLastname());
        quickBeneficiary.setOthernames(transferRequestDTO.getFirstname());
        quickBeneficiaryRepo.save(quickBeneficiary);
        corpTransRequest.setQuickBeneficiary(quickBeneficiary);

        QuickInitiation quickInitiation = new QuickInitiation();
        BigDecimal a = transferRequestDTO.getAmount();
        BigDecimal b = new BigDecimal(100);
        BigDecimal initiationAmount = a.multiply(b);
        quickInitiation.setAmount(initiationAmount);
        quickInitiation.setChannel("7");
        quickInitiation.setCurrencyCode("566");
        quickInitiation.setPaymentMethodCode("CA");
        quickInitiationRepo.save(quickInitiation);
        corpTransRequest.setQuickInitiation(quickInitiation);

        QuickSender quickSender = new QuickSender();
        quickSender.setEmail(getCurrentUser().getEmail());
        quickSender.setLastname(getCurrentUser().getLastName());
        quickSender.setOthernames(getCurrentUser().getFirstName());
        quickSender.setPhone(getCurrentUser().getPhoneNumber());
        quickSenderRepo.save(quickSender);
        corpTransRequest.setQuickSender(quickSender);


        QuickTermination quickTermination = new QuickTermination();

        AccountReceivable accountReceivable = new AccountReceivable();
        accountReceivable.setAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
        accountReceivable.setAccountType("00");

        quickTermination.setAccountReceivable(accountReceivable);
        BigDecimal c = transferRequestDTO.getAmount();
        BigDecimal d = new BigDecimal(100);
        BigDecimal terminatingAmount = c.multiply(d);
        quickTermination.setAmount(terminatingAmount);
        quickTermination.setCountryCode("NG");
        quickTermination.setCurrencyCode("566");
        quickTermination.setEntityCode("");
        quickTermination.setPaymentMethodCode("AC");
        quickTerminationRepo.save(quickTermination);
        corpTransRequest.setQuickTermination(quickTermination);

        Random rand = new Random();
        int upperbound = 9999999;
        int random = rand.nextInt(upperbound);
        corpTransRequest.setTransferCode("1453" + random);

        }
        Corporate corporate = corporateRepo.findOneById(Long.parseLong(transferRequestDTO.getCorporateId()));
        corpTransRequest.setCorporate(corporate);
        if (transferRequestDTO.getTransAuthId() != null) {
            CorpTransferAuth transferAuth = transferAuthRepo.findById(Long.parseLong(transferRequestDTO.getTransAuthId())).get();
            corpTransRequest.setTransferAuth(transferAuth);
        }
        corpTransRequest.setCorpDirectDebit(null);

        if(transferRequestDTO instanceof CorpInternationalTransferRequestDTO){
            CorpInternationalTransfer internationalTransfer = modelMapper.map(corpTransRequest,CorpInternationalTransfer.class);
            CorpInternationalTransferRequestDTO internationalTransferRequestDTO = (CorpInternationalTransferRequestDTO)transferRequestDTO;
            internationalTransfer.setBeneficiaryBank(internationalTransferRequestDTO.getBeneficiaryBank());
            internationalTransfer.setSwiftCode(internationalTransferRequestDTO.getSwiftCode());
            internationalTransfer.setSortCode(internationalTransferRequestDTO.getSortCode());
            internationalTransfer.setBeneficiaryAddress(internationalTransferRequestDTO.getBeneficiaryAddress());
            internationalTransfer.setIntermediaryBankAcctNo(internationalTransferRequestDTO.getIntermediaryBankAcctNo());
            internationalTransfer.setIntermediaryBankName(internationalTransferRequestDTO.getIntermediaryBankName());
            internationalTransfer.setCurrencyCode(internationalTransferRequestDTO.getCurrencyCode());
            internationalTransfer.setChargeFrom(internationalTransferRequestDTO.getChargeFrom());
            internationalTransfer.setReferenceNumber(internationalTransferRequestDTO.getReferenceNumber());
            return internationalTransfer;
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
        CorpTransRequest corpTransRequest = corpTransferRequestRepo.findById(transRequest.getId()).get();
        return corpTransRequest.getTransferAuth();

    }


    public boolean userCanAuthorize(TransRequest transRequest) {
        CorporateUser corporateUser = getCurrentUser();
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(transRequest);
        CorpTransRequest transferRequest = (CorpTransRequest) transRequest;

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
        CorpTransRequest corpTransRequest = corpTransferRequestRepo.findById(transReqEntry.getTranReqId()).get();
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

        if (TransferAuthorizationStatus.DECLINED.equals(transReqEntry.getAuthStatus())) {
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
                    if (corpTransRequest.getCorpDirectDebit() == null) {
                        CorpTransferRequestDTO corpTransferRequestDTO = convertEntityToDTO(corpTransRequest);
                        corpTransferRequestDTO.setChannel(transReqEntry.getChannel());
                        corpTransferRequestDTO.setTranLocation(transReqEntry.getTranLocation());

                        CorpTransferRequestDTO requestDTO = makeTransfer(corpTransferRequestDTO);
                        logger.info("maketransfer details {}", requestDTO);
                        logger.info("status  {}", requestDTO.getStatus());

                        if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) {//successful transaction

                            transferAuth.setStatus("C");
                            transferAuth.setLastEntry(new Date());
                            transferAuth.getAuths().add(transReqEntry);// the entry is added to the list of other entries
                            transferAuthRepo.save(transferAuth);
                            return requestDTO.getStatusDescription();

                        }
                        if ("34".equals(requestDTO.getStatus())) {
                            requestDTO.setStatusDescription(messageSource.getMessage(transferErrorService.getMessage(requestDTO.getStatus()), null, locale));
                            return requestDTO.getStatusDescription();

                        }
                        if ("09".equals(requestDTO.getStatus())) {
                            requestDTO.setStatusDescription(messageSource.getMessage(transferErrorService.getMessage(requestDTO.getStatus()), null, locale));
                            return requestDTO.getStatusDescription();

                        } else {//failed transaction
                            return messageSource.getMessage(transferErrorService.getMessage(requestDTO.getStatus()), null, locale);//GB

//                        throw new InternetBankingTransferException(String.format(messageSource.getMessage("transfer.auth.failure.reason", null, locale), requestDTO.getStatusDescription()));
                        }
                    } else {
                        logger.info("transfer request is a standing order , about to generate payments for standing order ");
                        CorpDirectDebit corpDirectDebit = corpTransRequest.getCorpDirectDebit();
                        directDebitService.generatePaymentsForDirectDebit(corpDirectDebit);
                        transferAuth.setStatus("C");
                        transferAuth.setLastEntry(new Date());
                        transferAuth.getAuths().add(transReqEntry);// the entry is added to the list of other entries
                        transferAuthRepo.save(transferAuth);
                        corpTransRequest.setStatus("000");
                        corpTransRequest.setStatusDescription("Standing order successfull");
                        corpTransferRequestRepo.save(corpTransRequest);
                        return messageSource.getMessage("directdebit.add.success", null, locale);

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
                    if(corpTransRequest.getCorpDirectDebit() == null ) {
                        logger.info("transfer request not a standing order !");
                        CorpTransferRequestDTO corpTransferRequestDTO = convertEntityToDTO(corpTransRequest);
                        corpTransferRequestDTO.setTranLocation(transReqEntry.getTranLocation());
                        corpTransferRequestDTO.setChannel(transReqEntry.getChannel());

                        CorpTransferRequestDTO requestDTO = makeTransfer(corpTransferRequestDTO);
//                    CorpTransferRequestDTO requestDTO = makeTransfer(convertEntityToDTO(corpTransRequest));
                        if ("00".equals(requestDTO.getStatus()) || "000".equals(requestDTO.getStatus())) { //successful transaction
                            return requestDTO.getStatusDescription();
                        } else {
                            throw new InternetBankingTransferException(requestDTO.getStatusDescription());
                        }
                    }else{
                        logger.info("transfer request is a standing order , about to generate payments for standing order ");
                        CorpDirectDebit corpDirectDebit = corpTransRequest.getCorpDirectDebit() ;
                        directDebitService.generatePaymentsForDirectDebit(corpDirectDebit);
                        transferAuth.setStatus("C");
                        transferAuth.setLastEntry(new Date());
                        transferAuth.getAuths().add(transReqEntry);// the entry is added to the list of other entries
                        transferAuthRepo.save(transferAuth);
                        corpTransRequest.setStatus("000");
                        corpTransRequest.setStatusDescription("Standing order successfull");
                        corpTransferRequestRepo.save(corpTransRequest);
                        return messageSource.getMessage("directdebit.add.success", null, locale);

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
        return (CorporateUser) principal.getUser();
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

    private NeftResponse convertResponseToEntity(NeftResponseDTO neftResponseDTO){
        NeftResponse n = new NeftResponse();
        n.setAppId(neftResponseDTO.getAppId());
        n.setItemCount(neftResponseDTO.getItemCount());
        n.setMsgId(neftResponseDTO.getMsgId());
        n.setResponseCode(neftResponseDTO.getResponseCode());
        n.setResponseMessage(neftResponseDTO.getResponseMessage());
        return n;
    }

    @Override
    public boolean validateDirectDebitAccountBalance(CorpTransferRequestDTO dto) throws InternetBankingTransferException {

        BigDecimal balance = integrationService.getAvailableBalance(dto.getCustomerAccountNumber());
        if (balance != null) {
            if (!(balance.compareTo(dto.getAmount()) == 0 || (balance.compareTo(dto.getAmount()) > 0))) {
                logger.info("Account Balance is insufficient for this transfer {}", dto.getCustomerAccountNumber());
                return false;
            }
        }
        return true;
    }

    private CorporateUser getCurrentCorpUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (CorporateUser) principal.getUser();
    }
}
