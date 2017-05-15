package longbridge.services.implementations;

import longbridge.api.AccountInfo;
import longbridge.dtos.AccountDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountConfigService;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chigozirim on 3/29/17.
 */

@Service
public class AccountServiceImpl implements AccountService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AccountRepo accountRepo;

    private IntegrationService integrationService;

    private ModelMapper modelMapper;

    private AccountConfigService accountConfigService;

    private MessageSource messageSource;


    public AccountServiceImpl(AccountRepo accountRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountConfigService accountConfigService, MessageSource messageSource) {
        this.accountRepo = accountRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
        this.accountConfigService = accountConfigService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean AddFIAccount(String customerId, AccountInfo acct) {
        if (!customerId.equals(acct.getCustomerId())) {
            return false;
        }
        Account account = new Account();
        account.setPrimaryFlag("N");
        account.setHiddenFlag("N");
        account.setCustomerId(acct.getCustomerId());
        account.setAccountName(acct.getAccountName());
        account.setAccountNumber(acct.getAccountNumber());
        account.setSolId(acct.getSolId());
        account.setSchemeCode(acct.getSchemeCode());
        account.setSchemeType(acct.getSchemeType());
        account.setAccountId(acct.getAccountId());
        accountRepo.save(account);
        return true;
    }

    @Override
    public boolean AddAccount(String customerId, Account account) throws InternetBankingException {
        if (!customerId.equals(account.getCustomerId())) {
            return false;
        }
        accountRepo.save(account);
        return true;
    }

    @Override
    public String customizeAccount(Long id, String name) throws InternetBankingException{

        Account account = accountRepo.findFirstById(id);
        account.setAccountName(name);
        this.accountRepo.save(account);
        return messageSource.getMessage("account.customize.success",null, LocaleContextHolder.getLocale());

    }

    @Override
    public AccountDTO getAccount(Long accId) {
        AccountDTO account =  convertEntityToDTO(accountRepo.findById(accId));
        //TODO fetch account Balance and account type
        return account;
    }

    @Override
    public Account getAccountByCustomerId(String customerId) {
        return accountRepo.findAccountByCustomerId(customerId);
    }

    @Override
    public Iterable<AccountDTO> getAccounts(String customerId) {
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
        List<Account> accounts = new ArrayList<Account>();
        List<Account> accountList = accountRepo.findByCustomerId(customerId);
        for (Account account : accountList) {
            if (account.getCurrencyCode().equals(currencyCode)) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    @Override
    public Map<String, BigDecimal> getBalance(Account account) {
        return integrationService.getBalance(account.getAccountId());
    }

    @Override
    public AccountStatement getAccountStatements(Account account, Date fromDate, Date toDate) {
        return integrationService.getAccountStatements(account.getAccountId(), fromDate, toDate);
    }


    @Override
    public Page<Account> getAccounts(String customerId, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
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
        return accountRepo.findByAccountNumber(accountNumber);
    }

    @Override
    public boolean hideAccount(Long id) throws InternetBankingException{
            Account account = accountRepo.findFirstById(id);
            account.setHiddenFlag("Y");
            accountRepo.save(account);
            return true;

    }

    @Override
    public boolean unhideAccount(Long id) throws InternetBankingException{
            Account account = accountRepo.findFirstById(id);
            account.setHiddenFlag("N");
            accountRepo.save(account);
            return true;
    }

    @Override
    public boolean makePrimaryAccount(Long acctId, String customerId) throws InternetBankingException {
            accountRepo.unsetPrimaryAccount(customerId);
//            for (Account account : accounts){
//                    account.setPrimaryFlag("N");
//                    accountRepo.save(account);
//            }
            Account account = accountRepo.findFirstById(acctId);
            account.setPrimaryFlag("Y");
            accountRepo.save(account);
            return true;

    }


    @Override
    public List<Account> getAccountsForDebit(String customerId, String currencyCode) {
        List<Account> accountsForDebit = new ArrayList<Account>();
        List<Account> accounts = this.getCustomerAccounts(customerId, currencyCode);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber()) && (!accountConfigService.isAccountClassRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountClassRestrictedForDebit(account.getSchemeCode())))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public List<Account> getAccountsForDebit(String customerId) {
        List<Account> accountsForDebit = new ArrayList<Account>();
        Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber()) && (!accountConfigService.isAccountClassRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountClassRestrictedForDebit(account.getSchemeCode())))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public List<AccountDTO> getAccountsForDebitAndCredit(String customerId) {
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<AccountDTO>();
        //Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(this.getCustomerAccounts(customerId));
        for (AccountDTO account : accountDTOS) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebitAndCredit(account.getAccountNumber()) && (!accountConfigService.isAccountClassRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountClassRestrictedForDebitAndCredit(account.getSchemeCode())))) {

                Map<String, BigDecimal> balance = integrationService.getBalance(account.getAccountId());
                String availbalance = balance.get("AvailableBalance").toString();
                String ledBalance = balance.get("LedgerBalance").toString();
                account.setAccountBalance(availbalance);
                account.setLedgerBalance(ledBalance);
                accountsForDebitAndCredit.add(account);
            }

        }
        return accountsForDebitAndCredit;
    }

    @Override
    public Iterable<Account> getAccountsForCredit(String customerId, String currencyCode) {
        List<Account> accountsForCredit = new ArrayList<Account>();
        List<Account> accounts = this.getCustomerAccounts(customerId, currencyCode);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForCredit(account.getAccountNumber()) && (!accountConfigService.isAccountClassRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountClassRestrictedForCredit(account.getSchemeCode())))) {
                accountsForCredit.add(account);
            }

        }
        return accountsForCredit;
    }

    @Override
    public Iterable<Account> getAccountsForCredit(String customerId) {
        List<Account> accountsForCredit = new ArrayList<Account>();
        Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        for (Account account : accounts) {
            if (!accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForCredit(account.getAccountNumber()) && (!accountConfigService.isAccountClassRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountClassRestrictedForCredit(account.getSchemeCode())))) {
                accountsForCredit.add(account);
            }

        }
        return accountsForCredit;
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
//            mockAccount.setAccountName("null");
//        }
//        return mockAccount;
//    }


}
