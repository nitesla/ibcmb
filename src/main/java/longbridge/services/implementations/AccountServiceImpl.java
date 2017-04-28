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
    private AccountRestrictionRepo accountRestrictionRepo;
    private AccountClassRestrictionRepo accountClassRestrictionRepo;

    private IntegrationService integrationService;
    @Autowired
    private CodeService codeService;

    @Autowired
    private ModelMapper modelMapper;


    public AccountServiceImpl(AccountRepo accountRepo, AccountRestrictionRepo accountRestrictionRepo, AccountClassRestrictionRepo accountClassRestrictionRepo, IntegrationService integrationService) {
        this.accountRepo = accountRepo;
        this.accountRestrictionRepo = accountRestrictionRepo;
        this.accountClassRestrictionRepo = accountClassRestrictionRepo;
        this.integrationService = integrationService;
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
    public Map<String, BigDecimal> getBalance(Account account) {
       return integrationService.getBalance(account.getAccountId());
    }

    @Override
    public AccountStatement getAccountStatements(Account account, Date fromDate, Date toDate) {
        return integrationService.getAccountStatements(account.getAccountId(), fromDate, toDate);
    }

    @Override
    public Iterable<Account> getAccountsForDebit(String customerId) {
        return null;
    }

    @Override
    public Iterable<Account> getAccountsForCredit(String customerId) {
        return null;
    }

    @Override
    public void addAccountRestriction(AccountRestrictionDTO accountRestrictionDTO) throws Exception{
        AccountRestriction accountRestriction = convertAccountRestrictionDTOToEntity(accountRestrictionDTO);
        accountRestriction.setDateCreated(new Date());
        accountRestrictionRepo.save(accountRestriction);
    }

    @Override
    public void updateAccountRestriction(AccountRestrictionDTO accountRestrictionDTO) throws Exception {
        AccountRestriction accountRestriction = convertAccountRestrictionDTOToEntity(accountRestrictionDTO);
        accountRestrictionRepo.save(accountRestriction);
    }

    @Override
    public AccountRestrictionDTO getAccountRestriction(Long id) {
        AccountRestriction accountRestriction = accountRestrictionRepo.findOne(id);
        return convertAccountRestrictionEntityToDTO(accountRestriction);
    }

    @Override
    public AccountClassRestrictionDTO getAccountClassRestriction(Long id) {
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findOne(id);
        return convertAccountClassRestrictionEntityToDTO(accountClassRestriction);
    }

    @Override
    public void removeAccountRestriction(Long id) {
        accountRestrictionRepo.delete(id);

    }

    @Override
    public void addAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws Exception {
        AccountClassRestriction accountClassRestriction = convertAccountClassRestrictionDTOToEntity(accountClassRestrictionDTO);
        accountClassRestriction.setDateCreated(new Date());
        accountClassRestrictionRepo.save(accountClassRestriction);
    }

    @Override
    public void updateAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws Exception {
        AccountClassRestriction accountClassRestriction = convertAccountClassRestrictionDTOToEntity(accountClassRestrictionDTO);
        accountClassRestrictionRepo.save(accountClassRestriction);
    }

    @Override
    public void removeAccountClassRestriction(Long id) {
        accountClassRestrictionRepo.delete(id);
    }

    @Override
    public boolean isAccountRestrictedForDebit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if(accountRestriction!=null){
            if(accountRestriction.getRestrictionType().equals("D")){
                isRestricted =true;
            }
        }
        return  isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForCredit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if(accountRestriction!=null){
            if(accountRestriction.getRestrictionType().equals("C")){
                isRestricted =true;
            }
        }
        return  isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForDebitAndCredit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if(accountRestriction!=null){
            if(accountRestriction.getRestrictionType().equals("CD")){
                isRestricted =true;
            }
        }
        return  isRestricted;       }

    @Override
    public boolean isAccountRestrictedForView(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if(accountRestriction!=null){
            if(accountRestriction.getRestrictionType().equals("V")){
                isRestricted =true;
            }
        }
        return  isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForDebit(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findByAccountClass(accountClass);
        if(accountClassRestriction!=null){
            if(accountClassRestriction.getRestrictionType().equals("D")){
                isRestricted =true;
            }
        }
        return  isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForCredit(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findByAccountClass(accountClass);
        if(accountClassRestriction!=null){
            if(accountClassRestriction.getRestrictionType().equals("C")){
                isRestricted =true;
            }
        }
        return  isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForDebitAndCredit(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findByAccountClass(accountClass);
        if(accountClassRestriction!=null){
            if(accountClassRestriction.getRestrictionType().equals("CD")){
                isRestricted =true;
            }
        }
        return  isRestricted;       }

    @Override
    public boolean isAccountClassRestrictedForView(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findByAccountClass(accountClass);
        if(accountClassRestriction!=null){
            if(accountClassRestriction.getRestrictionType().equals("V")){
                isRestricted =true;
            }
        }
        return  isRestricted;
    }




    @Override
    public Iterable<AccountRestrictionDTO> getRestrictedAccounts() {
        Iterable<AccountRestriction> accountRestrictions = accountRestrictionRepo.findAll();
        return convertAccountRestrictionEntitiesToDTOs(accountRestrictions);
    }

    @Override
    public Iterable<AccountClassRestrictionDTO> getRestrictedAccountClasses() {
        Iterable<AccountClassRestriction> accountClassRestrictions = accountClassRestrictionRepo.findAll();
        return convertAccountClassRestrictionEntitiesToDTOs(accountClassRestrictions);
    }

    @Override
    public Page<AccountRestrictionDTO> getRestrictedAccounts(Pageable pageable) {
        Page<AccountRestriction> accountRestrictionPageable = accountRestrictionRepo.findAll(pageable);
        List<AccountRestrictionDTO> dtos = convertAccountRestrictionEntitiesToDTOs(accountRestrictionPageable.getContent());
        long t = accountRestrictionPageable.getTotalElements();
        return  new PageImpl<AccountRestrictionDTO>(dtos,pageable,t);


    }

    @Override
    public Page<AccountClassRestrictionDTO> getRestrictedAccountClasses(Pageable pageable) {
        Page<AccountClassRestriction> accountClassRestrictionPageable = accountClassRestrictionRepo.findAll(pageable);
        List<AccountClassRestrictionDTO> dtos = convertAccountClassRestrictionEntitiesToDTOs(accountClassRestrictionPageable.getContent());
        long t = accountClassRestrictionPageable.getTotalElements();
        return  new PageImpl<AccountClassRestrictionDTO>(dtos,pageable,t);    }

    @Override
	public Page<Account> getAccounts(String customerId, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

    public AccountDTO convertEntityToDTO(Account account){
        return  this.modelMapper.map(account,AccountDTO.class);
    }


    public Account convertDTOToEntity(AccountDTO accountDTO){
        return this.modelMapper.map(accountDTO,Account.class);
    }

    public List<AccountDTO> convertEntitiesToDTOs(Iterable<Account> accounts){
        List<AccountDTO> accountDTOList = new ArrayList<>();
        for(Account account: accounts){
            AccountDTO accountDTO = convertEntityToDTO(account);
            accountDTOList.add(accountDTO);
        }
        return accountDTOList;
    }

    private AccountRestrictionDTO convertAccountRestrictionEntityToDTO(AccountRestriction accountRestriction){
        AccountRestrictionDTO accountRestrictionDTO = modelMapper.map(accountRestriction,AccountRestrictionDTO.class);
        return accountRestrictionDTO;
    }


    private AccountRestriction convertAccountRestrictionDTOToEntity(AccountRestrictionDTO accountRestrictionDTO){
        return this.modelMapper.map(accountRestrictionDTO,AccountRestriction.class);
    }

    private List<AccountRestrictionDTO> convertAccountRestrictionEntitiesToDTOs(Iterable<AccountRestriction> accountRestrictions){
        List<AccountRestrictionDTO> accountRestrictionDTOList = new ArrayList<>();
        for(AccountRestriction accountRestriction: accountRestrictions){
            AccountRestrictionDTO accountRestrictionDTO = convertAccountRestrictionEntityToDTO(accountRestriction);
            accountRestrictionDTO.setRestrictionType(codeService.getByTypeAndCode("RESTRICTION_TYPE",accountRestriction.getRestrictionType()).getDescription());
            accountRestrictionDTOList.add(accountRestrictionDTO);
        }
        return accountRestrictionDTOList;
    }



    private AccountClassRestrictionDTO convertAccountClassRestrictionEntityToDTO(AccountClassRestriction accountClassRestriction){
        AccountClassRestrictionDTO accountClassRestrictionDTO = modelMapper.map(accountClassRestriction,AccountClassRestrictionDTO.class);
        return accountClassRestrictionDTO;
    }


    private AccountClassRestriction convertAccountClassRestrictionDTOToEntity(AccountClassRestrictionDTO accountClassRestrictionDTO){
        return this.modelMapper.map(accountClassRestrictionDTO,AccountClassRestriction.class);
    }

    private List<AccountClassRestrictionDTO> convertAccountClassRestrictionEntitiesToDTOs(Iterable<AccountClassRestriction> accountClassRestrictions){
        List<AccountClassRestrictionDTO> accountClassRestrictionDTOList = new ArrayList<>();
        for(AccountClassRestriction accountClassRestriction: accountClassRestrictions){
            AccountClassRestrictionDTO accountClassRestrictionDTO = convertAccountClassRestrictionEntityToDTO(accountClassRestriction);
            accountClassRestrictionDTO.setRestrictionType(codeService.getByTypeAndCode("RESTRICTION_TYPE",accountClassRestriction.getRestrictionType()).getDescription());
            accountClassRestrictionDTOList.add(accountClassRestrictionDTO);
        }
        return accountClassRestrictionDTOList;
    }

	@Override
	public Account getAccountByAccountNumber(String accountNumber) {
		return accountRepo.findByAccountNumber(accountNumber);
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
