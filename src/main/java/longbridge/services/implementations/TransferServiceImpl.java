package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.InternationalTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferExceptions;
import longbridge.models.*;
import longbridge.repositories.NeftTransferRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.TransferRequestRepo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

import static longbridge.utils.TransferType.INTER_BANK_TRANSFER;

/**
 * Created by Fortune on 3/30/2017.
 */

@Service
public class TransferServiceImpl implements TransferService {
    private RetailUserRepo retailUserRepo;
    private TransferRequestRepo transferRequestRepo;
    private IntegrationService integrationService;
    private TransactionLimitServiceImpl limitService;
    private ModelMapper modelMapper;
    private AccountService accountService;
    private FinancialInstitutionService financialInstitutionService;
    private ConfigurationService configService;
    private MessageSource messages;
    private Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SessionUtil sessionUtil;


    @Autowired
    IpAddressUtils ipAddressUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    private NeftTransferRepo neftTransferRepo;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    public TransferServiceImpl(TransferRequestRepo transferRequestRepo, IntegrationService integrationService, TransactionLimitServiceImpl limitService, ModelMapper modelMapper, AccountService accountService, FinancialInstitutionService financialInstitutionService, ConfigurationService configurationService
            , RetailUserRepo retailUserRepo, MessageSource messages, SessionUtil sessionUtil) {
        this.transferRequestRepo = transferRequestRepo;
        this.integrationService = integrationService;
        this.limitService = limitService;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.financialInstitutionService = financialInstitutionService;
        this.configService = configurationService;
        this.retailUserRepo = retailUserRepo;
        this.messages = messages;
        this.sessionUtil=sessionUtil;

    }


    private NeftTransfer pfDataItemStore(TransferRequestDTO neftTransferDTO){
        NeftTransfer neftTransfer = new NeftTransfer();
        neftTransfer.setAccountNo(neftTransferDTO.getCustomerAccountNumber());
        neftTransfer.setBeneficiaryAccountNo(neftTransferDTO.getBeneficiaryAccountNumber());
        neftTransfer.setBeneficiary(neftTransferDTO.getBeneficiaryAccountName());
        neftTransfer.setAmount(neftTransferDTO.getAmount());
        neftTransfer.setCurrency(neftTransferDTO.getCurrencyCode());
        neftTransfer.setNarration(neftTransferDTO.getNarration());
        neftTransfer.setSpecialClearing(true);
        neftTransfer.setBVNBeneficiary("");
        neftTransfer.setBankOfFirstDepositSortCode("");
        neftTransfer.setCollectionType("");
        neftTransfer.setBVNPayer("");
        neftTransfer.setInstrumentType("");
        neftTransfer.setMICRRepairInd("");
        neftTransfer.setSettlementTime("not settled");
        neftTransfer.setCycleNo("");
        neftTransfer.setNarration(neftTransferDTO.getRemarks());
        neftTransfer.setPresentingBankSortCode("");
        neftTransfer.setSortCode("");
        neftTransfer.setTranCode("");
        neftTransferRepo.save(neftTransfer);
        return neftTransfer;
    }





    @Override
    public TransferRequestDTO makeTransfer(TransferRequestDTO transferRequestDTO) throws InternetBankingTransferException {
        validateTransfer(transferRequestDTO);
        if (transferRequestDTO.getTransferType() == TransferType.NEFT){
            logger.info("transferType from service layer is {}", transferRequestDTO.getTransferType());
             pfDataItemStore(transferRequestDTO);
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

       if (transferRequestDTO.getTransferType() != TransferType.NEFT) {
           transRequest = integrationService.makeTransfer(transRequest2);
       }
            logger.trace("Transfer Details: ", transRequest);
        
        if (transRequest == null) {

            logger.info("uniqueid {}",transRequest);
            transRequest2.setStatus("00");
            transRequest = transferRequestRepo.save(transRequest2);

            return convertEntityToDTO(transRequest);


//            if (transRequest.getStatus() != null) {
//                if (transRequest.getStatus().equalsIgnoreCase("000") || transRequest.getStatus().equalsIgnoreCase("00")||
//                        transRequest.getStatus().equalsIgnoreCase("34"))
//                return convertEntityToDTO(transRequest);
//                throw new InternetBankingTransferException(transRequest.getStatus());
           }


//            throw new InternetBankingTransferException(TransferExceptions.ERROR.toString());
//        }
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
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
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
        Page<TransferRequestDTO> pageImpl = new PageImpl<TransferRequestDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Page<TransRequest> getCompletedTransfers(String pattern, Pageable pageDetails) {
        logger.info("Retrieving completed transfers");
        RetailUser user = getCurrentUser();

        Page<TransRequest> page = transferRequestRepo.findUsingPattern("RET_" + user.getId(),pattern, pageDetails);

        return page;
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
        Page<TransferRequestDTO> pageImpl = new PageImpl<TransferRequestDTO>(dtOs, pageDetails, t);
        return pageImpl;
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
        if(transRequest.getFinancialInstitution()!=null)
            dto.setBeneficiaryBank(transRequest.getFinancialInstitution().getInstitutionName());
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
        transRequest.setAntiFraudData(antiFraudData);
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
        RetailUser retailUser = (RetailUser) principal.getUser();
        return retailUser;
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
        boolean acctPresent = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
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
        Page<TransferRequestDTO> pageImpl = new PageImpl<>(dtOs, pageDetails, t);
        return pageImpl;
    }

}
