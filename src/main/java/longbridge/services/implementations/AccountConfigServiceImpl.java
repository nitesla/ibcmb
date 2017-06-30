package longbridge.services.implementations;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.AccountClassRestriction;
import longbridge.models.AccountRestriction;
import longbridge.repositories.AccountClassRestrictionRepo;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.AccountRestrictionRepo;
import longbridge.services.AccountConfigService;
import longbridge.services.CodeService;
import longbridge.utils.DateFormatter;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 5/1/2017.
 */


@Service
public class AccountConfigServiceImpl implements AccountConfigService {


    private AccountRestrictionRepo accountRestrictionRepo;
    private AccountClassRestrictionRepo accountClassRestrictionRepo;
    private CodeService codeService;
    private ModelMapper modelMapper;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public AccountConfigServiceImpl(AccountRestrictionRepo accountRestrictionRepo, AccountClassRestrictionRepo accountClassRestrictionRepo, CodeService codeService, ModelMapper modelMapper) {
        this.accountRestrictionRepo = accountRestrictionRepo;
        this.accountClassRestrictionRepo = accountClassRestrictionRepo;
        this.codeService = codeService;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean isAccountHidden(String accountNumber) {

        Account account = accountRepo.findFirstByAccountNumber(accountNumber);
        if(account!=null) {
            if ("Y".equals(account.getHiddenFlag())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Verifiable(operation="ACC_RES_ADD",description="Adding Account Restriction")
    public String addAccountRestriction(AccountRestrictionDTO accountRestrictionDTO) throws InternetBankingException {

        validateNoAccountDuplication(accountRestrictionDTO);
        try {
            AccountRestriction accountRestriction = convertAccountRestrictionDTOToEntity(accountRestrictionDTO);
            accountRestriction.setDateCreated(new Date());
            accountRestrictionRepo.save(accountRestriction);
            return messageSource.getMessage("account.restrict.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.restrict.add.failure", null, locale), e);
        }
    }


    @Override
    @Verifiable(operation="ACC_RES_UPDATE",description="Update Account Restriction")
    public String updateAccountRestriction(AccountRestrictionDTO accountRestrictionDTO) throws InternetBankingException {

        validateNoAccountDuplication(accountRestrictionDTO);
        try {
            AccountRestriction accountRestriction = accountRestrictionRepo.findOne(accountRestrictionDTO.getId());
            accountRestriction.setVersion(accountRestrictionDTO.getVersion());
            accountRestriction.setAccountNumber(accountRestrictionDTO.getAccountNumber());
            accountRestriction.setRestrictionType(accountRestrictionDTO.getRestrictionType());
            accountRestrictionRepo.save(accountRestriction);
            return messageSource.getMessage("account.restrict.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.restrict.update.failure", null, locale), e);
        }
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
    @Transactional
    @Verifiable(operation="ACC_RES_DEL",description="Delete Account Restriction")
    public String deleteAccountRestriction(Long id) throws InternetBankingException {
        try {
            accountRestrictionRepo.delete(id);
            return messageSource.getMessage("account.restrict.delete.success", null, LocaleContextHolder.getLocale());

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.restrict.delete.failure", null, locale), e);

        }
    }

    @Override
    @Verifiable(operation="ACL_RES_ADD",description="Add Account Class Restriction")
    public String addAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException {

        validateNoAccountClassDuplication(accountClassRestrictionDTO);
        try {
            AccountClassRestriction accountClassRestriction = convertAccountClassRestrictionDTOToEntity(accountClassRestrictionDTO);
            accountClassRestriction.setDateCreated(new Date());
            accountClassRestrictionRepo.save(accountClassRestriction);
            return messageSource.getMessage("class.restrict.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("class.restrict.add.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="ACL_RES_UPDATE",description="Update Account Class Restriction")
    public String updateAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException {

        validateNoAccountClassDuplication(accountClassRestrictionDTO);
        try {
            AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findOne(accountClassRestrictionDTO.getId());
            accountClassRestriction.setVersion(accountClassRestrictionDTO.getVersion());
            accountClassRestriction.setAccountClass(accountClassRestrictionDTO.getAccountClass());
            accountClassRestriction.setRestrictionType(accountClassRestrictionDTO.getRestrictionType());
            accountClassRestrictionRepo.save(accountClassRestriction);
            return messageSource.getMessage("class.restrict.update.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("class.restrict.update.failure", null, locale), e);

        }
    }

    @Override
    @Verifiable(operation="ACC_RES_ADD",description="Delete Account Class Restriction")
    public String deleteAccountClassRestriction(Long id) throws InternetBankingException {
        try {
            AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findOne(id);
            accountClassRestriction.setDeletedOn(new Date());
            accountClassRestrictionRepo.save(accountClassRestriction);
            accountClassRestrictionRepo.delete(id);
            return messageSource.getMessage("class.restrict.delete.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("class.restrict.update.failure", null, locale), e);

        }

    }

    @Override
    public boolean isAccountRestrictedForDebit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if (accountRestriction != null) {
            if (accountRestriction.getRestrictionType().equals("D")) {
                isRestricted = true;
            }
        }
        return isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForCredit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if (accountRestriction != null) {
            if (accountRestriction.getRestrictionType().equals("C")) {
                isRestricted = true;
            }
        }
        return isRestricted;
    }

    @Override
    @Transactional
    public boolean isAccountRestrictedForDebitAndCredit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountNumber);
        if (accountRestriction != null) {
            if (accountRestriction.getRestrictionType().equals("CD")) {
                isRestricted = true;
            }
        }
        return isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForView(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByAccountNumberAndRestrictionTypeIgnoreCase(accountNumber,"V");
        if (accountRestriction != null) {

                isRestricted = true;

        }
        return isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForDebit(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findFirstByAccountClassAndRestrictionTypeIgnoreCase(accountClass,"D");
        if (accountClassRestriction != null) {

                isRestricted = true;

        }
        return isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForCredit(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findFirstByAccountClassAndRestrictionTypeIgnoreCase(accountClass,"C");
        if (accountClassRestriction != null) {

                isRestricted = true;

        }
        return isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForDebitAndCredit(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findFirstByAccountClassAndRestrictionTypeIgnoreCase(accountClass,"CD");
        if (accountClassRestriction != null) {

                isRestricted = true;

        }
        return isRestricted;
    }

    @Override
    public boolean isAccountClassRestrictedForView(String accountClass) {
        boolean isRestricted = false;
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findFirstByAccountClassAndRestrictionTypeIgnoreCase(accountClass,"V");
        if (accountClassRestriction != null) {

                isRestricted = true;

        }
        return isRestricted;
    }

    @Override
    public Iterable<AccountRestrictionDTO> getAccountRestrictions() {
        Iterable<AccountRestriction> accountRestrictions = accountRestrictionRepo.findAll();
        return convertAccountRestrictionEntitiesToDTOs(accountRestrictions);
    }

    @Override
    public Iterable<AccountClassRestrictionDTO> getAccountClassRestrictions() {
        Iterable<AccountClassRestriction> accountClassRestrictions = accountClassRestrictionRepo.findAll();
        return convertAccountClassRestrictionEntitiesToDTOs(accountClassRestrictions);
    }

    @Override
    public Page<AccountRestrictionDTO> getAccountRestrictions(Pageable pageable) {
        Page<AccountRestriction> accountRestrictionPageable = accountRestrictionRepo.findAll(pageable);
        List<AccountRestrictionDTO> dtos = convertAccountRestrictionEntitiesToDTOs(accountRestrictionPageable.getContent());
        long t = accountRestrictionPageable.getTotalElements();
        return new PageImpl<>(dtos, pageable, t);


    }

    @Override
    public Page<AccountClassRestrictionDTO> getAccountClassRestrictions(Pageable pageable) {
        Page<AccountClassRestriction> accountClassRestrictionPageable = accountClassRestrictionRepo.findAll(pageable);
        List<AccountClassRestrictionDTO> dtos = convertAccountClassRestrictionEntitiesToDTOs(accountClassRestrictionPageable.getContent());
        long t = accountClassRestrictionPageable.getTotalElements();
        return new PageImpl<AccountClassRestrictionDTO>(dtos, pageable, t);
    }

    private void validateNoAccountDuplication(AccountRestrictionDTO accountRestrictionDTO) throws DuplicateObjectException {
        AccountRestriction accountRestriction = accountRestrictionRepo.findByAccountNumber(accountRestrictionDTO.getAccountNumber());
        if (accountRestrictionDTO.getId() == null && accountRestriction != null) {
            throw new DuplicateObjectException(String.format(messageSource.getMessage("account.restrict.exists", null, locale), accountRestrictionDTO.getAccountNumber())); //Duplication on creation
        }
        if (accountRestriction != null && (!accountRestriction.getId().equals(accountRestrictionDTO.getId()))) {
            throw new DuplicateObjectException(String.format(messageSource.getMessage("account.restrict.exists", null, locale), accountRestrictionDTO.getAccountNumber())); //Duplication on update

        }
    }

    private void validateNoAccountClassDuplication(AccountClassRestrictionDTO accountClassRestrictionDTO) throws DuplicateObjectException {
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findByAccountClass(accountClassRestrictionDTO.getAccountClass());
        if (accountClassRestrictionDTO.getId() == null && accountClassRestriction != null) {
            throw new DuplicateObjectException(String.format(messageSource.getMessage("class.restrict.exists", null, locale), accountClassRestrictionDTO.getAccountClass())); //Duplication on creation
        }
        if (accountClassRestriction != null && (!accountClassRestriction.getId().equals(accountClassRestrictionDTO.getId()))) {
            throw new DuplicateObjectException(String.format(messageSource.getMessage("class.restrict.exists", null, locale), accountClassRestrictionDTO.getAccountClass())); //Duplication on update

        }
    }


    private AccountRestrictionDTO convertAccountRestrictionEntityToDTO(AccountRestriction accountRestriction) {
        AccountRestrictionDTO accountRestrictionDTO = modelMapper.map(accountRestriction, AccountRestrictionDTO.class);
        accountRestrictionDTO.setDateCreated(DateFormatter.format(accountRestriction.getDateCreated()));

        return accountRestrictionDTO;
    }


    private AccountRestriction convertAccountRestrictionDTOToEntity(AccountRestrictionDTO accountRestrictionDTO) {
        return this.modelMapper.map(accountRestrictionDTO, AccountRestriction.class);
    }

    private List<AccountRestrictionDTO> convertAccountRestrictionEntitiesToDTOs(Iterable<AccountRestriction> accountRestrictions) {
        List<AccountRestrictionDTO> accountRestrictionDTOList = new ArrayList<>();
        for (AccountRestriction accountRestriction : accountRestrictions) {
            AccountRestrictionDTO accountRestrictionDTO = convertAccountRestrictionEntityToDTO(accountRestriction);
            accountRestrictionDTO.setRestrictionType(codeService.getByTypeAndCode("RESTRICTION_TYPE", accountRestriction.getRestrictionType()).getDescription());
            accountRestrictionDTOList.add(accountRestrictionDTO);
        }
        return accountRestrictionDTOList;
    }



    private AccountClassRestrictionDTO convertAccountClassRestrictionEntityToDTO(AccountClassRestriction accountClassRestriction) {
        AccountClassRestrictionDTO accountClassRestrictionDTO = modelMapper.map(accountClassRestriction, AccountClassRestrictionDTO.class);
        accountClassRestrictionDTO.setDateCreated(DateFormatter.format(accountClassRestriction.getDateCreated()));
        return accountClassRestrictionDTO;

    }


    private AccountClassRestriction convertAccountClassRestrictionDTOToEntity(AccountClassRestrictionDTO accountClassRestrictionDTO) {
        return this.modelMapper.map(accountClassRestrictionDTO, AccountClassRestriction.class);
    }

    private List<AccountClassRestrictionDTO> convertAccountClassRestrictionEntitiesToDTOs(Iterable<AccountClassRestriction> accountClassRestrictions) {
        List<AccountClassRestrictionDTO> accountClassRestrictionDTOList = new ArrayList<>();
        for (AccountClassRestriction accountClassRestriction : accountClassRestrictions) {
            AccountClassRestrictionDTO accountClassRestrictionDTO = convertAccountClassRestrictionEntityToDTO(accountClassRestriction);
            accountClassRestrictionDTO.setRestrictionType(codeService.getByTypeAndCode("RESTRICTION_TYPE", accountClassRestriction.getRestrictionType()).getDescription());
            accountClassRestrictionDTOList.add(accountClassRestrictionDTO);
        }
        return accountClassRestrictionDTOList;
    }


}
