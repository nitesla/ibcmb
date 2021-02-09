package longbridge.services.implementations;

import longbridge.dtos.AccountClassRestrictionDTO;
import longbridge.dtos.AccountRestrictionDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Account;
import longbridge.models.AccountClassRestriction;
import longbridge.models.AccountRestriction;
import longbridge.models.UserAccountRestriction;
import longbridge.repositories.AccountClassRestrictionRepo;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.AccountRestrictionRepo;
import longbridge.repositories.UserAccountRestrictionRepo;
import longbridge.services.AccountConfigService;
import longbridge.services.CodeService;
import longbridge.utils.DateFormatter;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Fortune on 5/1/2017.
 */


@Service
public class AccountConfigServiceImpl implements AccountConfigService {


    private final AccountRestrictionRepo accountRestrictionRepo;
    private final AccountClassRestrictionRepo accountClassRestrictionRepo;
    private final CodeService codeService;
    private final ModelMapper modelMapper;
    private final AccountRepo accountRepo;
    private final MessageSource messageSource;
    private final EntityManager entityManager;
    private final UserAccountRestrictionRepo userAccountRestrictionRepo;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public AccountConfigServiceImpl(AccountRestrictionRepo accountRestrictionRepo, AccountClassRestrictionRepo accountClassRestrictionRepo, CodeService codeService, ModelMapper modelMapper, AccountRepo accountRepo, MessageSource messageSource, EntityManager entityManager, UserAccountRestrictionRepo userAccountRestrictionRepo) {
        this.accountRestrictionRepo = accountRestrictionRepo;
        this.accountClassRestrictionRepo = accountClassRestrictionRepo;
        this.codeService = codeService;
        this.modelMapper = modelMapper;
        this.accountRepo = accountRepo;
        this.messageSource = messageSource;
        this.entityManager = entityManager;
        this.userAccountRestrictionRepo = userAccountRestrictionRepo;
    }




    @Override
    public boolean isAccountHidden(String accountNumber) {

        Account account = accountRepo.findFirstByAccountNumber(accountNumber);
        if (account != null) {
            return !"Y".equals(account.getHiddenFlag());
        }
        return true;
    }

    @Override
    @Verifiable(operation = "ADD_ACCT_RESTRICT", description = "Adding Account Restriction")
    public String addAccountRestriction(AccountRestrictionDTO accountRestrictionDTO) throws InternetBankingException {

        validateNoAccountDuplication(accountRestrictionDTO);
        try {
            AccountRestriction accountRestriction = convertAccountRestrictionDTOToEntity(accountRestrictionDTO);
            accountRestriction.setDateCreated(new Date());
            accountRestrictionRepo.save(accountRestriction);
            return messageSource.getMessage("account.restrict.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.restrict.add.failure", null, locale), e);
        }
    }


    @Override
    @Verifiable(operation = "UPDATE_ACCT_RESTRICT", description = "Updating Account Restriction")
    public String updateAccountRestriction(AccountRestrictionDTO accountRestrictionDTO) throws InternetBankingException {

        validateNoAccountDuplication(accountRestrictionDTO);
        try {
            AccountRestriction accountRestriction = accountRestrictionRepo.findById(accountRestrictionDTO.getId()).get();
            entityManager.detach(accountRestriction);
            accountRestriction.setVersion(accountRestrictionDTO.getVersion());
            accountRestriction.setRestrictionType(accountRestrictionDTO.getRestrictionType());
            accountRestriction.setRestrictionValue(accountRestrictionDTO.getRestrictionValue());
            accountRestriction.setRestrictedFor(accountRestrictionDTO.getRestrictedFor());
            accountRestrictionRepo.save(accountRestriction);
            return messageSource.getMessage("account.restrict.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.restrict.update.failure", null, locale), e);
        }
    }

    @Override
    public AccountRestrictionDTO getAccountRestriction(Long id) {
        AccountRestriction accountRestriction = accountRestrictionRepo.findById(id).get();
        return convertAccountRestrictionEntityToDTO(accountRestriction);
    }

    @Override
    public AccountClassRestrictionDTO getAccountClassRestriction(Long id) {
        AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findById(id).get();
        return convertAccountClassRestrictionEntityToDTO(accountClassRestriction);
    }

    @Override
    @Transactional
    @Verifiable(operation = "DELETE_ACCT_RESTRICT", description = "Deleting Account Restriction")
    public String deleteAccountRestriction(Long id) throws InternetBankingException {
        try {
            AccountRestriction accountRestriction = accountRestrictionRepo.findById(id).get();
            accountRestrictionRepo.delete(accountRestriction);
            return messageSource.getMessage("account.restrict.delete.success", null, LocaleContextHolder.getLocale());

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("account.restrict.delete.failure", null, locale), e);

        }
    }

    @Override
    public String addAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException {

        validateNoAccountClassDuplication(accountClassRestrictionDTO);
        try {
            AccountClassRestriction accountClassRestriction = convertAccountClassRestrictionDTOToEntity(accountClassRestrictionDTO);
            accountClassRestriction.setDateCreated(new Date());
            accountClassRestrictionRepo.save(accountClassRestriction);
            return messageSource.getMessage("class.restrict.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("class.restrict.add.failure", null, locale), e);
        }
    }

    @Override
    public String updateAccountClassRestriction(AccountClassRestrictionDTO accountClassRestrictionDTO) throws InternetBankingException {

        validateNoAccountClassDuplication(accountClassRestrictionDTO);
        try {
            AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findById(accountClassRestrictionDTO.getId()).get();
            entityManager.detach(accountClassRestriction);
            accountClassRestriction.setVersion(accountClassRestrictionDTO.getVersion());
            accountClassRestriction.setAccountClass(accountClassRestrictionDTO.getAccountClass());
            accountClassRestriction.setRestrictionType(accountClassRestrictionDTO.getRestrictionType());
            accountClassRestrictionRepo.save(accountClassRestriction);
            return messageSource.getMessage("class.restrict.update.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("class.restrict.update.failure", null, locale), e);

        }
    }

    @Override
    public String deleteAccountClassRestriction(Long id) throws InternetBankingException {
        try {
            AccountClassRestriction accountClassRestriction = accountClassRestrictionRepo.findById(id).get();
            entityManager.detach(accountClassRestriction);
            accountClassRestriction.setDeletedOn(new Date());
            accountClassRestrictionRepo.save(accountClassRestriction);
            accountClassRestrictionRepo.deleteById(id);
            return messageSource.getMessage("class.restrict.delete.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("class.restrict.update.failure", null, locale), e);

        }

    }

    @Override
    public boolean isAccountRestrictedForDebit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("ACCOUNT", accountNumber, "DEBIT");
        if (accountRestriction != null) {

            isRestricted = true;

        }
        return isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForCredit(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("ACCOUNT", accountNumber, "CREDIT");
        if (accountRestriction != null) {

            isRestricted = true;

        }
        return isRestricted;
    }


    @Override
    public boolean isAccountRestrictedForView(String accountNumber) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("ACCOUNT", accountNumber, "VIEW");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }

    @Override
    public boolean isAccountSchemeTypeRestrictedForDebit(String schemeType) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("SCHEME_TYPE", schemeType, "DEBIT");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }

    @Override
    public boolean isAccountSchemeTypeRestrictedForCredit(String schemeType) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("SCHEME_TYPE", schemeType, "CREDIT");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }


    @Override
    public boolean isAccountSchemeTypeRestrictedForView(String schemeType) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("SCHEME_TYPE", schemeType, "VIEW");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }


    @Override
    public boolean isAccountSchemeCodeRestrictedForDebit(String schemeCode) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("SCHEME_CODE", schemeCode, "DEBIT");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }


    @Override
    public boolean isAccountSchemeCodeRestrictedForCredit(String schemeCode) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("SCHEME_CODE", schemeCode, "CREDIT");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }


    @Override
    public boolean isAccountSchemeCodeRestrictedForView(String schemeCode) {
        boolean isRestricted = false;
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor("SCHEME_CODE", schemeCode, "VIEW");
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForViewFromUser(Long accountId, Long userId){
        boolean isRestricted = false;
        UserAccountRestriction accountRestriction = userAccountRestrictionRepo.findByCorporateUserIdAndAccountIdAndRestrictionType(userId,accountId, UserAccountRestriction.RestrictionType.VIEW);
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }

    @Override
    public boolean isAccountRestrictedForTransactionFromUser(Long accountId, Long userId){
        boolean isRestricted = false;
        UserAccountRestriction accountRestriction = userAccountRestrictionRepo.findByCorporateUserIdAndAccountIdAndRestrictionType(userId,accountId, UserAccountRestriction.RestrictionType.TRANSACTION);
        if (accountRestriction != null) {
            isRestricted = true;
        }
        return isRestricted;
    }

    @Override
    public Page<AccountRestrictionDTO> getAccountRestrictions(Pageable pageable) {
        return accountRestrictionRepo.findAll(pageable).map(this::convertAccountRestrictionEntityToDTO);
    }

    @Override
    public Page<AccountClassRestrictionDTO> getAccountClassRestrictions(Pageable pageable) {
       return accountClassRestrictionRepo.findAll(pageable)
                .map(this::convertAccountClassRestrictionEntityToDTO);
    }

    private void validateNoAccountDuplication(AccountRestrictionDTO accountRestrictionDTO) throws DuplicateObjectException {
        AccountRestriction accountRestriction = accountRestrictionRepo.findFirstByRestrictionTypeAndRestrictionValueIgnoreCaseAndRestrictedFor(accountRestrictionDTO.getRestrictionType(), accountRestrictionDTO.getRestrictionValue(), accountRestrictionDTO.getRestrictedFor());
        if (accountRestrictionDTO.getId() == null && accountRestriction != null) {
            throw new DuplicateObjectException(String.format(messageSource.getMessage("account.restrict.exists", null, locale), accountRestrictionDTO.getRestrictionValue())); //Duplication on creation
        }
        if (accountRestriction != null && (!accountRestriction.getId().equals(accountRestrictionDTO.getId()))) {
            throw new DuplicateObjectException(String.format(messageSource.getMessage("account.restrict.exists", null, locale), accountRestrictionDTO.getRestrictionValue())); //Duplication on update

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



    private AccountClassRestrictionDTO convertAccountClassRestrictionEntityToDTO(AccountClassRestriction accountClassRestriction) {
        AccountClassRestrictionDTO accountClassRestrictionDTO = modelMapper.map(accountClassRestriction, AccountClassRestrictionDTO.class);
        accountClassRestrictionDTO.setDateCreated(DateFormatter.format(accountClassRestriction.getDateCreated()));
        return accountClassRestrictionDTO;

    }


    private AccountClassRestriction convertAccountClassRestrictionDTOToEntity(AccountClassRestrictionDTO accountClassRestrictionDTO) {
        return this.modelMapper.map(accountClassRestrictionDTO, AccountClassRestriction.class);
    }



}
