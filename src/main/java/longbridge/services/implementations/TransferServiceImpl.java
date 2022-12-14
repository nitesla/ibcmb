package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.InternationalTransferRequestDTO;
import longbridge.dtos.NeftTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.dtos.apidtos.NeftResponseDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferExceptions;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.response.NeftResponse;
import longbridge.security.IpAddressUtils;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.SessionUtil;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static longbridge.utils.TransferType.INTER_BANK_TRANSFER;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {
    private final RetailUserRepo retailUserRepo;
    private final TransferRequestRepo transferRequestRepo;
    private final IntegrationService integrationService;
    private final TransactionLimitServiceImpl limitService;
    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final SettingsService configService;
    private final MessageSource messages;
    private final Locale locale = LocaleContextHolder.getLocale();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SessionUtil sessionUtil;


    @Autowired
    IpAddressUtils ipAddressUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private NeftTransferRepo neftTransferRepo;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private QuickBeneficiaryRepo quickBeneficiaryRepo;

    @Autowired
    private QuickTerminationRepo quickTerminationRepo;

    @Autowired
    private QuickInitiationRepo quickInitiationRepo;

    @Autowired
    private QuickSenderRepo quickSenderRepo;

    @Autowired
    private AntiFraudRepo antiFraudRepo;

    @Autowired
    private NeftResponseRepo neftResponseRepo;

    @Value("${MICRRepairInd}")
    private String micrRepairInd;

    @Value("${bankSortCode}")
    private String bankSortCode;

    @Autowired
    public TransferServiceImpl(TransferRequestRepo transferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService, SettingsService configurationService
            , RetailUserRepo retailUserRepo, MessageSource messages, SessionUtil sessionUtil) {
        this.transferRequestRepo = transferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.configService = configurationService;
        this.retailUserRepo = retailUserRepo;
        this.messages = messages;
        this.sessionUtil=sessionUtil;
        this.accountRepo=accountRepo;

    }

    private String getUserBvn(String accountnumber){
        Account account = accountRepo.findFirstByAccountNumber(accountnumber);
        String customerId = account.getCustomerId();
        RetailUser retailUser = retailUserRepo.findFirstByCustomerId(customerId);
        return retailUser.getBvn();
    }

    private NeftTransfer makepfDataItemStore(TransferRequestDTO neftTransferDTO){
        RetailUser user = getCurrentUser();
        NeftTransfer neftTransfer = new NeftTransfer();
        String bvn = getUserBvn(neftTransferDTO.getCustomerAccountNumber());
        neftTransfer.setAccountNo(neftTransferDTO.getCustomerAccountNumber());
        neftTransfer.setBeneficiaryAccountNo(neftTransferDTO.getBeneficiaryAccountNumber());
        neftTransfer.setBeneficiary(neftTransferDTO.getBeneficiaryAccountName());
        neftTransfer.setAmount(neftTransferDTO.getAmount());
        neftTransfer.setCurrency(neftTransferDTO.getCurrencyCode());
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
        neftTransfer.setNarration(neftTransferDTO.getNarration());
        neftTransfer.setPresentingBankSortCode(bankSortCode);
        neftTransfer.setSortCode(neftTransferDTO.getBeneficiarySortCode());
        neftTransfer.setTranCode("20");
        neftTransfer.setSerialNo("");
        neftTransfer.setRetailUser(user);
        neftTransfer.setStatus("PENDING");
        neftTransfer.setBeneficiaryBank(neftTransferDTO.getBeneficiaryBank());

        logger.info("Neft pfDataItemStore : {}", neftTransfer);
        return neftTransferRepo.save(neftTransfer);
    }





    @Override
    public TransferRequestDTO makeTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(transferRequestDTO);
        NeftTransfer neftTransfer = new NeftTransfer();
        if (transferRequestDTO.getTransferType() == TransferType.NEFT || transferRequestDTO.getTransferType() == TransferType.NEFT_BULK){
            logger.info("transferType from service layer is {}", transferRequestDTO.getTransferType());
             neftTransfer = makepfDataItemStore(transferRequestDTO);
        }
        
        logger.info("Initiating {} Transfer to {}", transferRequestDTO.getTransferType(), transferRequestDTO.getBeneficiaryAccountName());
        logger.info("Initiating Transfer to {}", transferRequestDTO);
        System.out.println("received request"+transferRequestDTO);
        TransRequest transRequest1 = convertDTOToEntity(transferRequestDTO);
        System.out.println("after request"+transRequest1);

        if(null==transferRequestDTO.getChannel()) {
            transRequest1.setChannel("MOBILE");
        }
        if("web".equals(transferRequestDTO.getChannel())) {
            CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();

            String sessionkey = ((WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails())
                    .getSessionId();

            AntiFraudData antiFraudData = new AntiFraudData();
            antiFraudData.setIp(ipAddressUtils.getClientIP());
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
            transRequest1.setChannel("INTERNET");
            antiFraudRepo.save(antiFraudData);
            transRequest1.setAntiFraudData(antiFraudData);

            logger.info("country code {}", antiFraudData.getCountryCode());
            logger.info("sfactorAuthIndicator {}", antiFraudData.getSfactorAuthIndicator());
            logger.info("UserAgent {}", antiFraudData.getHeaderUserAgent());
            logger.info("ip address {}", antiFraudData.getIp());
            logger.info("tranLocation {}", antiFraudData.getTranLocation());
            logger.info("proxyAuthorization {}", antiFraudData.getHeaderProxyAuthorization());
            logger.info("loginName  {}", antiFraudData.getLoginName());
            logger.info("sessionkey  {}", antiFraudData.getSessionkey());

        }

        transRequest1.getAntiFraudData().setChannel(transRequest1.getChannel());
        System.out.println("received request after antifraud"+transRequest1);

        TransRequest  transRequest2 = persistTransfer(convertEntityToDTO(transRequest1));
        System.out.println("before integration request after antifraud"+transRequest2);

        TransRequest transRequest = null;

        System.out.println("This is the transfer Id : "+ transferRequestDTO.getTransferType());

       if (transferRequestDTO.getTransferType() != TransferType.NEFT && transferRequestDTO.getTransferType() != TransferType.NEFT_BULK) {
           transRequest = integrationService.makeTransfer(transRequest2);
           if (transferRequestDTO.getTransferType() == TransferType.QUICKTELLER){
               transRequest = integrationService.checkQuicktellerTransaction(transRequest);
           }
           logger.info("Transfer Details: {}", transRequest);
       }
            logger.trace("Transfer Details: {}", transRequest);
        
        if (transferRequestDTO.getTransferType() == TransferType.NEFT) {

            logger.info("uniqueid {}",transRequest2);
            NeftResponseDTO response = integrationService.submitInstantNeftTransfer(neftTransfer);
            NeftResponse neftResponse = neftResponseRepo.save(convertResponseToEntity(response));
            neftTransfer.setNeftResponse(neftResponse);
            neftTransferRepo.save(neftTransfer);
            transRequest2.setStatus("000");
            transRequest2.setStatusDescription("Transaction Successful");
            transRequest2.setReferenceNumber(longbridge.utils.NumberUtils.generateReferenceNumber(15));
            logger.info("Transfer reference Number : {} ", transRequest2.getReferenceNumber());
            logger.info("Transfer reference Number : {} ", transRequest2.getUserReferenceNumber());
            transRequest = transferRequestRepo.save(transRequest2);


            return convertEntityToDTO(transRequest);



           /* if (transRequest.getStatus() != null) {
                if (transRequest.getStatus().equalsIgnoreCase("000") || transRequest.getStatus().equalsIgnoreCase("00")||
                        transRequest.getStatus().equalsIgnoreCase("34"))
                return convertEntityToDTO(transRequest);
                throw new InternetBankingTransferException(transRequest.getStatus());
            }
            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());*/
        } else if (transferRequestDTO.getTransferType() == TransferType.NEFT_BULK) {

            logger.info("uniqueid {}", transRequest2);
            transRequest2.setStatus("PENDING");
            transRequest = transferRequestRepo.save(transRequest2);

            return convertEntityToDTO(transRequest);
        }

        else if (transRequest != null){
            logger.info("uniqueid {}",transRequest);
            transRequest = transferRequestRepo.save(transRequest);
            return convertEntityToDTO(transRequest);


        }
        throw new InternetBankingTransferException(messages.getMessage("transfer.failed",null,locale));
    }


    @Override
    public TransRequest getTransfer(Long id) {
        return transferRequestRepo.findById(id).get();
    }

    @Override
    public Iterable<TransRequest> getTransfers(User user) {
        return transferRequestRepo.findByUserReferenceNumber("RET_" + user.getId());

    }

    @Override
    public TransferRequestDTO saveTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {

        try {
            TransRequest transRequest = convertDTOToEntity(transferRequestDTO);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(currentUserName);
                transRequest.setUserReferenceNumber("RET_" + user.getId());
            }
            transferRequestDTO = convertEntityToDTO(transferRequestRepo.save(transRequest));


        } catch (Exception e) {
            logger.error("Exception occurred saving transfer request", e);
        }
        return transferRequestDTO;
    }
    private TransRequest persistTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        TransRequest transRequest = convertDTOToEntity(transferRequestDTO);
        logger.info("trantype {}",transRequest.getTransferType());
        if(transRequest.getTransferType().equals(INTER_BANK_TRANSFER)) {
            String sessId = sessionUtil.generateSessionId();
            transRequest.setReferenceNumber(sessId);
        }
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                RetailUser user = retailUserRepo.findFirstByUserNameIgnoreCase(currentUserName);
                transRequest.setUserReferenceNumber("RET_" + user.getId());
            }


            transRequest = transferRequestRepo.save(transRequest);
        return transRequest;

        } catch (Exception e) {
            logger.error("Exception occurred saving transfer request", e);
        }
        return transRequest;
    }


    private void validateBalance(TransferRequestDTO transferRequest) throws InternetBankingTransferException {


        BigDecimal balance = integrationService.getAvailableBalance(transferRequest.getCustomerAccountNumber());
        if (balance != null) {

            if (!(balance.compareTo(transferRequest.getAmount()) == 0 || (balance.compareTo(transferRequest.getAmount()) > 0))) {
                throw new InternetBankingTransferException(TransferExceptions.BALANCE.toString());
            }
        }

    }

    @Override
    public void validateTransfer(TransferRequestDTO dto) throws InternetBankingTransferException {


        if (dto.getBeneficiaryAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber())) {
            throw new InternetBankingTransferException(TransferExceptions.SAME_ACCOUNT.toString());
        }
        try {
        validateAccounts(dto);
        } catch (InternetBankingTransferException e) {
            logger.error("error validating account {}",e);
            throw new InternetBankingTransferException(e.getMessage());
        }
        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                dto.getTransferType(),
                UserType.RETAIL,
                dto.getCustomerAccountNumber(),
                dto.getAmount()

        );
        if (limitExceeded) throw new InternetBankingTransferException(TransferExceptions.LIMIT_EXCEEDED.toString());

        String cif = accountService.getAccountByAccountNumber(dto.getCustomerAccountNumber()).getCustomerId();
        boolean acctPresent = accountService.getAccountsForDebit(cif).stream()
                .anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber()));


        if (!acctPresent) {
            throw new InternetBankingTransferException(TransferExceptions.NO_DEBIT_ACCOUNT.toString());
        }
        if (validateBalance()) {
            validateBalance(dto);
        }

    }

    @Override
    public Page<TransRequest> getCompletedTransfers(Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
        RetailUser user = getCurrentUser();
//        Page<TransRequest> page = transferRequestRepo.findByUserReferenceNumberAndStatusInAndTranDateNotNullOrderByTranDateDesc("RET_" + user.getId(), Arrays.asList("00","000"), pageDetails);
        Page<TransRequest> page = transferRequestRepo.findByUserReferenceNumberAndTranDateNotNullOrderByTranDateDesc("RET_" + user.getId(), pageDetails);
        logger.info("Completed transfers content" + page.getContent());
        return page;
    }

    @Override
    public Page<TransferRequestDTO> getCompletedTransfer(Pageable pageDetails) {
        logger.debug("Retrieving completed transfers");
        RetailUser user = getCurrentUser();
//        Page<TransRequest> page = transferRequestRepo.findByUserReferenceNumberAndStatusInAndTranDateNotNullOrderByTranDateDesc("RET_" + user.getId(), Arrays.asList("00","000"), pageDetails);
        Page<TransRequest> page = transferRequestRepo.findByUserReferenceNumberAndTranDateNotNullOrderByTranDateDesc("RET_" + user.getId(), pageDetails);
        logger.info("Completed transfers content" + page.getContent());
        List<TransferRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    @Override
    public Page<TransRequest> getCompletedTransfers(String pattern, Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
        RetailUser user = getCurrentUser();

        return transferRequestRepo.findUsingPattern("RET_" + user.getId(),pattern, pageDetails);
    }

    @Override
    public Page<TransferRequestDTO> getCompletedTransfer(String pattern, Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
//        Page<TransRequest> page = transferRequestRepo.findUsingPattern(pattern, pageDetails);
        RetailUser user = getCurrentUser();

        Page<TransRequest> page = transferRequestRepo.findUsingPattern("RET_" + user.getId(),pattern, pageDetails);

        List<TransferRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        logger.trace("Completed transfers", dtOs);
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }
    @Override
    public List<TransRequest> getLastTenTransactionsForAccount(String s) {
        return transferRequestRepo.findTop10ByCustomerAccountNumberOrderByTranDateDesc(s);
    }

    private boolean validateBalance() {
        SettingDTO setting = configService.getSettingByName("ACCOUNT_BALANCE_VALIDATION");
        if (setting != null && setting.isEnabled()) {

            return ("YES".equals(setting.getValue()));
        }
        return true;
    }

    @Override
    public Page<TransRequest> getTransfers(User user, Pageable pageDetails) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public TransferRequestDTO convertEntityToDTO(TransRequest transRequest) {
       // return modelMapper.map(transRequest, TransferRequestDTO.class);
        TransferRequestDTO dto=null;
        if(transRequest.getTransferType().equals(TransferType.INTERNATIONAL_TRANSFER)){
            dto= modelMapper.map(transRequest, InternationalTransferRequestDTO.class);
        }else dto= modelMapper.map(transRequest, TransferRequestDTO.class);
        dto.setStatus(transRequest.getStatus());
        dto.setDeviceNumber(transRequest.getAntiFraudData().getDeviceNumber());
        dto.setCountryCode(transRequest.getAntiFraudData().getCountryCode());
        dto.setHeaderProxyAuthorization(transRequest.getAntiFraudData().getHeaderProxyAuthorization());
        dto.setHeaderUserAgent(transRequest.getAntiFraudData().getHeaderUserAgent());
        dto.setIp(transRequest.getAntiFraudData().getIp());
        dto.setLoginName(transRequest.getAntiFraudData().getLoginName());
        dto.setSessionkey(transRequest.getAntiFraudData().getSessionkey());
        dto.setSfactorAuthIndicator(transRequest.getAntiFraudData().getSfactorAuthIndicator());
        dto.setChannel(transRequest.getChannel());
        dto.setTranLocation(transRequest.getAntiFraudData().getTranLocation());
        dto.setNarration(transRequest.getNarration());
        dto.setRemarks(transRequest.getNarration());
        if (transRequest.getRemarks() !=null){
            dto.setRemarks(transRequest.getRemarks());
        }

        dto.setCustomerAccountNumber(transRequest.getCustomerAccountNumber());
        if(transRequest.getTransferType() == TransferType.QUICKTELLER){
            dto.setLastname(transRequest.getQuickBeneficiary().getLastname());
            dto.setFirstname(transRequest.getQuickBeneficiary().getOthernames());
            if(transRequest.getFinancialInstitution()!=null)
                dto.setBeneficiaryBank(transRequest.getFinancialInstitution().getInstitutionName());
        }
        dto.setBeneficiaryBank(transRequest.getBeneficiaryBank());

        return dto;
    }


    public TransferRequestDTO convertEntityToDTO2(TransRequest transRequest){
        if(transRequest.getTransferType().equals(TransferType.INTERNATIONAL_TRANSFER)){
            return modelMapper.map(transRequest, InternationalTransferRequestDTO.class);
        }
        TransferRequestDTO transferRequestDTO = modelMapper.map(transRequest, TransferRequestDTO.class);
        if(transRequest.getFinancialInstitution()!=null)
            transferRequestDTO.setBeneficiaryBank(transRequest.getFinancialInstitution().getInstitutionName());
        return transferRequestDTO;
    }

    public TransRequest convertDTOToEntity(TransferRequestDTO transferRequestDTO) {
        TransRequest transRequest=null;
        if(transferRequestDTO.getTransferType().equals(TransferType.INTERNATIONAL_TRANSFER)){
             transRequest=modelMapper.map(transferRequestDTO, InternationalTransfer.class);
        }else transRequest= modelMapper.map(transferRequestDTO, TransRequest.class);


        AntiFraudData antiFraudData=new AntiFraudData();
        antiFraudData.setIp(transferRequestDTO.getIp());
        antiFraudData.setCountryCode(transferRequestDTO.getCountryCode());
        antiFraudData.setSfactorAuthIndicator(transferRequestDTO.getSfactorAuthIndicator());
        antiFraudData.setHeaderUserAgent(transferRequestDTO.getHeaderUserAgent());
        antiFraudData.setHeaderProxyAuthorization(transferRequestDTO.getHeaderProxyAuthorization());
        antiFraudData.setLoginName(transferRequestDTO.getLoginName());
        antiFraudData.setDeviceNumber(transferRequestDTO.getDeviceNumber());
        antiFraudData.setSessionkey(transferRequestDTO.getSessionkey());
        antiFraudData.setTranLocation(transferRequestDTO.getTranLocation());
        antiFraudData.setChannel(transferRequestDTO.getChannel());
        antiFraudRepo.save(antiFraudData);
        transRequest.setAntiFraudData(antiFraudData);

        if (transferRequestDTO.getTransferType() == TransferType.QUICKTELLER){
            logger.info("transferType from service layer is {}", transferRequestDTO.getTransferType());
            QuickBeneficiary quickBeneficiary = new QuickBeneficiary();
            quickBeneficiary.setLastname(transferRequestDTO.getLastname());
            quickBeneficiary.setOthernames(transferRequestDTO.getFirstname());
            quickBeneficiaryRepo.save(quickBeneficiary);
            transRequest.setQuickBeneficiary(quickBeneficiary);

            QuickInitiation quickInitiation = new QuickInitiation();
            BigDecimal a = transferRequestDTO.getAmount();
            BigDecimal b = new BigDecimal(100);
            BigDecimal initiationAmount = a.multiply(b);
            quickInitiation.setAmount(initiationAmount);
            quickInitiation.setChannel("7");
            quickInitiation.setCurrencyCode("566");
            quickInitiation.setPaymentMethodCode("CA");
            quickInitiationRepo.save(quickInitiation);
            transRequest.setQuickInitiation(quickInitiation);

            QuickSender quickSender = new QuickSender();
            quickSender.setEmail(getCurrentUser().getEmail());
            quickSender.setLastname(getCurrentUser().getLastName());
            quickSender.setOthernames(getCurrentUser().getFirstName());
            quickSender.setPhone(getCurrentUser().getPhoneNumber());
            quickSenderRepo.save(quickSender);
            transRequest.setQuickSender(quickSender);


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
            transRequest.setQuickTermination(quickTermination);

            Random rand = new Random();
            int upperbound = 9999999;
            int random = rand.nextInt(upperbound);
            transRequest.setTransferCode("1453" + random);
            transRequest.setCustomerAccountNumber(transferRequestDTO.getCustomerAccountNumber());
        }


        return transRequest;

    }

    public List<TransferRequestDTO> convertEntitiesToDTOs(Iterable<TransRequest> transferRequests) {
        List<TransferRequestDTO> transferRequestDTOList = new ArrayList<>();
        for (TransRequest transRequest : transferRequests) {
            TransferRequestDTO transferRequestDTO = convertEntityToDTO2(transRequest);
            transferRequestDTOList.add(transferRequestDTO);
        }
        return transferRequestDTOList;
    }



    private void validateAccounts(TransferRequestDTO dto) throws InternetBankingTransferException {

        accountService.validateAccount(dto.getCustomerAccountNumber());
        String bvn = integrationService.viewCustomerDetails(dto.getCustomerAccountNumber()).getBvn();
        if (bvn == null || bvn.isEmpty() || bvn.equalsIgnoreCase(""))
            throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());

 
        if (!integrationService.viewAccountDetails(dto.getCustomerAccountNumber()).getAcctStatus().equalsIgnoreCase("A"))
            throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());


        if (!NumberUtils.isNumber(dto.getAmount().toString()) || dto.getAmount().compareTo(new BigDecimal(0)) <= 0)
            throw new InternetBankingTransferException(TransferExceptions.INVALID_AMOUNT.toString());


        if (dto.getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER) || dto.getTransferType().equals(TransferType.WITHIN_BANK_TRANSFER)) {
            AccountDetails sourceAccount = integrationService.viewAccountDetails(dto.getCustomerAccountNumber());
            AccountDetails destAccount = integrationService.viewAccountDetails(dto.getBeneficiaryAccountNumber());
            if (sourceAccount == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
            if (destAccount == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());
            if ((("NGN").equalsIgnoreCase(sourceAccount.getAcctCrncyCode())) && !("NGN").equalsIgnoreCase(destAccount.getAcctCrncyCode()))

                throw new InternetBankingTransferException(TransferExceptions.NOT_ALLOWED.toString());

        }
        if (dto.getTransferType().equals(INTER_BANK_TRANSFER)) {
            NEnquiryDetails details = integrationService.doNameEnquiry(dto.getFinancialInstitution().getInstitutionCode(), dto.getBeneficiaryAccountNumber());
          /*  if (details != null && details.getAccountName() == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_BENEFICIARY.toString());*/

            if (integrationService.viewAccountDetails(dto.getCustomerAccountNumber()) == null)
                throw new InternetBankingTransferException(TransferExceptions.INVALID_ACCOUNT.toString());
        }

    }

    private RetailUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (RetailUser) principal.getUser();
    }

    @Override
    public TransRequest updateTransferStatus(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        TransRequest transRequest=null;
        try {

          transRequest=transferRequestRepo.findByReferenceNumberAndStatus(transferRequestDTO.getReferenceNumber(),"34");
          transRequest.setStatus(transferRequestDTO.getStatus());
          transRequest.setStatusDescription(transferRequestDTO.getStatusDescription());

            transRequest = transferRequestRepo.save(transRequest);
            logger.info("updated");

            return transRequest;

        } catch (Exception e) {
            logger.error("Exception occurred updating transfer status", e);
        }
        return transRequest;
    }

    @Override
    public boolean validateDirectDebitTransfer(TransferRequestDTO dto) throws InternetBankingTransferException {

        if (dto.getBeneficiaryAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber())) {
            logger.info("beneficiary account {}",dto.getBeneficiaryAccountNumber());
            logger.info("source account {}",dto.getCustomerAccountNumber());
            logger.info("Source and Beneficiary Accounts are Same");
            return false;
        }

        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                dto.getTransferType(),
                UserType.RETAIL,
                dto.getCustomerAccountNumber(),
                dto.getAmount()

        );
        if (limitExceeded){
            logger.info("Internet Banking limit exceeded");
            return false;
        }

        String cif = accountService.getAccountByAccountNumber(dto.getCustomerAccountNumber()).getCustomerId();
        boolean acctPresent = accountService.getAccountsForDebit(cif).stream()
                .anyMatch(i -> i.getAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber()));


        if (!acctPresent) {
            logger.info("Account is flagged for NO-DEBIT {} ",dto.getCustomerAccountNumber());
            return false;
        }

        BigDecimal balance = integrationService.getAvailableBalance(dto.getCustomerAccountNumber());
        if (balance != null) {
            if (!(balance.compareTo(dto.getAmount()) == 0 || (balance.compareTo(dto.getAmount()) > 0))) {
                logger.info("Account Balance is insufficient for this transfer {}",dto.getCustomerAccountNumber());
                return false;
            }
        }
        return true;
    }

    @Override
    public TransRequest makeBackgroundTransfer(TransferRequestDTO transferRequestDTO, DirectDebit directDebit) {
        logger.info("Initiating a Background Transfer", transferRequestDTO);
        TransRequest transRequest = integrationService.makeBackgroundTransfer(convertDTOToEntity(transferRequestDTO));
        logger.trace("Transfer Details: {} ", transRequest);
        if(directDebit.getCorporate()!=null)
            transRequest.setUserReferenceNumber("CORP_" + directDebit.getCorporateUser().getId());
        else transRequest.setUserReferenceNumber("RET_" + directDebit.getRetailUser().getId());
        transferRequestRepo.save(transRequest);
        return transRequest;
    }

    @Override
    public Page<TransferRequestDTO> getTransferReviews(TransferType transfertype, String accountNumber, Date startDate, Date endDate, Pageable pageDetails){
        Page<TransRequest> page;
        if(transfertype==null){
            page=transferRequestRepo.findByReviewParamsForAllTransTypes(accountNumber, startDate, endDate, pageDetails);
        }else {
            page = transferRequestRepo.findByReviewParams(transfertype, accountNumber, startDate, endDate, pageDetails);
        }
        List<TransferRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        logger.trace("transfers", dtOs);
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    private CorporateUser getCurrentCorpUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (CorporateUser) principal.getUser();
    }

    private TransferRequestDTO convertNeftTransferToTransferRequest(NeftTransferRequestDTO neftTransferRequestDTO){
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryBank(neftTransferRequestDTO.getBeneficiaryBankName());
        transferRequestDTO.setBeneficiaryAccountName(neftTransferRequestDTO.getBeneficiaryAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(neftTransferRequestDTO.getBeneficiaryAccountNumber());
        transferRequestDTO.setBeneficiaryPrefferedName(neftTransferRequestDTO.getBeneficiaryAccountName());
        transferRequestDTO.setCurrencyCode("NGN");
        transferRequestDTO.setAmount(new BigDecimal(neftTransferRequestDTO.getAmount()));
        transferRequestDTO.setNarration(neftTransferRequestDTO.getNarration());
        transferRequestDTO.setCharge(neftTransferRequestDTO.getCharge());
        return transferRequestDTO;
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

}
