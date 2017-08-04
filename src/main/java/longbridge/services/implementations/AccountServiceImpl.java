package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountConfigService;
import longbridge.services.AccountService;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.utils.statement.AccountStatement;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.StreamSupport;

//import longbridge.utils.AccountStatement;

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
    public AccountServiceImpl(AccountRepo accountRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountConfigService accountConfigService, MessageSource messageSource, ConfigurationService configurationService) {
        this.accountRepo = accountRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
        this.accountConfigService = accountConfigService;
        this.messageSource = messageSource;
        this.configurationService = configurationService;
    }

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

    @Override
    public boolean AddAccount(String customerId, AccountDTO accountdto) throws InternetBankingException {
        if (!customerId.equals(accountdto.getCustomerId())) {
            return false;
        }
        Account account = getAccountDetails(accountdto.getAccountNumber());
        //account.setAccountId(acct.);TODO
        accountRepo.save(account);
        return true;
    }

    private Account getAccountDetails(String accountNumber) {
        AccountDetails acct = integrationService.viewAccountDetails(accountNumber);
        Account account = new Account();
        account.setPrimaryFlag("N");
        account.setHiddenFlag("N");
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
        return convertEntitiesToDTOs(accountList);
    }

    @Override
    public Iterable<Account> getCustomerAccounts(String customerId) {

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
        return integrationService.getAccountStatements(account.getAccountNumber(), fromDate, toDate, transType);
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
            accountRepo.unsetPrimaryAccount(customerId);
            Account account = accountRepo.findFirstById(acctId);
            account.setPrimaryFlag("Y");
            accountRepo.save(account);
            return messageSource.getMessage("success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("failure", null, locale), e);
        }

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
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber()) && (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeTypeRestrictedForDebit(account.getSchemeCode())))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public List<AccountDTO> getAccountsForDebitAndCredit(String customerId) {
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<>();
        //Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(this.getCustomerAccounts(customerId));
        StreamSupport
                .stream(accountDTOS.spliterator(), false)
                .filter(i -> !accountConfigService.isAccountHidden(i.getAccountNumber()))
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
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<>();
        //Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(this.getCustomerAccounts(customerId));
        StreamSupport
                .stream(accountDTOS.spliterator(), false)
                .filter(i -> !accountConfigService.isAccountHidden(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForView(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForDebit(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForCredit(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForView(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForDebit(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForCredit(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForView(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForDebit(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForCredit(i.getSchemeCode()))

                .forEach(i -> {
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
        logger.info("the customer is " + customerId);
        List<Account> accountsForCredit = new ArrayList<Account>();
        Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        logger.info("accounts are {}", accounts);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForCredit(account.getAccountNumber()) && (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) && (!accountConfigService.isAccountSchemeTypeRestrictedForCredit(account.getSchemeType())) && (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeCodeRestrictedForCredit(account.getSchemeCode()))))) {
                accountsForCredit.add(account);
            }

        }
        return accountsForCredit;
    }

    @Override
    public Boolean updateAccountDetails() {
        //1010007408
        List<Account> allAccounts = accountRepo.findAll();
        for (Account account : allAccounts) {
            logger.info("the account name on our db is {} and account number {}", account.getAccountName(), account.getAccountNumber());
            AccountDetails accountDetails = integrationService.viewAccountDetails(account.getAccountNumber());
            System.out.println("the account name on finacle is" + accountDetails.getAcctName());
            if (account.getCurrencyCode() != null) {
                if (account.getAccountName().equalsIgnoreCase("ADEDOKUN  OLUTOPE") && account.getCurrencyCode().equalsIgnoreCase("NGN")) {
//            account.setPreferredName(accountDetails.getAcctName());
                    account.setAccountName("MARTINS");
                    System.out.println("the account name after setting is" + account.getAccountName());
//            accountRepo.save(account);
                }
//            accountDetails.;

            }
        }
        return false;
    }


//    private Account mockAccount;
//
//    /** Creates an empty {@link longbridge.models.Account} object which will be
//     * returned in place of null
//     * @return {@code Account} object containing "null" as account name
//     */
//    private Account mockAccount(){
//        if(mockAccount==null){
//            mockAccount = new Account();
//            mockAccount.setPreferredName("null");
//        }
//        return mockAccount;
//    }


}
