package longbridge.services.implementations;

import longbridge.api.CustomerDetails;
import longbridge.dtos.BulkStatusDTO;
import longbridge.dtos.BulkTransferDTO;
import longbridge.dtos.CreditRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferAuthorizationException;
import longbridge.exception.TransferRuleException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.IpAddressUtils;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.services.bulkTransfers.BulkTransferJobLauncher;
import longbridge.services.bulkTransfers.TransferDTO;
import longbridge.utils.StatusCode;
import longbridge.utils.TransferAuthorizationStatus;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Service
public class BulkRetailTransferServiceImpl implements BulkRetailTransferService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private BulkTransferRepo bulkTransferRepo;

    private MessageSource messageSource;

    private BulkTransferJobLauncher jobLauncher;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private CorporateService corporateService;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private CreditRequestRepo creditRequestRepo;


    @Autowired
    private ConfigurationService configService;

    @Autowired
    private CorporateRoleRepo corpRoleRepo;

    @Autowired
    private CorpTransReqEntryRepo reqEntryRepo;

    @Autowired
    private CorpTransferAuthRepo transferAuthRepo;

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountConfigService accountConfigService;
    @Autowired
    private IntegrationService integrationService;

    private RestTemplate template;

    @Autowired
    IpAddressUtils ipAddressUtils;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Value("${naps.url}")
    private String url;

    private NapsAntiFraudRepo napsAntiFraudRepo;


    @Autowired
    public BulkRetailTransferServiceImpl(BulkTransferRepo bulkTransferRepo, BulkTransferJobLauncher jobLauncher, MessageSource messageSource,RestTemplate template,NapsAntiFraudRepo napsAntiFraudRepo

    ) {
        this.bulkTransferRepo = bulkTransferRepo;
        this.jobLauncher = jobLauncher;
        this.messageSource = messageSource;
        this.template=template;
        this.napsAntiFraudRepo=napsAntiFraudRepo;
    }


    @Override
    public String  makeBulkTransferRequest(BulkTransfer bulkTransfer) {

        logger.debug("Retail Transfer details valid {}", bulkTransfer);

        accountService.validateAccount(bulkTransfer.getCustomerAccountNumber());
        bulkTransfer.setUserReferenceNumber("RET_"+getCurrentUser().getId().toString());
        bulkTransfer.setStatus(StatusCode.PROCESSING.toString());
        bulkTransfer.setStatusDescription("Processing Transaction");
        BulkTransfer transfer = bulkTransferRepo.save(bulkTransfer);

        CustomUserPrincipal user = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        String sessionkey = ((WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails())
                .getSessionId();

        NapsAntiFraudData antiFraudData = new NapsAntiFraudData();
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
        antiFraudData.setTranLocation(bulkTransfer.getTranLocation());


        logger.info("Antifraud details {}",antiFraudData);
        NapsAntiFraudData napsAntiFraudData2=napsAntiFraudRepo.save(antiFraudData);
        logger.info("napsData {}",napsAntiFraudData2);


        List<CreditRequest> creditRequests = bulkTransfer.getCrRequestList();
        creditRequests.forEach(request -> {
            request.setStatus("PROCESSING");
            request.setApprovalDate(new Date());
            request.setNapsAntiFraudData(napsAntiFraudData2);
            creditRequestRepo.save(request);
        });


        try {
            jobLauncher.launchBulkTransferJob("" + transfer.getRefCode());
        } catch (Exception e) {
            logger.error("Exception occurred {}", e);
            return messageSource.getMessage("bulk.transfer.failure", null, null);
        }
        return messageSource.getMessage("bulk.transfer.success", null, null);
    }



    @Override
    public Boolean refCodeExists(String refCode) {
        BulkTransfer bulkTransfer = bulkTransferRepo.findFirstByRefCode(refCode);
        return (bulkTransfer != null) ? true : false;
    }

    @Override
    public boolean creditRequestRefNumberExists(String refNumber) {
        return creditRequestRepo.existsByReferenceNumber(refNumber);
    }

    @Override
    public List<BulkStatusDTO> getStatus(BulkTransfer bulkTransfer) {
        return creditRequestRepo.getCreditRequestSummary(bulkTransfer);
    }


    @Override
    public Page<BulkTransferDTO> getBulkTransferRequests(Pageable details) {
        RetailUser retailUser = getCurrentUser();

        Page<BulkTransfer> page = bulkTransferRepo.findByRetailUserOrderByTranDateDesc(retailUser, details);

        List<BulkTransferDTO> dtOs = convertEntitiesToDTOs(page.getContent());

        long t = page.getTotalElements();
        Page<BulkTransferDTO> pageImpl = new PageImpl<BulkTransferDTO>(dtOs, details, t);
        return pageImpl;
    }

    public BulkTransferDTO convertEntityToDTO(BulkTransfer bulkTransfer) {
        BulkTransferDTO bulkTransferDTO = new BulkTransferDTO();
        bulkTransferDTO.setId(bulkTransfer.getId());
        bulkTransferDTO.setCustomerAccountNumber(bulkTransfer.getCustomerAccountNumber());
        bulkTransferDTO.setRefCode(bulkTransfer.getRefCode());
        bulkTransferDTO.setTranDate(bulkTransfer.getTranDate());
        bulkTransferDTO.setStatus(bulkTransfer.getStatus());
        bulkTransferDTO.setStatusDescription(bulkTransfer.getStatusDescription());
        return bulkTransferDTO;
    }

    public List<BulkTransferDTO> convertEntitiesToDTOs(Iterable<BulkTransfer> bulkTransfers) {
        List<BulkTransferDTO> bulkTransferDTOList = new ArrayList<>();
        for (BulkTransfer bulkTransfer : bulkTransfers) {
            BulkTransferDTO bulkTransferDTO = convertEntityToDTO(bulkTransfer);
            bulkTransferDTOList.add(bulkTransferDTO);
        }
        return bulkTransferDTOList;
    }


    @Override
    public String cancelBulkTransferRequest(Long id) {
        //cancelling bulk transaction request
        BulkTransfer bulkTransfer = bulkTransferRepo.getOne(id);
        bulkTransfer.setStatus(StatusCode.CANCELLED.toString());
        bulkTransferRepo.save(bulkTransfer);
        List<CreditRequest> creditRequests = bulkTransfer.getCrRequestList();
        creditRequests.forEach(i -> {
            i.setStatus("CANCELLED");
            creditRequestRepo.save(i);
        });
        return null;
    }

    @Override
    public BulkTransfer getBulkTransferRequest(Long id) {
        return bulkTransferRepo.getOne(id);
    }

    @Override
    public Page<CreditRequestDTO> getCreditRequests(BulkTransfer bulkTransfer, Pageable pageable) {
        List<CreditRequest> creditRequests = bulkTransfer.getCrRequestList();
        Page<CreditRequest> page = new PageImpl<CreditRequest>(creditRequests);
        List<CreditRequestDTO> dtOs = convertEntToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CreditRequestDTO> pageImpl = new PageImpl<CreditRequestDTO>(dtOs, pageable, t);
        return pageImpl;
    }

    @Override
    public List<CreditRequestDTO> getCreditRequests(Long bulkTransferId) {

        BulkTransfer bulkTransfer = bulkTransferRepo.findById(bulkTransferId).get();
        List<CreditRequest> creditRequests = bulkTransfer.getCrRequestList();
        List<CreditRequestDTO> dtOs = convertEntToDTOs(creditRequests);
        return dtOs;

    }

    @Override
    public Page<CreditRequest> getAllCreditRequests(BulkTransfer bulkTransfer, Pageable pageable) {
        Page<CreditRequest> page = (Page<CreditRequest>) bulkTransfer.getCrRequestList();
        List<CreditRequest> creditRequests = page.getContent();
        long t = page.getTotalElements();
        Page<CreditRequest> pageImpl = new PageImpl<CreditRequest>(creditRequests, pageable, t);
        return pageImpl;
    }

    @Override
    public CreditRequestDTO getCreditRequest(Long id) {

        CreditRequest creditRequest = creditRequestRepo.findById(id).get();
        CreditRequestDTO creditRequestDTO = convertEntityToDTO(creditRequest);
        return creditRequestDTO;
    }


    public List<CreditRequestDTO> convertEntToDTOs(Iterable<CreditRequest> creditRequests) {
        List<CreditRequestDTO> creditRequestDTOList = new ArrayList<>();
        for (CreditRequest creditRequest : creditRequests) {
            CreditRequestDTO creditRequestDTO = convertEntityToDTO(creditRequest);
            FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByBankCode(creditRequest.getSortCode());
            if (financialInstitution != null) {
                creditRequestDTO.setBeneficiaryBank(financialInstitution.getInstitutionName());
            }
            creditRequestDTOList.add(creditRequestDTO);
        }
        return creditRequestDTOList;
    }


    public CreditRequestDTO convertEntityToDTO(CreditRequest creditRequest) {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setId(creditRequest.getId());
        creditRequestDTO.setNarration(creditRequest.getNarration());
        creditRequestDTO.setAmount(creditRequest.getAmount());
        creditRequestDTO.setAccountName(creditRequest.getAccountName());
        creditRequestDTO.setSortCode(creditRequest.getSortCode());
        creditRequestDTO.setAccountNumber(creditRequest.getAccountNumber());
        creditRequestDTO.setStatus(creditRequest.getStatus());
        creditRequestDTO.setCustomerAccountNumber(creditRequest.getBulkTransfer().getCustomerAccountNumber());
        creditRequestDTO.setReferenceNumber(creditRequest.getReferenceNumber());
        creditRequestDTO.setTranDate(creditRequest.getBulkTransfer().getTranDate());
        creditRequestDTO.setAccountNameEnquiry(creditRequest.getAccountNameEnquiry());
        FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByBankCode(creditRequest.getSortCode());
        if (financialInstitution != null) {
            creditRequestDTO.setBeneficiaryBank(financialInstitution.getInstitutionName());
        }
        return creditRequestDTO;
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

    /*@Override
    public boolean userCanAuthorize(TransRequest transRequest) {
        RetailUser RetailUser = getCurrentUser();
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(transRequest);
        BulkTransfer bulkTransfer = (BulkTransfer) transRequest;

        if (transferRule == null) {
            return false;
        }

        List<CorporateRole> roles = getExistingRoles(transferRule.getRoles());

        for (CorporateRole corporateRole : roles) {
            if (corpRoleRepo.countInRole(corporateRole, RetailUser) > 0 && !reqEntryRepo.existsByTranReqIdAndRole(transRequest.getId(), corporateRole) && "P".equals(bulkTransfer.getTransferAuth().getStatus())) {
                return true;
            }
        }
        return false;
    }
*/
    @Override
    public int getPendingBulkTransferRequests(Corporate corporate) {
        return bulkTransferRepo.countByCorporateAndStatus(corporate, StatusCode.PENDING.toString());
    }

    @Override
    public boolean transactionAboveLimit(BigDecimal totalCreditAmount, String debitAccount) {

        String amount = integrationService.getDailyAccountLimit(debitAccount, "NAPS");

        logger.info("the daily limit {}",amount);

        if(null==amount || amount.isEmpty()){
            throw new InternetBankingException(messageSource.getMessage("transfer.limit.validate.failed", null, locale));
        }
        logger.debug("the debitAccount {} totalcredit amount {}",debitAccount, totalCreditAmount);
        BigDecimal dailyLimit = new BigDecimal(amount);
        if(totalCreditAmount.compareTo(dailyLimit)>0){
            throw new InternetBankingException(messageSource.getMessage("transfer.daily.limit", null, locale));
        }
        return false;
    }

    private boolean isAuthorizationComplete(BulkTransfer transRequest) {
        CorpTransferAuth transferAuth = transRequest.getTransferAuth();
        Set<CorpTransReqEntry> transReqEntries = transferAuth.getAuths();
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(transRequest);
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

    public List<BulkTransfer> getBulkTransferRequestsForCorporate(Corporate corporate){
        List<BulkTransfer> bulkTransfers=bulkTransferRepo.findByCorporate(corporate);
        return bulkTransfers;
    }
    public List<BulkTransfer>getByStatus(){
        List<BulkTransfer> bulkTransfers=bulkTransferRepo.findByStatus("Processing");
        return bulkTransfers;
    }

    private RetailUser getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RetailUser retailUser = (RetailUser) principal.getUser();
        return retailUser;
    }

}
