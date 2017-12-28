package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.repositories.AccountRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountConfigService;
import longbridge.services.AccountService;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.utils.statement.AccountStatement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Created by chigozirim on 3/29/17.
 */

@Service
public class AccountServiceImpl implements AccountService {

    Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AccountRepo accountRepo;
    private IntegrationService integrationService;
    private ModelMapper modelMapper;
    private AccountConfigService accountConfigService;
    private MessageSource messageSource;
    private ConfigurationService configurationService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    public AccountServiceImpl(AccountRepo accountRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountConfigService accountConfigService, MessageSource messageSource, ConfigurationService configurationService) {
        this.accountRepo = accountRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
        this.accountConfigService = accountConfigService;
        this.messageSource = messageSource;
        this.configurationService = configurationService;
    }

    //TODO: Move to where Business rules reside
    @Override
    public boolean AddFIAccount(String customerId, AccountInfo acct) {
        if (!customerId.equals(acct.getCustomerId())) {
            return false;
        }

        Account account = new Account();
        account.setPrimaryFlag("N");
        account.setHiddenFlag("N");
        account.setStatus(acct.getAccountStatus());
        account.setCustomerId(acct.getCustomerId());
        account.setAccountName(acct.getAccountName());
        account.setPreferredName(acct.getAccountName());
        account.setAccountNumber(acct.getAccountNumber());
        account.setCurrencyCode(acct.getAccountCurrency());
        account.setSolId(acct.getSolId());
        account.setSchemeCode(acct.getSchemeCode());
        account.setSchemeType(acct.getSchemeType());
        accountRepo.save(account);
        return true;
    }

    //TODO: Move to where Business rules reside
    @Override
    public boolean AddAccount(String customerId, AccountDTO accountdto) throws InternetBankingException {
        if (!customerId.equals(accountdto.getCustomerId())) {
            return false;
        }
        Account account = getAccountDetails(accountdto.getAccountNumber());
        accountRepo.save(account);
        return true;
    }

    private Account getAccountDetails(String accountNumber) {
        AccountDetails acct = integrationService.viewAccountDetails(accountNumber);
        Account account = new Account();
        account.setPrimaryFlag("N");
        account.setHiddenFlag("N");
        account.setStatus(acct.getAcctStatus());
        account.setCustomerId(acct.getCustId());
        account.setAccountName(acct.getAcctName());
        account.setPreferredName(acct.getAcctName());
        account.setAccountNumber(acct.getAcctNumber());
        account.setCurrencyCode(acct.getAcctCrncyCode());
        account.setSolId(acct.getSolId());
        account.setSchemeCode(acct.getSchmCode());
        account.setSchemeType(acct.getAcctType());
        return account;
    }

    public List<Account> addAccounts(List<AccountDTO> accountDTOs) {

        List<Account> accounts = new ArrayList<>();
        for (AccountDTO accountDTO : accountDTOs) {
            Account account = getAccountDetails(accountDTO.getAccountNumber());
            Account acc = accountRepo.findFirstByAccountNumber(account.getAccountNumber());
            if (acc == null) {
                acc = accountRepo.save(account);
                accounts.add(acc);
            } else {
                accounts.add(acc);
            }
        }
        return accounts;
    }

    @Override
    public String customizeAccount(Long id, String name) throws InternetBankingException {
        try {
            Account account = accountRepo.findFirstById(id);
            validate(account);
            account.setPreferredName(name);
            this.accountRepo.save(account);
            return messageSource.getMessage("account.customize.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.customize.failure", null, locale), e);
        }

    }

    @Override
    public AccountDTO getAccount(Long accId) {
        AccountDTO account = convertEntityToDTO(accountRepo.findById(accId));
        //TODO fetch account Balance and account type
        return account;
    }

    @Override
    public Account getAccountByCustomerId(String customerId) {
        return accountRepo.findFirstAccountByCustomerId(customerId);
    }

    @Override
    public List<AccountDTO> getAccounts(String customerId) {
        List<Account> accountList = accountRepo.findByCustomerId(customerId);
        return convertEntitiesToDTOs(filterUnrestrictedAccounts(accountList));
    }

    @Override
    public List<Account> getCustomerAccounts(String customerId) {

        List<Account> accountList = accountRepo.findByCustomerId(customerId);
        return accountList;
    }

    @Override
    public List<Account> getCustomerAccounts(String customerId, String currencyCode) {

        List<Account> accountList = accountRepo.findByCustomerIdAndCurrencyCodeIgnoreCase(customerId, currencyCode);

        return accountList;

    }

    @Override
    public Map<String, BigDecimal> getBalance(Account account) {
        return integrationService.getBalance(account.getAccountNumber());
    }

    @Override
    public AccountStatement getAccountStatements(Account account, Date fromDate, Date toDate, String transType) {
        return integrationService.getAccountStatements(account.getAccountNumber(), fromDate, toDate, transType, "5");
    }


    @Override
    public Page<AccountDTO> getAccounts(String customerId, Pageable pageDetails) {
        Page<Account> page = accountRepo.findAccountByCustomerId(customerId, pageDetails);
        List<AccountDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<AccountDTO> pageImpl = new PageImpl<AccountDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    private AccountDTO convertEntityToDTO(Account account) {
        return this.modelMapper.map(account, AccountDTO.class);
    }


    private Account convertDTOToEntity(AccountDTO accountDTO) {
        return this.modelMapper.map(accountDTO, Account.class);
    }

    private List<AccountDTO> convertEntitiesToDTOs(Iterable<Account> accounts) {
        List<AccountDTO> accountDTOList = new ArrayList<>();
        for (Account account : accounts) {
            AccountDTO accountDTO = convertEntityToDTO(account);
            accountDTOList.add(accountDTO);
        }
        return accountDTOList;
    }


    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepo.findFirstByAccountNumber(accountNumber);
    }

    @Override
    public String hideAccount(Long id) throws InternetBankingException {

        try {
            Account account = accountRepo.findFirstById(id);
            account.setHiddenFlag("Y");
            accountRepo.save(account);
            return messageSource.getMessage("account.hide.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.hide.failure", null, locale), e);
        }
    }

    @Override
    public String unhideAccount(Long id) throws InternetBankingException {

        try {
            Account account = accountRepo.findFirstById(id);
            account.setHiddenFlag("N");
            accountRepo.save(account);
            return messageSource.getMessage("success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("failure", null, locale), e);
        }
    }

    @Override
    public String makePrimaryAccount(Long acctId, String customerId) throws InternetBankingException {

        try {
            Account account = accountRepo.findFirstById(acctId);
            validate(account);
            accountRepo.unsetPrimaryAccount(customerId);
            account.setPrimaryFlag("Y");
            accountRepo.save(account);
            return messageSource.getMessage("success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("failure", null, locale), e);
        }

    }

    @Override
    public void deleteAccount(Long id) {
            accountRepo.delete(id);
    }


    @Override
    public List<AccountInfo> getTransactionalAccounts(List<AccountInfo> accounts) {
        ArrayList<AccountInfo> transactionalAccounts = new ArrayList<>();
        SettingDTO setting = configurationService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        if (setting != null && setting.isEnabled()) {
            String[] list = StringUtils.split(setting.getValue(), ",");

            for (AccountInfo account : accounts) {
                if (ArrayUtils.contains(list, account.getSchemeType()) && "A".equalsIgnoreCase(account.getAccountStatus())) {
                    transactionalAccounts.add(account);
                }
            }

        }
        return getUnrestrictedAccounts(transactionalAccounts);
    }

    @Override
    public List<Account> filterUnrestrictedAccounts(List<Account> accounts){

        return accounts.stream().filter(account -> !accountConfigService.isAccountRestrictedForView(account.getAccountNumber()))
                                .filter(account -> !accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeType()))
                                .filter(account -> !accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()))
                                .collect(Collectors.toList());
    }


    private List<AccountInfo> getUnrestrictedAccounts(List<AccountInfo> accounts){

        return accounts.stream().filter(account -> !accountConfigService.isAccountRestrictedForView(account.getAccountNumber()))
                .filter(account -> !accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeType()))
                .filter(account -> !accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountRestricted(AccountDetails account){

        return accountConfigService.isAccountRestrictedForView(account.getAcctNumber()) || accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchmCode())
                || accountConfigService.isAccountSchemeTypeRestrictedForView(account.getAcctType());
    }


    @Override
    public List<Account> getAccountsForDebit(String customerId) {
        List<String> schmTypes = new ArrayList<>();
        SettingDTO settingDTO = configurationService.getSettingByName("ACCOUNT_FOR_DEBIT");
        if (settingDTO != null && settingDTO.isEnabled()) {
            List<String> list = Arrays.asList(StringUtils.split(settingDTO.getValue(), ","));
            schmTypes.addAll(list);

        }

        List<Account> accountsForDebit = new ArrayList<Account>();
        Iterable<Account> accounts = accountRepo.findByCustomerIdAndSchemeTypeIn(customerId, schmTypes);
        for (Account account : accounts) {
            if ("A".equalsIgnoreCase(account.getStatus()) && !accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber()) && (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeCodeRestrictedForDebit(account.getSchemeCode()))&& (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) && (!accountConfigService.isAccountSchemeTypeRestrictedForDebit(account.getSchemeType()))))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public Iterable<Account> getAccountsForDebit(List<Account> accounts) {
        List<String> schmTypes = new ArrayList<>();
        SettingDTO settingDTO = configurationService.getSettingByName("ACCOUNT_FOR_DEBIT");
        if (settingDTO != null && settingDTO.isEnabled()) {
            List<String> list = Arrays.asList(StringUtils.split(settingDTO.getValue(), ","));
            schmTypes = (list);

        }


        List<Account> accountsForDebit = new ArrayList<Account>();
        for (Account account : accounts) {
            if ("A".equalsIgnoreCase(account.getStatus()) && !accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber()) && (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeCodeRestrictedForDebit(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) && (!accountConfigService.isAccountSchemeTypeRestrictedForDebit(account.getSchemeType())))
                    && (!schmTypes.isEmpty() && ArrayUtils.contains(schmTypes.toArray(), account.getSchemeType()))))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public List<AccountDTO> getAccountsForDebitAndCredit(String customerId) {
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<>();
        SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        //Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(this.getCustomerAccounts(customerId));

        Stream<AccountDTO> accounts = StreamSupport
                .stream(accountDTOS.spliterator(), true);
        if (dto != null && dto.isEnabled()) {
            String[] list = StringUtils.split(dto.getValue(), ",");

            accounts = accounts.filter(
                    i -> org.apache.commons.lang3.ArrayUtils.contains(list, i.getAccountType())
            );
        }
        accounts.filter(i -> !accountConfigService.isAccountHidden(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForView(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForDebit(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForCredit(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForView(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForDebit(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForCredit(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForView(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForDebit(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForCredit(i.getSchemeType()))
                .forEach(i -> accountsForDebitAndCredit.add(i));


        return accountsForDebitAndCredit;
    }
    @Override
    public List<AccountDTO> getAccountsAndBalances(String customerId) {

        //Iterable<Account> accounts = this.getCustomerAccounts(customerId);

        List<AccountDTO> accountsForDebitAndCredit = getAccountsAndBalances(this.getCustomerAccounts(customerId));
        return accountsForDebitAndCredit;
    }

    @Override
    public List<AccountDTO> getAccountsAndBalances(List<Account> accounts) {
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<>();
        //Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(accounts);
        StreamSupport
                .stream(accountDTOS.spliterator(), false)
                .filter(i -> !accountConfigService.isAccountHidden(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForView(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForView(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForView(i.getSchemeCode()))
                .forEach(i -> {
                		validate(i.getAccountNumber());
                    Map<String, BigDecimal> balance = integrationService.getBalance(i.getAccountNumber());
                    String availbalance = "0";
                    String ledBalance = "0";
                    if (balance != null) {
                        availbalance = balance.get("AvailableBalance").toString();
                        ledBalance = balance.get("LedgerBalance").toString();
                    }

                    i.setAccountBalance(availbalance);
                    i.setLedgerBalance(ledBalance);
                    accountsForDebitAndCredit.add(i);


                });


        return accountsForDebitAndCredit;
    }


    @Override
	public Iterable<Account> getAccountsForCredit(String customerId) {
        List<Account> accountsForCredit = new ArrayList<Account>();
        Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber()) && "A".equalsIgnoreCase(account.getStatus())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) 
                    && !accountConfigService.isAccountRestrictedForCredit(account.getAccountNumber()) 
                    && (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) 
                    		&& (!accountConfigService.isAccountSchemeTypeRestrictedForCredit(account.getSchemeType())) 
                    		&& (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) 
                    				&& (!accountConfigService.isAccountSchemeCodeRestrictedForCredit(account.getSchemeCode()))))) {
                accountsForCredit.add(account);
            }

        }
        return accountsForCredit;
    }

    @Override
    public Iterable<Account> getAccountsForCredit(List<Account> accounts) {
        List<Account> accountsForCredit = new ArrayList<>();
        SettingDTO dto= configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        if (dto!=null && dto.isEnabled()){
            String []list= StringUtils.split(dto.getValue(),",");
        accounts = accounts
                    .stream()
                    .filter(
                            i-> org.apache.commons.lang3.ArrayUtils.contains(list,i.getSchemeType())
                    ).collect(Collectors.toList());

        }
        logger.info("accounts for credit are {}", accounts);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber()) && "A".equalsIgnoreCase(account.getStatus())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForCredit(account.getAccountNumber()) && (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) && (!accountConfigService.isAccountSchemeTypeRestrictedForCredit(account.getSchemeType())) && (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeCodeRestrictedForCredit(account.getSchemeCode()))))) {
                accountsForCredit.add(account);
            }

        }
        return accountsForCredit;
    }

    private void validate(Account account) {
    		validate(account.getAccountNumber());
    }
    
    private void validate(String account) {
    		User currentUser = getCurrentUser();
    		switch(currentUser.getUserType()) {
    		case RETAIL : {	
    			RetailUser user = (RetailUser) currentUser;
    			Account acct = accountRepo.findFirstByAccountNumber(account);
    			if(acct == null || acct.getCustomerId() == null) {
    				throw new InternetBankingException("Access Denied");
    			}else if(!acct.getCustomerId().equals(user.getCustomerId())) {
    				logger.warn("User " + user.toString() + "trying to access other accounts");
    				throw new InternetBankingException("Access Denied");
    			}
    		} break;
    		case CORPORATE : {	
    			CorporateUser user = (CorporateUser) currentUser;
    			Account acct = accountRepo.findFirstByAccountNumber(account);
    			boolean valid = user.getCorporate().getAccounts().contains(acct);
    			if(!valid) {
    				logger.warn("User " + user.toString() + "trying to access other accounts");
    				throw new InternetBankingException("Access Denied");
    			}
    		} break;
    			default: {
    				logger.warn("Internal User " + currentUser.toString() + "trying to access accounts");
    				throw new InternetBankingException("Access Denied");
    			} 
    		}
    }


    private User getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }

	@Override
	public void validateAccount(String accountNumber) throws InternetBankingException {
		validate(accountNumber);
	}
    
}
