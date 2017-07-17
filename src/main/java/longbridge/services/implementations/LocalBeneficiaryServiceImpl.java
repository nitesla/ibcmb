package longbridge.services.implementations;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.FinancialInstitutionRepo;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.services.IntegrationService;
import longbridge.services.LocalBeneficiaryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class LocalBeneficiaryServiceImpl implements LocalBeneficiaryService {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    MessageSource messageSource;
    @Autowired
    FinancialInstitutionRepo financialInstitutionRepo;
    @Autowired
    IntegrationService integrationService;
    Locale locale = LocaleContextHolder.getLocale();
    private LocalBeneficiaryRepo localBeneficiaryRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public LocalBeneficiaryServiceImpl(LocalBeneficiaryRepo localBeneficiaryRepo) {
        this.localBeneficiaryRepo = localBeneficiaryRepo;
    }

    @Override
    public String addLocalBeneficiary(RetailUser user, LocalBeneficiaryDTO beneficiary) throws InternetBankingException {

        try {
            LocalBeneficiary localBeneficiary = convertDTOToEntity(beneficiary);
            localBeneficiary.setUser(user);
            validateBeneficiary(localBeneficiary, user);
            localBeneficiaryRepo.save(localBeneficiary);
            logger.trace("Beneficiary {} has been added", localBeneficiary.toString());
            return messageSource.getMessage("beneficiary.add.success", null, locale);
        } catch (Exception e) {
            //throw new InternetBankingException(messageSource.getMessage("beneficiary.add.failure",null, locale), e);
            throw new InternetBankingException(e.getMessage());
        }


    }

    @Override
    public String deleteLocalBeneficiary(Long beneficiaryId) {

        try {
            this.localBeneficiaryRepo.delete(beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }


    }

    @Override
    public LocalBeneficiary getLocalBeneficiary(Long id) {
        return localBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<LocalBeneficiary> getLocalBeneficiaries(RetailUser user) {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIsNotNull(user);
    }

    @Override
    public Iterable<LocalBeneficiary> getBankBeneficiaries(RetailUser user) {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIgnoreCase(user,bankCode );
    }

    @Override
    public List<LocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<LocalBeneficiary> localBeneficiaries) {
        List<LocalBeneficiaryDTO> localBeneficiaryDTOList = new ArrayList<>();
        for (LocalBeneficiary localBeneficiary : localBeneficiaries) {
            LocalBeneficiaryDTO benDTO = convertEntityToDTO(localBeneficiary);
            localBeneficiaryDTOList.add(benDTO);
        }
        return localBeneficiaryDTOList;
    }

    @Override
    public LocalBeneficiaryDTO convertEntityToDTO(LocalBeneficiary localBeneficiary) {
        LocalBeneficiaryDTO localBeneficiaryDTO = new LocalBeneficiaryDTO();
        localBeneficiaryDTO.setAccountName(localBeneficiary.getAccountName());
        localBeneficiaryDTO.setAccountNumber(localBeneficiary.getAccountNumber());
        localBeneficiaryDTO.setBeneficiaryBank(localBeneficiary.getBeneficiaryBank());
        localBeneficiaryDTO.setPreferredName(localBeneficiary.getPreferredName());
        return modelMapper.map(localBeneficiary, LocalBeneficiaryDTO.class);
    }

    @Override
    public LocalBeneficiary convertDTOToEntity(LocalBeneficiaryDTO localBeneficiaryDTO) {
        return modelMapper.map(localBeneficiaryDTO, LocalBeneficiary.class);
    }

    private void validateBeneficiary(LocalBeneficiary localBeneficiary, User user) {
        if (localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), localBeneficiary.getAccountNumber()) != null)
            throw new DuplicateObjectException("beneficiary.exist");

        if (financialInstitutionRepo.findByInstitutionCode(localBeneficiary.getBeneficiaryBank())==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

       if (integrationService.doNameEnquiry(localBeneficiary.getBeneficiaryBank(), localBeneficiary.getAccountNumber()).getAccountName()==null)
           throw new InternetBankingException("transfer.beneficiary.invalid");
    }



}
