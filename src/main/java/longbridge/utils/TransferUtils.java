package longbridge.utils;

import longbridge.api.NEnquiryDetails;
import longbridge.api.Rate;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferExceptions;
import longbridge.models.UserType;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 7/10/2017.
 */
@Service
public class TransferUtils {
    
    private IntegrationService integrationService;
    private AccountService accountService;
    private RetailUserService retailUserService;
    private CorporateService corporateService;
    private CodeService codeService;
    @Autowired
    private LocalBeneficiaryRepo localBeneficiaryRepo;
    @Autowired
    private CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo;
    @Autowired
    private MessageSource messageSource;
    
    private final Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private NeftBeneficiaryRepo neftBeneficiaryRepo;
    @Autowired
    private CorporateRepo corporateRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    public void setCodeService(CodeService codeService) {
        this.codeService = codeService;
    }
    
    @Autowired
    public void setCorporateService(CorporateService corporateService) {
        this.corporateService = corporateService;
    }
    
    @Autowired
    public void setRetailUserService(RetailUserService retailUserService) {
        this.retailUserService = retailUserService;
    }
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @Autowired
    public void setIntegrationService(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }
    
    private String createMessage(String message, boolean successOrFailure) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", message);
            object.put("success", successOrFailure);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
    
    
    public String doIntraBankkNameLookup(String acctNo) {
        if (getCurrentUser() != null && !acctNo.isEmpty()) {
            User user = getCurrentUser();
            if (user.getUserType().equals(longbridge.models.UserType.RETAIL)) {
                LocalBeneficiary localBeneficiary = localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), acctNo);
                if (localBeneficiary != null) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            } else if (user.getUserType().equals(longbridge.models.UserType.CORPORATE)) {
                CorporateUser corporateUser = (CorporateUser) user;
                boolean exists = corpLocalBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(corporateUser.getCorporate().getId(), acctNo);
                if (exists) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            }
            
            String name = integrationService.viewAccountDetails(acctNo).getAcctName();
            if (name != null && !name.isEmpty()) {
                return createMessage(name, true);
            }
            
            return createMessage("Invalid Account", false);
            
        }
        
        return "";
    }
    
    public String doInterBankNameLookup(String bank, String accountNo) {
        
        if (getCurrentUser() != null && !accountNo.isEmpty()) {
            
            User user = getCurrentUser();
            if (user.getUserType().equals(UserType.RETAIL)) {
                LocalBeneficiary localBeneficiary = localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), accountNo);
                if (localBeneficiary != null) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            } else if (user.getUserType().equals(UserType.CORPORATE)) {
                CorporateUser corporateUser = (CorporateUser) user;
                boolean exists = corpLocalBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(corporateUser.getCorporate().getId(), accountNo);
                if (exists) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            }
            
            
            NEnquiryDetails details = integrationService.doNameEnquiry(bank, accountNo);
            if (details == null)
                return createMessage("Service unavailable, please try again later", false);
            
            
            if (details.getResponseCode() != null && !details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getResponseDescription(), false);
            
            
            if (details.getAccountName() != null && details.getResponseCode() != null && details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getAccountName(), true);
            
            
            return createMessage("session_expired", false);
            
        }
        return "";
    }

    public String doNEFTBankNameLookup(String bank, String accountNo) {

        if (getCurrentUser() != null && !accountNo.isEmpty()) {

            User user = getCurrentUser();
            if (user.getUserType().equals(UserType.RETAIL)) {
                NeftBeneficiary neftBeneficiary = neftBeneficiaryRepo.findByUser_IdAndBeneficiaryAccountNumber(user.getId(), accountNo);
                if (neftBeneficiary != null) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            } else if (user.getUserType().equals(UserType.CORPORATE)) {
                CorporateUser corporateUser = (CorporateUser) user;
                boolean exists = corpLocalBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(corporateUser.getCorporate().getId(), accountNo);
                if (exists) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            }


            NEnquiryDetails details = integrationService.doNameEnquiry(bank, accountNo);
            if (details == null)
                return createMessage("Service unavailable, please try again later", false);


            if (details.getResponseCode() != null && !details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getResponseDescription(), false);


            if (details.getAccountName() != null && details.getResponseCode() != null && details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getAccountName(), true);


            return createMessage("session_expired", false);

        }
        return "";
    }

    public String doQuicktellerNameLookup(String bank, String accountNo) {

        if (getCurrentUser() != null && !accountNo.isEmpty()) {

            User user = getCurrentUser();
            if (user.getUserType().equals(UserType.RETAIL)) {
                LocalBeneficiary localBeneficiary = localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), accountNo);
                if (localBeneficiary != null) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            } else if (user.getUserType().equals(UserType.CORPORATE)) {
                CorporateUser corporateUser = (CorporateUser) user;
                boolean exists = corpLocalBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(corporateUser.getCorporate().getId(), accountNo);
                if (exists) {
                    return createMessage("A beneficary with these details already exists", false);
                }
            }


            NEnquiryDetails details = integrationService.doNameEnquiryQuickteller(bank, accountNo);
            if (details == null) {
                return createMessage("Service unavailable, please try again later", false);
            }


            if (details.getAccountName() != null) {
                return createMessage(details.getAccountName(), true);
            }

            return createMessage("session_expired", false);

        }
        return "";
    }
    
    
    public String getBalance(String accountNumber) {
        if (getCurrentUser() != null) {
            validate(accountNumber);
            Account account = accountService.getAccountByAccountNumber(accountNumber);
            Map<String, BigDecimal> balance = accountService.getBalance(account);
            BigDecimal availBal = balance.get("AvailableBalance");
            return createMessage(getCurrency(accountNumber) + "" + availBal.toString(), true);
        }
        return createMessage("", false);
    }
    
    
    public String getLimit(String accountNumber, String channel) {
        validate(accountNumber);
        if (getCurrentUser() != null) {
            String limit = integrationService.getDailyAccountLimit(accountNumber, channel);
            if (limit != null && !limit.isEmpty())
                return createMessage(getCurrency(accountNumber) + "" + limit, true);
        }
        
        return "";
    }

    public String getLimitForAuthorization(String accountNumber, String channel) {
        validate(accountNumber);
        if (getCurrentUser() != null) {
            String limit = integrationService.getDailyAccountLimit(accountNumber, channel);
            if (limit != null && !limit.isEmpty())
                return limit;
        }

        return "";
    }
    
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }
        
        return null;
    }
    
    public List<Account> getNairaAccounts() {
        
        RetailUser user = retailUserService.getUserByName(getCurrentUser().getUserName());
        List<Account> accountList = new ArrayList<>();
        if (user != null) {
            
            
            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());
            
            StreamSupport.stream(accounts.spliterator(), false)
                     .filter(Objects::nonNull)
                     .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                     
                     .forEach(accountList::add);
            
        }
        return accountList;
    }
    
    
    public List<Account> getNairaAccounts(String custId) {
        
        List<Account> accountList = new ArrayList<>();
        if (custId != null && !custId.isEmpty()) {
            
            Iterable<Account> accounts = accountService.getAccountsForDebit(custId);
            
            StreamSupport.stream(accounts.spliterator(), false)
                     .filter(Objects::nonNull)
                     .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                     .forEach(i ->{
                         validate(i);
                         accountList.add(i);});
            
        }
        return accountList;
    }
    
    
    public List<Account> getNairaAccounts(Long corpId) {
        List<Account> accountList = new ArrayList<>();
        if (corpId != null) {
            
            Iterable<Account> accounts = accountService.getAccountsForDebit(corporateService.getAccounts(corpId));
            StreamSupport.stream(accounts.spliterator(), false)
                     .filter(Objects::nonNull)
                     .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                     
                     .forEach(i -> {
                         validate(i);
                         accountList.add(i);});
            
        }
        return accountList;
    }
    
    
    public String getFee(String...channel) {
        String result = "";
        try {
            Rate rate = integrationService.getFee(channel[0],channel[1]);
            if ("FIXED".equalsIgnoreCase(rate.getFeeName())) {
//                result = StringEscapeUtils.unescapeHtml4("&#8358;") + "" + rate.getFeeValue();
                result = "" + rate.getFeeValue();
            } else if ("RANGE".equalsIgnoreCase(rate.getFeeName())) {
                result = rate.getFeeValue();
            } else if ("RATE".equalsIgnoreCase(rate.getFeeName())) {
                result = rate.getFeeValue() + "" + "%";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public String calculateFee(BigDecimal amount, String channel) {
        String result = "";
        try {
            Rate rate = integrationService.getFee(channel);
            if ("FIXED".equalsIgnoreCase(rate.getFeeName())) {
                result = rate.getFeeValue();
            } else if ("RANGE".equalsIgnoreCase(rate.getFeeName())) {
                result = new BigDecimal(rate.getFeeValue()).divide(new BigDecimal("100").multiply(amount)).toPlainString();
            } else if ("RATE".equalsIgnoreCase(rate.getFeeName())) {
                result = rate.getFeeValue();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    public void validateTransferCriteria() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            
            if(currentUser == null){
                throw new InternetBankingTransferException(messageSource.getMessage("user.invalid",null,locale));
            }
            if (currentUser.getCorpId() == null) {
                
                RetailUser retailUser = retailUserService.getUserByName(currentUser.getUsername());
                
                if (retailUser.getBvn() == null || "NO BVN".equalsIgnoreCase(retailUser.getBvn()) || retailUser.getBvn().isEmpty()) {
                    
                    throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());
                }
                
            } else {
                CorporateUser user  = (CorporateUser)currentUser.getUser();
                if(CorpUserType.AUTHORIZER.equals(user.getCorpUserType())){
                    throw new InternetBankingTransferException(messageSource.getMessage("transfer.initiate.disallowed",null,locale));
                    
                }
                
                Corporate corporate = corporateService.getCorp(currentUser.getCorpId());
                if (corporate == null) throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());
                
                if ((corporate.getRcNumber() == null || corporate.getRcNumber().isEmpty() ) && (corporate.getTaxId()==null ||corporate.getTaxId().isEmpty() )){
                    throw new InternetBankingTransferException(TransferExceptions.NO_RC.toString());
                }
                
            }
            
            
        }
        
    }
    
    
    public String getCurrency(String accountNumber) {
        String currency = "";
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account != null) {
            Code code = codeService.getByTypeAndCode("CURRENCY", account.getCurrencyCode());
            if (code != null && null != code.getExtraInfo()) {
                
                currency = StringEscapeUtils.unescapeHtml4(code.getExtraInfo());
                return currency;
            }
            currency = account.getCurrencyCode();
            
        }
        return currency;
        
    }
    private void validate(Account account) {
        validate(account.getAccountNumber());
    }
    
    private void validate(String account) {
        User currentUser = getCurrentUser();
        switch (currentUser.getUserType()) {
            case RETAIL: {
                RetailUser user = (RetailUser) currentUser;
                Account acct = accountRepo.findFirstByAccountNumber(account);
                if (acct == null || acct.getCustomerId() == null) {
                    throw new InternetBankingException("Access Denied");
                } else if (!acct.getCustomerId().equals(user.getCustomerId())) {
                    logger.warn("User " + user.toString() + "trying to access other accounts");
                    throw new InternetBankingException("Access Denied");
                }
            }
            break;
            case CORPORATE: {
                CorporateUser user = (CorporateUser) currentUser;
                Account acct = accountRepo.findFirstByAccountNumber(account);
                Corporate corporate = corporateRepo.findById(user.getCorporate().getId()).get();
                boolean valid = corporate.getAccounts().contains(acct);			if (!valid) {
                    logger.warn("User " + user.toString() + "trying to access other accounts");
                    throw new InternetBankingException("Access Denied");
                }
            }
            break;
            default: {
                logger.warn("Internal User " + currentUser.toString() + "trying to access accounts");
                throw new InternetBankingException("Access Denied");
            }
        }
    }



    public String generateReferenceNumber(int numOfDigits) {
        if(numOfDigits<1) {
            throw new IllegalArgumentException(numOfDigits + ": Number must be equal or greater than 1");
        }
        long random = (long) Math.floor(Math.random() * 9 * (long)Math.pow(10,numOfDigits-1)) + (long)Math.pow(10,numOfDigits-1);
        return Long.toString(random);
    }
    
}
