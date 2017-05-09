package longbridge.services.implementations;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.AccountLimitRepo;
import longbridge.repositories.ClassLimitRepo;
import longbridge.repositories.GlobalLimitRepo;
import longbridge.services.CodeService;
import longbridge.services.TransactionLimitService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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

    Locale locale = LocaleContextHolder.getLocale();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String addGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException {
        GlobalLimit globalLimit = convertGlobalLimitDTOToEntity(globalLimitDTO);
        globalLimitRepo.save(globalLimit);
        logger.info("Added global limit {}",globalLimit.toString());
        return messageSource.getMessage("limit.add.success",null,locale);
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
    public String addClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException {
        ClassLimit classLimit = convertClassLimitDTOToEntity(classLimitDTO);
        classLimitRepo.save(classLimit);
        logger.info("Added class limit {}",classLimit.toString());
        return messageSource.getMessage("limit.add.success",null,locale);

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
    public String addAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException {
        AccountLimit accountLimit = convertAccountLimitDTOToEntity(accountLimitDTO);
        accountLimitRepo.save(accountLimit);
        logger.info("Added account limit {}",accountLimit.toString());
        return messageSource.getMessage("limit.add.success",null,locale);

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

    private GlobalLimitDTO convertGlobalLimitEntityToDTO(GlobalLimit limit) {
        GlobalLimitDTO globalLimitDTO = modelMapper.map(limit, GlobalLimitDTO.class);
        globalLimitDTO.setStartDate(dateFormatter.format(limit.getEffectiveDate()));
        globalLimitDTO.setEffectiveDate(limit.getEffectiveDate());
        globalLimitDTO.setFrequency(codeService.getByTypeAndCode("FREQUENCY", limit.getFrequency()).getDescription());
        return globalLimitDTO;
    }

    private GlobalLimit convertGlobalLimitDTOToEntity(GlobalLimitDTO limit) {
        GlobalLimit globalLimit = modelMapper.map(limit, GlobalLimit.class);
        try {
            globalLimit.setEffectiveDate(dateFormatter.parse(limit.getStartDate()));
        } catch (ParseException e) {
            logger.error("Could not parse date {}", e.toString());
        }
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
        classLimitDTO.setStartDate(dateFormatter.format(limit.getEffectiveDate()));
        classLimitDTO.setEffectiveDate(limit.getEffectiveDate());
        return classLimitDTO;
    }

    private ClassLimit convertClassLimitDTOToEntity(ClassLimitDTO limit) {
        ClassLimit classLimit = modelMapper.map(limit, ClassLimit.class);
        try {
            classLimit.setEffectiveDate(dateFormatter.parse(limit.getStartDate()));
        } catch (ParseException e) {
            logger.error("Could not parse date {}", e.toString());
        }
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
        accountLimitDTO.setStartDate(dateFormatter.format(limit.getEffectiveDate()));
        accountLimitDTO.setEffectiveDate(limit.getEffectiveDate());
        return accountLimitDTO;
    }

    private AccountLimit convertAccountLimitDTOToEntity(AccountLimitDTO limit) {
        AccountLimit accountLimit = modelMapper.map(limit, AccountLimit.class);
        try {
            accountLimit.setEffectiveDate(dateFormatter.parse(limit.getStartDate()));
        } catch (ParseException e) {
            logger.error("Could not parse date {}", e.toString());
        }
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


}
