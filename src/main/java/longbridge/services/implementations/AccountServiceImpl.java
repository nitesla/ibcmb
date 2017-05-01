package longbridge.services.implementations;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.models.Account;
import longbridge.models.AccountClassRestriction;
import longbridge.models.AccountRestriction;
import longbridge.repositories.AccountClassRestrictionRepo;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.AccountRestrictionRepo;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.IntegrationService;
import longbridge.utils.AccountStatement;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class AccountServiceImpl implements AccountService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AccountRepo accountRepo;

    private IntegrationService integrationService;

    private ModelMapper modelMapper;


    @Autowired
    public AccountServiceImpl(AccountRepo accountRepo, IntegrationService integrationService, ModelMapper modelMapper) {
        this.accountRepo = accountRepo;
        this.integrationService = integrationService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean AddAccount(String customerId, Account account) {
        if (!customerId.equals(account.getCustomerId())) {
            return false;
        }
        accountRepo.save(account);
        return true;
    }

    @Override
    public boolean customizeAccount(Long id, String name) {
        boolean result= false;

        try {
            Account account = accountRepo.findFirstById(id);
            account.setAccountName(name);
            this.accountRepo.save(account);
            logger.trace("Customization successful {}", account.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("Could not customize account",e);
        }
        return result;
    }

    @Override
    public AccountDTO getAccount(Long accId) {
        return convertEntityToDTO(accountRepo.findById(accId));
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

    private AccountDTO convertEntityToDTO(Account account){
        return  this.modelMapper.map(account,AccountDTO.class);
    }


    private Account convertDTOToEntity(AccountDTO accountDTO){
        return this.modelMapper.map(accountDTO,Account.class);
    }

    private List<AccountDTO> convertEntitiesToDTOs(Iterable<Account> accounts){
        List<AccountDTO> accountDTOList = new ArrayList<>();
        for(Account account: accounts){
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
    public boolean hideAccount(Long id) {
        try {
            Account account = accountRepo.findFirstById(id);
            account.setHiddenFlag("Y");
            accountRepo.save(account);
            return true;
        }catch (Exception e){
            logger.info("Error in hiding account");
        }
        return false;
    }

    @Override
    public boolean unhideAccount(Long id) {
        try {
            Account account = accountRepo.findFirstById(id);
            account.setHiddenFlag("N");
            accountRepo.save(account);
            return true;
        }catch (Exception e){
            logger.info("Error unhiding account");
        }
        return false;
    }

    @Override
    public boolean makePrimaryAccount(Long acctId, Iterable<Account> accounts) {
        try {
            for (Account account : accounts){
                    account.setPrimaryFlag("N");
                    accountRepo.save(account);
            }
            Account account = accountRepo.findFirstById(acctId);
            account.setPrimaryFlag("Y");
            accountRepo.save(account);
            return true;
        }catch (Exception e){
            logger.info("Error setting primary account");
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
//            mockAccount.setAccountName("null");
//        }
//        return mockAccount;
//    }


}
