package longbridge.services.implementations;

import longbridge.dtos.AccountLimitDTO;
import longbridge.dtos.ClassLimitDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.*;
import longbridge.repositories.AccountLimitRepo;
import longbridge.repositories.ClassLimitRepo;
import longbridge.repositories.GlobalLimitRepo;
import longbridge.services.AccountService;
import longbridge.services.CodeService;
import longbridge.services.TransactionLimitService;
import longbridge.utils.TransferType;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/25/2017.
 */
@Service
public class TransactionLimitServiceImpl implements TransactionLimitService {

    @Autowired
    GlobalLimitRepo globalLimitRepo;
    @Autowired
    ClassLimitRepo classLimitRepo;
    @Autowired
    AccountLimitRepo accountLimitRepo;

    @Autowired
    CodeService codeService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    AccountService accountService;

    final Locale locale = LocaleContextHolder.getLocale();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Verifiable(operation = "ADD_TRANS_LIMIT", description = "Adding a Global Limit")
    public String addGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException {
        try {
            GlobalLimit globalLimit = convertGlobalLimitDTOToEntity(globalLimitDTO);
            globalLimitRepo.save(globalLimit);
            logger.info("Added global limit {}", globalLimit.toString());
            return messageSource.getMessage("limit.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.add.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "UPDATE_TRANS_LIMIT", description = "Updating a Global Limit")
    public String updateGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException {
        try {
            GlobalLimit globalLimit = convertGlobalLimitDTOToEntity(globalLimitDTO);
            globalLimitRepo.save(globalLimit);
            logger.info("Updated global limit {}", globalLimit.toString());
            return messageSource.getMessage("limit.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.update.failure", null, locale), e);
        }
    }


    @Override
    public GlobalLimitDTO getCorporateGlobalLimit(Long id) {
        GlobalLimit globalLimit = globalLimitRepo.findById(id).get();
        return convertGlobalLimitEntityToDTO(globalLimit);
    }


    @Override
    public GlobalLimitDTO getRetailGlobalLimit(Long id) {
        GlobalLimit globalLimit = globalLimitRepo.findById(id).get();
        return convertGlobalLimitEntityToDTO(globalLimit);
    }


    @Override
    public List<GlobalLimitDTO> getCorporateGlobalLimits() {
        List<GlobalLimit> limitsDTOs = globalLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        return convertGlobalLimitEntitiesToDTOs(limitsDTOs);
    }


    @Override
    public List<GlobalLimitDTO> getRetailGlobalLimits() {
        List<GlobalLimit> limitsDTOs = globalLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        return convertGlobalLimitEntitiesToDTOs(limitsDTOs);
    }


    @Override
    public ClassLimitDTO getRetailClassLimit(Long id) {
        ClassLimit classLimit = classLimitRepo.findById(id).get();
        return convertClassLimitEntityToDTO(classLimit);
    }

    @Override
    public ClassLimitDTO getCorporateClassLimit(Long id) {
        ClassLimit classLimit = classLimitRepo.findById(id).get();
        return convertClassLimitEntityToDTO(classLimit);
    }

    @Override
    @Verifiable(operation = "ADD_TRANS_LIMIT", description = "Adding a Class Limit")
    public String addClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException {
        try {
            ClassLimit classLimit = convertClassLimitDTOToEntity(classLimitDTO);
            classLimitRepo.save(classLimit);
            logger.info("Added class limit {}", classLimit.toString());
            return messageSource.getMessage("limit.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.add.success", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "UPDATE_TRANS_LIMIT", description = "Updating a Class Limit")
    public String updateClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException {
        try {
            ClassLimit classLimit = convertClassLimitDTOToEntity(classLimitDTO);
            classLimitRepo.save(classLimit);
            logger.info("Update class limit {}", classLimit.toString());
            return messageSource.getMessage("limit.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.update.success", null, locale), e);
        }
    }

    @Override
    public List<ClassLimitDTO> getCorporateClassLimits() {
        List<ClassLimit> limitsDTOs = classLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        return convertClassLimitEntitiesToDTOs(limitsDTOs);
    }

    @Override
    public List<ClassLimitDTO> getRetailClassLimits() {
        List<ClassLimit> limitsDTOs = classLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        return convertClassLimitEntitiesToDTOs(limitsDTOs);
    }


    @Override
    public AccountLimitDTO getRetailAccountLimit(Long id) {
        AccountLimit accountLimit = accountLimitRepo.findById(id).get();
        return convertAccountLimitEntityToDTO(accountLimit);
    }

    @Override
    public AccountLimitDTO getCorporateAccountLimit(Long id) {
        AccountLimit accountLimit = accountLimitRepo.findById(id).get();
        return convertAccountLimitEntityToDTO(accountLimit);
    }

    @Override
    @Verifiable(operation = "ADD_TRANS_LIMIT", description = "Adding an Account Limit")
    public String addAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException {
        try {
            AccountLimit accountLimit = convertAccountLimitDTOToEntity(accountLimitDTO);
            accountLimitRepo.save(accountLimit);
            logger.info("Added account limit {}", accountLimit.toString());
            return messageSource.getMessage("limit.add.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.add.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "UPDATE_TRANS_LIMIT", description = "Updating an Account Limit")
    public String updateAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException {
        try {
            AccountLimit accountLimit = convertAccountLimitDTOToEntity(accountLimitDTO);
            accountLimitRepo.save(accountLimit);
            logger.info("Updated account limit {}", accountLimit.toString());
            return messageSource.getMessage("limit.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.update.failure", null, locale), e);
        }
    }

    @Override
    public List<AccountLimitDTO> getCorporateAccountLimits() {
        List<AccountLimit> limitsDTOs = accountLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        return convertAccountLimitEntitiesToDTOs(limitsDTOs);
    }

    @Override
    public List<AccountLimitDTO> getRetailAccountLimits() {
        List<AccountLimit> limitsDTOs = accountLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        return convertAccountLimitEntitiesToDTOs(limitsDTOs);
    }

    @Override
    @Verifiable(operation = "DELETE_TRANS_LIMIT", description = "Deleting a Coporate Account Limit")
    public String deleteCorporateAccountLimit(Long id) throws InternetBankingException {
        try {
            AccountLimit limit = accountLimitRepo.findById(id).get();
            accountLimitRepo.delete(limit);
            logger.info("Deleted account limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_TRANS_LIMIT", description = "Deleting a Coporate Class Limit")
    public String deleteCorporateClassLimit(Long id) throws InternetBankingException {
        try {
            ClassLimit classLimit = classLimitRepo.findById(id).get();
            classLimitRepo.delete(classLimit);
            logger.info("Deleted class limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_TRANS_LIMIT", description = "Deleting a Corporate Global Limit")
    public String deleteCorporateGlobalLimit(Long id) throws InternetBankingException {
        try {
            GlobalLimit globalLimit = globalLimitRepo.findById(id).get();
            globalLimitRepo.delete(globalLimit);
            logger.info("Deleted global limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_TRANS_LIMIT", description = "Deleting a  Retail Account Limit")
    public String deleteRetailAccountLimit(Long id) throws InternetBankingException {
        try {
            AccountLimit accountLimit = accountLimitRepo.findById(id).get();
            accountLimitRepo.delete(accountLimit);
            logger.info("Deleted account limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_TRANS_LIMIT", description = "Deleting a Retail Class Limit")
    public String deleteRetailClassLimit(Long id) throws InternetBankingException {
        try {
            ClassLimit classLimit = classLimitRepo.findById(id).get();
            classLimitRepo.delete(classLimit);
            logger.info("Deleted class limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation = "DELETE_TRANS_LIMIT", description = "Deleting a Retail Global Limit")
    public String deleteRetailGlobalLimit(Long id) throws InternetBankingException {
        try {
            GlobalLimit globalLimit = globalLimitRepo.findById(id).get();
            globalLimitRepo.delete(globalLimit);
            logger.info("Deleted global limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    private GlobalLimitDTO convertGlobalLimitEntityToDTO(GlobalLimit limit) {
        GlobalLimitDTO globalLimitDTO = modelMapper.map(limit, GlobalLimitDTO.class);
        globalLimitDTO.setFrequency(codeService.getByTypeAndCode("FREQUENCY", limit.getFrequency()).getDescription());
        return globalLimitDTO;
    }

    private GlobalLimit convertGlobalLimitDTOToEntity(GlobalLimitDTO limit) {
        return modelMapper.map(limit, GlobalLimit.class);
    }


    private List<GlobalLimitDTO> convertGlobalLimitEntitiesToDTOs(Iterable<GlobalLimit> globalLimits) {
        List<GlobalLimitDTO> limitDTOList = new ArrayList<>();
        for (GlobalLimit globalLimit : globalLimits) {
            GlobalLimitDTO limitDTO = convertGlobalLimitEntityToDTO(globalLimit);
            limitDTOList.add(limitDTO);
        }
        return limitDTOList;
    }

    private ClassLimitDTO convertClassLimitEntityToDTO(ClassLimit limit) {
        return modelMapper.map(limit, ClassLimitDTO.class);
    }

    private ClassLimit convertClassLimitDTOToEntity(ClassLimitDTO limit) {
        return modelMapper.map(limit, ClassLimit.class);
    }


    private List<ClassLimitDTO> convertClassLimitEntitiesToDTOs(Iterable<ClassLimit> classLimits) {
        List<ClassLimitDTO> limitDTOList = new ArrayList<>();
        for (ClassLimit classLimit : classLimits) {
            ClassLimitDTO limitDTO = convertClassLimitEntityToDTO(classLimit);
            limitDTO.setFrequency(codeService.getByTypeAndCode("FREQUENCY", classLimit.getFrequency()).getDescription());
            limitDTOList.add(limitDTO);
        }
        return limitDTOList;

    }

    private AccountLimitDTO convertAccountLimitEntityToDTO(AccountLimit limit) {
        return modelMapper.map(limit, AccountLimitDTO.class);
    }

    private AccountLimit convertAccountLimitDTOToEntity(AccountLimitDTO limit) {
        return modelMapper.map(limit, AccountLimit.class);
    }


    private List<AccountLimitDTO> convertAccountLimitEntitiesToDTOs(Iterable<AccountLimit> accountLimits) {
        List<AccountLimitDTO> limitDTOList = new ArrayList<>();
        for (AccountLimit accountLimit : accountLimits) {
            AccountLimitDTO limitDTO = convertAccountLimitEntityToDTO(accountLimit);
            limitDTO.setFrequency(codeService.getByTypeAndCode("FREQUENCY", accountLimit.getFrequency()).getDescription());
            limitDTOList.add(limitDTO);
        }
        return limitDTOList;
    }

    @Override
    public boolean isAboveInternetBankingLimit(TransferType transferType, UserType customerType, String accountNumber, BigDecimal amount) {

        Account account = accountService.getAccountByAccountNumber(accountNumber);
        String accountClass = account.getSchemeCode();

        if (transferType.equals(TransferType.WITHIN_BANK_TRANSFER)) {

            GlobalLimit globalLimit = globalLimitRepo.findByChannel("CMB");
            ClassLimit classLimit = classLimitRepo.findByCustomerTypeAndAccountClassAndChannel(customerType.name(), accountClass, "CMB");
            AccountLimit accountLimit = accountLimitRepo.findByCustomerTypeAndAccountNumberAndChannel(customerType.name(), accountNumber, "CMB");

            if (globalLimit != null) {
                if (amount.compareTo(globalLimit.getMaxLimit()) > 0) {
                    return true;
                }
            }

            if (classLimit != null) {
                if (account.getSchemeCode().equals(classLimit.getAccountClass())) {
                    if (amount.compareTo(classLimit.getMaxLimit()) > 0) {
                        return true;
                    }
                }
            }

            if (accountLimit != null) {
                return amount.compareTo(accountLimit.getMaxLimit()) > 0;
            }


        } else if (transferType.equals(TransferType.NIP)) {

            GlobalLimit globalLimit = globalLimitRepo.findByChannel("NIP");
            ClassLimit classLimit = classLimitRepo.findByCustomerTypeAndAccountClassAndChannel(customerType.name(), accountClass, "NIP");
            AccountLimit accountLimit = accountLimitRepo.findByCustomerTypeAndAccountNumberAndChannel(customerType.name(), accountNumber, "NIP");


            if (globalLimit != null) {
                if (amount.compareTo(globalLimit.getMaxLimit()) > 0) {
                    return true;
                }
            }

            if (classLimit != null) {
                if (account.getSchemeCode().equals(classLimit.getAccountClass())) {
                    if (amount.compareTo(classLimit.getMaxLimit()) > 0) {
                        return true;
                    }
                }
            }

            if (accountLimit != null) {
                return amount.compareTo(accountLimit.getMaxLimit()) > 0;
            }
        } else if (transferType.equals(TransferType.RTGS)) {

            GlobalLimit globalLimit = globalLimitRepo.findByChannel("RTGS");
            ClassLimit classLimit = classLimitRepo.findByCustomerTypeAndAccountClassAndChannel(customerType.name(), accountClass, "RTGS");
            AccountLimit accountLimit = accountLimitRepo.findByCustomerTypeAndAccountNumberAndChannel(customerType.name(), accountNumber, "RTGS");

            if (globalLimit != null) {
                if (amount.compareTo(globalLimit.getMaxLimit()) > 0) {
                    return true;
                }
            }

            if (classLimit != null) {
                if (account.getSchemeCode().equals(classLimit.getAccountClass())) {
                    if (amount.compareTo(classLimit.getMaxLimit()) > 0) {
                        return true;
                    }
                }
            }

            if (accountLimit != null) {
                return amount.compareTo(accountLimit.getMaxLimit()) > 0;
            }
        } else if (transferType.equals(TransferType.NAPS)) {

            GlobalLimit globalLimit = globalLimitRepo.findByChannel("NAPS");
            ClassLimit classLimit = classLimitRepo.findByCustomerTypeAndAccountClassAndChannel(customerType.name(), accountClass, "NAPS");
            AccountLimit accountLimit = accountLimitRepo.findByCustomerTypeAndAccountNumberAndChannel(customerType.name(), accountNumber, "NAPS");

            if (globalLimit != null) {
                if (amount.compareTo(globalLimit.getMaxLimit()) > 0) {
                    return true;
                }
            }

            if (classLimit != null) {
                if (account.getSchemeCode().equals(classLimit.getAccountClass())) {
                    if (amount.compareTo(classLimit.getMaxLimit()) > 0) {
                        return true;
                    }
                }
            }

            if (accountLimit != null) {
                return amount.compareTo(accountLimit.getMaxLimit()) > 0;
            }
        }
        return false;
    }
}
