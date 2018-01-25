package longbridge.services.implementations;

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
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountService;
import longbridge.services.BulkTransferService;
import longbridge.services.ConfigurationService;
import longbridge.services.CorporateService;
import longbridge.services.bulkTransfers.BulkTransferJobLauncher;
import longbridge.utils.StatusCode;
import longbridge.utils.TransferAuthorizationStatus;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Service
public class BulkTransferServiceImpl implements BulkTransferService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private BulkTransferRepo bulkTransferRepo;

    private MessageSource messageSource;

    private BulkTransferJobLauncher jobLauncher;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private CorporateService corporateService;

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
    public BulkTransferServiceImpl(BulkTransferRepo bulkTransferRepo
            , BulkTransferJobLauncher jobLauncher, MessageSource messageSource

    ) {
        this.bulkTransferRepo = bulkTransferRepo;
        this.jobLauncher = jobLauncher;
        this.messageSource = messageSource;
    }


    @Override
    public String makeBulkTransferRequest(BulkTransfer bulkTransfer) {
        logger.trace("Transfer details valid {}", bulkTransfer);

        accountService.validateAccount(bulkTransfer.getCustomerAccountNumber());
        bulkTransfer.setStatus(StatusCode.PROCESSING.toString());
        bulkTransfer.setStatusDescription("Processing Transaction");
        BulkTransfer transfer = bulkTransferRepo.save(bulkTransfer);
        try {
            jobLauncher.launchBulkTransferJob("" + transfer.getId());
        } catch (Exception e) {
            logger.error("Exception occurred {}", e);
            return messageSource.getMessage("bulk.transfer.failure", null, null);
        }
        return messageSource.getMessage("bulk.transfer.success", null, null);
    }

    @Override
    public CorpTransferAuth getAuthorizations(BulkTransfer transRequest) {
        BulkTransfer bulkTransfer = bulkTransferRepo.findOne(transRequest.getId());
        return bulkTransfer.getTransferAuth();
    }

    @Override
    public String saveBulkTransferRequestForAuthorization(BulkTransfer bulkTransfer) {
        logger.trace("Saving bulk transfer request", bulkTransfer);
        accountService.validateAccount(bulkTransfer.getCustomerAccountNumber());
        if (corporateService.getApplicableTransferRule(bulkTransfer) == null) {
            throw new TransferRuleException(messageSource.getMessage("rule.unapplicable", null, locale));
        }
        try {

            bulkTransfer.setStatus(StatusCode.PENDING.toString());
            bulkTransfer.setStatusDescription("Pending Authorization");
            CorpTransferAuth transferAuth = new CorpTransferAuth();
            transferAuth.setStatus("P");
            bulkTransfer.setTransferAuth(transferAuth);
            BulkTransfer transfer = bulkTransferRepo.save(bulkTransfer);
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
    public Boolean refCodeExists(String refCode) {
        BulkTransfer bulkTransfer = bulkTransferRepo.findFirstByRefCode(refCode);
        return (bulkTransfer != null) ? true : false;
    }

    @Override
    public Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details) {
        //return bulkTransferRepo.findByCorporate(corporate,details);
        return null;
    }


    @Override
    public List<BulkStatusDTO> getStatus(BulkTransfer bulkTransfer) {
        return creditRequestRepo.getCreditRequestSummary(bulkTransfer);
    }

    @Override
    public String addAuthorization(CorpTransReqEntry transReqEntry) {

        CorporateUser corporateUser = getCurrentUser();
        BulkTransfer bulkTransfer = bulkTransferRepo.findOne(transReqEntry.getTranReqId());
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(bulkTransfer);
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

        CorpTransferAuth transferAuth = bulkTransfer.getTransferAuth();

        if (!"P".equals(transferAuth.getStatus())) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.complete", null, locale));
        }

        if (reqEntryRepo.existsByTranReqIdAndRole(bulkTransfer.getId(), userRole)) {
            throw new TransferAuthorizationException(messageSource.getMessage("transfer.auth.exist", null, locale));
        }


        try {
            transReqEntry.setEntryDate(new Date());
            transReqEntry.setRole(userRole);
            transReqEntry.setUser(corporateUser);
            transferAuth.getAuths().add(transReqEntry);
            if (TransferAuthorizationStatus.DECLINED.equals(transReqEntry.getAuthStatus())) {
                transferAuth.setStatus("X"); //cancelled
                bulkTransfer.setStatus(StatusCode.CANCELLED.toString());
                bulkTransfer.setStatusDescription("Cancelled");
                transferAuthRepo.save(transferAuth);
                return messageSource.getMessage("transfer.auth.decline", null, locale);
            }

            transferAuthRepo.save(transferAuth);
            if (isAuthorizationComplete(bulkTransfer)) {
                transferAuth.setStatus("C");
                transferAuth.setLastEntry(new Date());
                transferAuthRepo.save(transferAuth);
                return makeBulkTransferRequest(bulkTransfer);
            }

            return messageSource.getMessage("transfer.auth.success", null, locale);
        } catch (InternetBankingTransferException transferException) {
            throw transferException;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.auth.failure", null, locale), e);
        }
    }


    @Override
    public Page<BulkTransferDTO> getBulkTransferRequests(Corporate corporate, Pageable details) {
        Page<BulkTransfer> page = bulkTransferRepo.findByCorporateOrderByStatusAscTranDateDesc(corporate, details);
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        //Date dt = bulkTransfer.getTranDate();
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
        BulkTransfer one = bulkTransferRepo.getOne(id);
        one.setStatus(StatusCode.CANCELLED.toString());
        bulkTransferRepo.save(one);
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
    public Page<CreditRequest> getAllCreditRequests(BulkTransfer bulkTransfer, Pageable pageable) {
        Page<CreditRequest> page = (Page<CreditRequest>) bulkTransfer.getCrRequestList();
        List<CreditRequest> creditRequests = page.getContent();
        long t = page.getTotalElements();
        Page<CreditRequest> pageImpl = new PageImpl<CreditRequest>(creditRequests, pageable, t);
        return pageImpl;
    }


    public List<CreditRequestDTO> convertEntToDTOs(Iterable<CreditRequest> creditRequests) {
        List<CreditRequestDTO> creditRequestDTOList = new ArrayList<>();
        for (CreditRequest creditRequest : creditRequests) {
            CreditRequestDTO creditRequestDTO = convertEntityToDTO(creditRequest);
            creditRequestDTOList.add(creditRequestDTO);
        }
        return creditRequestDTOList;
    }


    public CreditRequestDTO convertEntityToDTO(CreditRequest creditRequest) {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setNarration(creditRequest.getNarration());
        creditRequestDTO.setAmount(creditRequest.getAmount());
        creditRequestDTO.setAccountName(creditRequest.getAccountName());
        creditRequestDTO.setSortCode(creditRequest.getSortCode());
        creditRequestDTO.setAccountNumber(creditRequest.getAccountNumber());
        creditRequestDTO.setStatus(creditRequest.getStatus());
        return creditRequestDTO;
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
    public boolean userCanAuthorize(TransRequest transRequest) {
        CorporateUser corporateUser = getCurrentUser();
        CorpTransRule transferRule = corporateService.getApplicableTransferRule(transRequest);
        BulkTransfer bulkTransfer = (BulkTransfer)transRequest;

        if (transferRule == null) {
            return false;
        }

        List<CorporateRole> roles = getExistingRoles(transferRule.getRoles());

        for (CorporateRole corporateRole : roles) {
            if (corpRoleRepo.countInRole(corporateRole, corporateUser) > 0 && !reqEntryRepo.existsByTranReqIdAndRole(transRequest.getId(), corporateRole) && "P".equals(bulkTransfer.getTransferAuth().getStatus())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getPendingBulkTransferRequests(Corporate corporate) {
        return bulkTransferRepo.countByCorporateAndStatus(corporate, StatusCode.PENDING.toString());
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

}
