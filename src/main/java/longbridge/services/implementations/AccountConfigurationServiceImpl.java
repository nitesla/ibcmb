package longbridge.services.implementations;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.models.Account;
import longbridge.models.AccountClassRestriction;
import longbridge.models.AccountRestriction;
import longbridge.repositories.AccountClassRestrictionRepo;
import longbridge.repositories.AccountRestrictionRepo;
import longbridge.services.AccountConfigurationService;
import longbridge.services.CodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 5/1/2017.
 */

@Service
public class AccountConfigurationServiceImpl implements AccountConfigurationService {


    private AccountRestrictionRepo accountRestrictionRepo;
    private AccountClassRestrictionRepo accountClassRestrictionRepo;
    private CodeService codeService;
    private ModelMapper modelMapper;

@Autowired
    public AccountConfigurationServiceImpl(AccountRestrictionRepo accountRestrictionRepo, AccountClassRestrictionRepo accountClassRestrictionRepo, CodeService codeService, ModelMapper modelMapper) {
        this.accountRestrictionRepo = accountRestrictionRepo;
        this.accountClassRestrictionRepo = accountClassRestrictionRepo;
        this.codeService = codeService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Iterable<Account> getAccountsForDebit(String customerId, String currency) {
        return null;
    }

    @Override
    public Iterable<Account> getAccountsForCredit(String customerId, String currency) {
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
        AccountRestriction accountRestriction = accountRestrictionRepo.findOne(accountRestrictionDTO.getId());
        accountRestriction.setVersion(accountRestrictionDTO.getVersion());
        accountRestriction.setAccountNumber(accountRestrictionDTO.getAccountNumber());
        accountRestriction.setRestrictionType(accountRestrictionDTO.getRestrictionType());
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
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findOne(accountClassRestrictionDTO.getId());
        accountClassRestriction.setVersion(accountClassRestrictionDTO.getVersion());
        accountClassRestriction.setAccountClass(accountClassRestrictionDTO.getAccountClass());
        accountClassRestriction.setRestrictionType(accountClassRestrictionDTO.getRestrictionType());
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


}
