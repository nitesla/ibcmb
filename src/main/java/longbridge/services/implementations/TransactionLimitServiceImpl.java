package longbridge.services.implementations;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
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

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    Locale locale = LocaleContextHolder.getLocale();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Verifiable(operation="ADD_TRANS_LIMIT",description="Adding a Global Limit")
    public String addGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException {
        try {
            GlobalLimit globalLimit = convertGlobalLimitDTOToEntity(globalLimitDTO);
            globalLimitRepo.save(globalLimit);
            logger.info("Added global limit {}", globalLimit.toString());
            return messageSource.getMessage("limit.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.add.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="UPDATE_TRANS_LIMIT",description="Updating a Global Limit")
    public String updateGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException {
        try {
            GlobalLimit globalLimit = convertGlobalLimitDTOToEntity(globalLimitDTO);
            globalLimitRepo.save(globalLimit);
            logger.info("Updated global limit {}", globalLimit.toString());
            return messageSource.getMessage("limit.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.update.failure", null, locale), e);
        }
    }


    @Override
    public GlobalLimitDTO getCorporateGlobalLimit(Long id) {
        GlobalLimit globalLimit = globalLimitRepo.findOne(id);
        return convertGlobalLimitEntityToDTO(globalLimit);
    }


    @Override
    public GlobalLimitDTO getRetailGlobalLimit(Long id) {
        GlobalLimit globalLimit = globalLimitRepo.findOne(id);
        return convertGlobalLimitEntityToDTO(globalLimit);
    }


    @Override
    public List<GlobalLimitDTO> getCorporateGlobalLimits() {
        List<GlobalLimit> limitsDTOs = globalLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        List<GlobalLimitDTO> dtOs = convertGlobalLimitEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }


    @Override
    public List<GlobalLimitDTO> getRetailGlobalLimits() {
        List<GlobalLimit> limitsDTOs = globalLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        List<GlobalLimitDTO> dtOs = convertGlobalLimitEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }


    @Override
    public ClassLimitDTO getRetailClassLimit(Long id) {
        ClassLimit classLimit = classLimitRepo.findOne(id);
        return convertClassLimitEntityToDTO(classLimit);
    }

    @Override
    public ClassLimitDTO getCorporateClassLimit(Long id) {
        ClassLimit classLimit = classLimitRepo.findOne(id);
        return convertClassLimitEntityToDTO(classLimit);
    }

    @Override
    @Verifiable(operation="ADD_TRANS_LIMIT",description="Adding a Class Limit")
    public String addClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException {
        try {
            ClassLimit classLimit = convertClassLimitDTOToEntity(classLimitDTO);
            classLimitRepo.save(classLimit);
            logger.info("Added class limit {}", classLimit.toString());
            return messageSource.getMessage("limit.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.add.success", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="UPDATE_TRANS_LIMIT",description="Updating a Class Limit")
    public String updateClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException {
        try {
            ClassLimit classLimit = convertClassLimitDTOToEntity(classLimitDTO);
            classLimitRepo.save(classLimit);
            logger.info("Update class limit {}", classLimit.toString());
            return messageSource.getMessage("limit.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.update.success", null, locale), e);
        }
    }

    @Override
    public List<ClassLimitDTO> getCorporateClassLimits() {
        List<ClassLimit> limitsDTOs = classLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        List<ClassLimitDTO> dtOs = convertClassLimitEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }

    @Override
    public List<ClassLimitDTO> getRetailClassLimits() {
        List<ClassLimit> limitsDTOs = classLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        List<ClassLimitDTO> dtOs = convertClassLimitEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }


    @Override
    public AccountLimitDTO getRetailAccountLimit(Long id) {
        AccountLimit accountLimit = accountLimitRepo.findOne(id);
        return convertAccountLimitEntityToDTO(accountLimit);
    }

    @Override
    public AccountLimitDTO getCorporateAccountLimit(Long id) {
        AccountLimit accountLimit = accountLimitRepo.findOne(id);
        return convertAccountLimitEntityToDTO(accountLimit);
    }

    @Override
    @Verifiable(operation="ADD_TRANS_LIMIT",description="Adding an Account Limit")
    public String addAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException {
        try {
            AccountLimit accountLimit = convertAccountLimitDTOToEntity(accountLimitDTO);
            accountLimitRepo.save(accountLimit);
            logger.info("Added account limit {}", accountLimit.toString());
            return messageSource.getMessage("limit.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.add.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="UPDATE_TRANS_LIMIT",description="Updating an Account Limit")
    public String updateAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException {
        try {
            AccountLimit accountLimit = convertAccountLimitDTOToEntity(accountLimitDTO);
            accountLimitRepo.save(accountLimit);
            logger.info("Updated account limit {}", accountLimit.toString());
            return messageSource.getMessage("limit.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.update.failure", null, locale), e);
        }
    }

    @Override
    public List<AccountLimitDTO> getCorporateAccountLimits() {
        List<AccountLimit> limitsDTOs = accountLimitRepo.findByCustomerType(UserType.CORPORATE.toString());
        List<AccountLimitDTO> dtOs = convertAccountLimitEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }

    @Override
    public List<AccountLimitDTO> getRetailAccountLimits() {
        List<AccountLimit> limitsDTOs = accountLimitRepo.findByCustomerType(UserType.RETAIL.toString());
        List<AccountLimitDTO> dtOs = convertAccountLimitEntitiesToDTOs(limitsDTOs);
        return dtOs;
    }

    @Override
    @Verifiable(operation="DELETE_TRANS_LIMIT",description="Deleting a Coporate Account Limit")
    public String deleteCorporateAccountLimit(Long id) throws InternetBankingException {
        try {
            AccountLimit limit = accountLimitRepo.findOne(id);
            accountLimitRepo.delete(limit);
            logger.info("Deleted account limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="DELETE_TRANS_LIMIT",description="Deleting a Coporate Class Limit")
    public String deleteCorporateClassLimit(Long id) throws InternetBankingException {
        try {
            ClassLimit classLimit = classLimitRepo.findOne(id);
            classLimitRepo.delete(classLimit);
            logger.info("Deleted class limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="DELETE_TRANS_LIMIT",description="Deleting a Corporate Global Limit")
    public String deleteCorporateGlobalLimit(Long id) throws InternetBankingException {
        try {
           GlobalLimit globalLimit = globalLimitRepo.findOne(id);
           globalLimitRepo.delete(globalLimit);
            logger.info("Deleted global limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="DELETE_TRANS_LIMIT",description="Deleting a  Retail Account Limit")
    public String deleteRetailAccountLimit(Long id) throws InternetBankingException {
        try {
           AccountLimit accountLimit =  accountLimitRepo.findOne(id);
           accountLimitRepo.delete(accountLimit);
            logger.info("Deleted account limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="DELETE_TRANS_LIMIT",description="Deleting a Retail Class Limit")
    public String deleteRetailClassLimit(Long id) throws InternetBankingException {
        try {
            ClassLimit classLimit = classLimitRepo.findOne(id);
            classLimitRepo.delete(classLimit);
            logger.info("Deleted class limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("limit.delete.failure", null, locale), e);
        }
    }

    @Override
    @Verifiable(operation="DELETE_TRANS_LIMIT",description="Deleting a Retail Global Limit")
    public String deleteRetailGlobalLimit(Long id) throws InternetBankingException {
        try {
           GlobalLimit globalLimit = globalLimitRepo.findOne(id);
           globalLimitRepo.delete(globalLimit);
            logger.info("Deleted global limit with Id {}", id);
            return messageSource.getMessage("limit.delete.success", null, locale);
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
        GlobalLimit globalLimit = modelMapper.map(limit, GlobalLimit.class);
        return globalLimit;
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
        ClassLimitDTO classLimitDTO = modelMapper.map(limit, ClassLimitDTO.class);
        return classLimitDTO;
    }

    private ClassLimit convertClassLimitDTOToEntity(ClassLimitDTO limit) {
        ClassLimit classLimit = modelMapper.map(limit, ClassLimit.class);
        return classLimit;
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
        AccountLimitDTO accountLimitDTO = modelMapper.map(limit, AccountLimitDTO.class);
        return accountLimitDTO;
    }

    private AccountLimit convertAccountLimitDTOToEntity(AccountLimitDTO limit) {
        AccountLimit accountLimit = modelMapper.map(limit, AccountLimit.class);
        return accountLimit;
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

        if (transferType.equals(TransferType.CORONATION_BANK_TRANSFER)) {

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
                if (amount.compareTo(accountLimit.getMaxLimit()) > 0) {
                    return true;
                }
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
                if (amount.compareTo(accountLimit.getMaxLimit()) > 0) {
                    return true;
                }
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
                if (amount.compareTo(accountLimit.getMaxLimit()) > 0) {
                    return true;
                }
            }
        } else if (transferType.equals(TransferType.CORONATION_BANK_TRANSFER.NAPS)) {

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
                if (amount.compareTo(accountLimit.getMaxLimit()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
