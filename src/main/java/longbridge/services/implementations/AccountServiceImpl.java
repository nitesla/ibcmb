package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.AccountInfo;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountConfigService;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.SettingsService;
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

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Created by chigozirim on 3/29/17.
 * Modified by Wunmi, Farooq, Fortune
 */

@Service
public class AccountServiceImpl implements AccountService {

    public static final String TRANSACTIONAL_ACCOUNTS = "TRANSACTIONAL_ACCOUNTS";
    public static final String ACCOUNT_FOR_DEBIT = "ACCOUNT_FOR_DEBIT";
    public static final String ACCESS_DENIED_MSG = "Access Denied";
    private final Locale locale = LocaleContextHolder.getLocale();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AccountRepo accountRepo;
    private final CorporateRepo corporateRepo;
    private final IntegrationService integrationService;
    private final ModelMapper modelMapper;
    private final AccountConfigService accountConfigService;
    private final MessageSource messageSource;
    private final SettingsService configurationService;

    @Autowired
    public AccountServiceImpl(AccountRepo accountRepo, CorporateRepo corporateRepo, IntegrationService integrationService, ModelMapper modelMapper, AccountConfigService accountConfigService, MessageSource messageSource, SettingsService configurationService) {
        this.accountRepo = accountRepo;
        this.corporateRepo = corporateRepo;
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
    public boolean AddAccount(String customerId, AccountDTO accountdto) {
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
            }
            accounts.add(acc);
        }
        return accounts;
    }

    @Override
    public String customizeAccount(Long id, String name) {
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
        //TODO fetch account Balance and account type
        return accountRepo.findById(accId).map(this::convertEntityToDTO).orElseThrow(EntityNotFoundException::new);
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

        return accountRepo.findByCustomerId(customerId);
    }

    @Override
    public List<Account> getCustomerAccounts(String customerId, String currencyCode) {

        return accountRepo.findByCustomerIdAndCurrencyCodeIgnoreCase(customerId, currencyCode);

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
        return new PageImpl<>(dtOs, pageDetails, t);
    }


    private AccountDTO convertEntityToDTO(Account account) {
        return this.modelMapper.map(account, AccountDTO.class);
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
    public String hideAccount(Long id) {

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
    public String unhideAccount(Long id) {

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
    public String makePrimaryAccount(Long acctId, String customerId) {

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
        accountRepo.deleteById(id);
    }


    @Override
    public List<AccountInfo> getTransactionalAccounts(List<AccountInfo> accounts) {
        ArrayList<AccountInfo> transactionalAccounts = new ArrayList<>();
        SettingDTO setting = configurationService.getSettingByName(TRANSACTIONAL_ACCOUNTS);
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
    public List<Account> filterUnrestrictedAccounts(List<Account> accounts) {

        return accounts.stream().filter(account -> !accountConfigService.isAccountRestrictedForView(account.getAccountNumber()))
                .filter(account -> !accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeType()))
                .filter(account -> !accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()))
                .filter(account -> !accountConfigService.isAccountRestrictedForViewFromUser(account.getId(), getCurrentUser().getId()))
                .collect(Collectors.toList());
    }


    private List<AccountInfo> getUnrestrictedAccounts(List<AccountInfo> accounts) {

        return accounts.stream().filter(account -> !accountConfigService.isAccountRestrictedForView(account.getAccountNumber()))
                .filter(account -> !accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeType()))
                .filter(account -> !accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountRestricted(AccountDetails account) {

        return accountConfigService.isAccountRestrictedForView(account.getAcctNumber())
                || accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchmCode())
                || accountConfigService.isAccountSchemeTypeRestrictedForView(account.getAcctType());
    }


    @Override
    public List<Account> getAccountsForDebit(String customerId) {
        List<String> schmTypes = new ArrayList<>();
        SettingDTO settingDTO = configurationService.getSettingByName(ACCOUNT_FOR_DEBIT);
        if (settingDTO != null && settingDTO.isEnabled()) {
            List<String> list = Arrays.asList(StringUtils.split(settingDTO.getValue(), ","));
            schmTypes.addAll(list);

        }

        List<Account> accountsForDebit = new ArrayList<>();
        Iterable<Account> accounts = accountRepo.findByCustomerIdAndSchemeTypeIn(customerId, schmTypes);
        for (Account account : accounts) {
            if ("A".equalsIgnoreCase(account.getStatus()) && accountConfigService.isAccountHidden(account.getAccountNumber())
                    && (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) && !accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber()) && (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) && (!accountConfigService.isAccountSchemeCodeRestrictedForDebit(account.getSchemeCode())) && (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) && (!accountConfigService.isAccountSchemeTypeRestrictedForDebit(account.getSchemeType()))))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public Iterable<Account> getAccountsForDebit(List<Account> accounts) {
        List<String> schmTypes = new ArrayList<>();
        SettingDTO settingDTO = configurationService.getSettingByName(ACCOUNT_FOR_DEBIT);
        if (settingDTO != null && settingDTO.isEnabled()) {
            schmTypes = (Arrays.asList(StringUtils.split(settingDTO.getValue(), ",")));
        }
        List<Account> accountsForDebit = new ArrayList<>();
        for (Account account : accounts) {
            account.setStatus(getAccountDetails(account.getAccountNumber()).getStatus());

            if ("A".equalsIgnoreCase(account.getStatus()) && accountConfigService.isAccountHidden(account.getAccountNumber()) &&
                    (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) &&
                    (!accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber())) &&
                    (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) &&
                            (!accountConfigService.isAccountSchemeCodeRestrictedForDebit(account.getSchemeCode()) &&
                                    (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) &&
                                            (!accountConfigService.isAccountSchemeTypeRestrictedForDebit(account.getSchemeType()))) &&
                                    (!accountConfigService.isAccountRestrictedForViewFromUser(account.getId(), getCurrentUser().getId())) &&
                                    (!accountConfigService.isAccountRestrictedForTransactionFromUser(account.getId(), getCurrentUser().getId())) &&
                                    (!schmTypes.isEmpty() && ArrayUtils.contains(schmTypes.toArray(), account.getSchemeType()))))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public Iterable<Account> getAccountsForDebitForStatement(List<Account> accounts) {
        List<String> schmTypes = new ArrayList<>();
        SettingDTO settingDTO = configurationService.getSettingByName(ACCOUNT_FOR_DEBIT);
        if (settingDTO != null && settingDTO.isEnabled()) {
            schmTypes = (Arrays.asList(StringUtils.split(settingDTO.getValue(), ",")));
        }
        List<Account> accountsForDebit = new ArrayList<>();
        for (Account account : accounts) {
            account.setStatus(getAccountDetails(account.getAccountNumber()).getStatus());

            if ("A".equalsIgnoreCase(account.getStatus()) && accountConfigService.isAccountHidden(account.getAccountNumber()) &&
                    (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) &&
                    (!accountConfigService.isAccountRestrictedForDebit(account.getAccountNumber())) &&
                    (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) &&
                            (!accountConfigService.isAccountSchemeCodeRestrictedForDebit(account.getSchemeCode()) &&
                                    (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) &&
                                            (!accountConfigService.isAccountSchemeTypeRestrictedForDebit(account.getSchemeType()))) &&
                                    (!accountConfigService.isAccountRestrictedForViewFromUser(account.getId(), getCurrentUser().getId())) &&
//                    (!accountConfigService.isAccountRestrictedForTransactionFromUser(account.getId(),getCurrentUser().getId())) &&
                                    (!schmTypes.isEmpty() && ArrayUtils.contains(schmTypes.toArray(), account.getSchemeType()))))) {
                accountsForDebit.add(account);
            }

        }
        return accountsForDebit;
    }

    @Override
    public List<AccountDTO> getAccountsForDebitAndCredit(String customerId) {
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<>();
        SettingDTO dto = configurationService.getSettingByName(TRANSACTIONAL_ACCOUNTS);
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(this.getCustomerAccounts(customerId));

        Stream<AccountDTO> accounts = StreamSupport
                .stream(accountDTOS.spliterator(), true);
        if (dto != null && dto.isEnabled()) {
            String[] list = StringUtils.split(dto.getValue(), ",");

            accounts = accounts.filter(
                    i -> org.apache.commons.lang3.ArrayUtils.contains(list, i.getAccountType())
            );
        }
        accounts.filter(i -> accountConfigService.isAccountHidden(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForView(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForDebit(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForCredit(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForView(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForDebit(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForCredit(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForView(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForDebit(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForCredit(i.getSchemeType()))
                .forEach(accountsForDebitAndCredit::add);


        return accountsForDebitAndCredit;
    }

    @Override
    public List<AccountDTO> getAccountsAndBalances(String customerId) {
        return getAccountsAndBalances(this.getCustomerAccounts(customerId));
    }

    @Override
    public List<AccountDTO> getAccountsAndBalances(List<Account> accounts) {
        List<AccountDTO> accountsForDebitAndCredit = new ArrayList<>();
        Iterable<AccountDTO> accountDTOS = convertEntitiesToDTOs(accounts);
        StreamSupport
                .stream(accountDTOS.spliterator(), false)
                .filter(i -> accountConfigService.isAccountHidden(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountRestrictedForView(i.getAccountNumber()))
                .filter(i -> !accountConfigService.isAccountSchemeTypeRestrictedForView(i.getSchemeType()))
                .filter(i -> !accountConfigService.isAccountSchemeCodeRestrictedForView(i.getSchemeCode()))
                .filter(i -> !accountConfigService.isAccountRestrictedForViewFromUser(i.getId(), getCurrentUser().getId()))
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
                    i.setAccountType(i.getSchemeType());
                    accountsForDebitAndCredit.add(i);


                });


        return accountsForDebitAndCredit;
    }


    @Override
    public Iterable<Account> getAccountsForCredit(String customerId) {
        List<Account> accountsForCredit = new ArrayList<>();
        Iterable<Account> accounts = this.getCustomerAccounts(customerId);
        for (Account account : accounts) {
            if (accountConfigService.isAccountHidden(account.getAccountNumber()) && "A".equalsIgnoreCase(account.getStatus())
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
        SettingDTO dto = configurationService.getSettingByName(TRANSACTIONAL_ACCOUNTS);
        if (dto != null && dto.isEnabled()) {
            String[] list = StringUtils.split(dto.getValue(), ",");
            accounts = accounts
                    .stream()
                    .filter(
                            i -> org.apache.commons.lang3.ArrayUtils.contains(list, i.getSchemeType())
                    ).collect(Collectors.toList());

        }
        logger.info("accounts for credit are {}", accounts);
        for (Account account : accounts) {
            if (accountConfigService.isAccountHidden(account.getAccountNumber()) && "A".equalsIgnoreCase(account.getStatus()) &&
                    (!accountConfigService.isAccountRestrictedForViewFromUser(account.getId(), getCurrentUser().getId())) &&
                    (!accountConfigService.isAccountRestrictedForTransactionFromUser(account.getId(), getCurrentUser().getId())) &&
                    (!accountConfigService.isAccountRestrictedForView(account.getAccountNumber())) &&
                    (!accountConfigService.isAccountRestrictedForCredit(account.getAccountNumber())) &&
                    (!accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()) &&
                            (!accountConfigService.isAccountSchemeTypeRestrictedForCredit(account.getSchemeType())) &&
                            (!accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()) &&
                                    (!accountConfigService.isAccountSchemeCodeRestrictedForCredit(account.getSchemeCode()))))) {
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
        switch (currentUser.getUserType()) {
            case RETAIL:
                validateRetail(account, (RetailUser) currentUser);
            break;
            case CORPORATE:
                validateCorporate(account, (CorporateUser) currentUser);
            break;
            default: {
                logger.warn("Internal User {} trying to access accounts", currentUser);
                throw new InternetBankingException(ACCESS_DENIED_MSG);
            }
        }
    }

    private void validateCorporate(String account, CorporateUser currentUser) {
        CorporateUser user = currentUser;
        Account acct = accountRepo.findFirstByAccountNumber(account);
        logger.debug("Account returned: {}", acct);
        if (acct != null) {
            Corporate corporate = corporateRepo.findById(user.getCorporate().getId()).orElseThrow(EntityNotFoundException::new);
            boolean valid = corporate.getAccounts().contains(acct);
            if (!valid) {
                logger.warn("User {} trying to access other accounts", user);
                throw new InternetBankingException(ACCESS_DENIED_MSG);
            }
        }
    }

    private void validateRetail(String account, RetailUser currentUser) {
        RetailUser user = currentUser;
        Account acct = accountRepo.findFirstByAccountNumber(account);
        logger.info("debit account {}", acct);
        if (acct == null || acct.getCustomerId() == null) {
            throw new InternetBankingException(ACCESS_DENIED_MSG);
        } else if (!acct.getCustomerId().equals(user.getCustomerId())) {
            logger.warn("User {} trying to access other accounts", user);
            throw new InternetBankingException(ACCESS_DENIED_MSG);
        }
    }


    private User getCurrentUser() {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }

    @Override
    public void validateAccount(String accountNumber) {
        validate(accountNumber);
    }

    @Override
    public List<AccountDTO> getAccountsForReg(String customerId) {
        List<Account> accountList = accountRepo.findByCustomerId(customerId);
        return convertEntitiesToDTOs(filterUnrestrictedAccountsForReg(accountList));
    }

    @Override
    public List<Account> filterUnrestrictedAccountsForReg(List<Account> accounts) {

        return accounts.stream().filter(account -> !accountConfigService.isAccountRestrictedForView(account.getAccountNumber()))
                .filter(account -> !accountConfigService.isAccountSchemeCodeRestrictedForView(account.getSchemeCode()))
                .filter(account -> !accountConfigService.isAccountSchemeTypeRestrictedForView(account.getSchemeType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccountByCifIdAndSchemeType(String cifId, String schemeType) {
        return accountRepo.findByCustomerIdAndSchemeType(cifId, schemeType);
    }

    @Override
    public Page<Account> getLoanAccounts(List<String> accountNumbers, Pageable pageable) {
        return accountRepo.getLoanAccounts(accountNumbers, pageable);
    }

    @Override
    public List<Account> getLoanAccounts(List<String> accountNumbers) {
        return accountRepo.getLoanAccounts(accountNumbers);
    }

    @Override
    public Page<Account> getFixedDepositAccounts(List<String> accountNumbers, Pageable pageable) {
        return accountRepo.getFixedDepositAccounts(accountNumbers, pageable);
    }

    @Override
    public List<Account> getFixedDepositAccounts(List<String> accountNumbers) {
        return accountRepo.getFixedDepositAccounts(accountNumbers);
    }

}
