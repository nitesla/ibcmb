package longbridge.services.implementations;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.services.InternationalBeneficiaryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class InternationalBeneficiaryServiceImpl implements InternationalBeneficiaryService {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    MessageSource messageSource;
    Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private InternationalBeneficiaryRepo internationalBeneficiaryRepo;

    @Autowired
    public InternationalBeneficiaryServiceImpl(InternationalBeneficiaryRepo internationalBeneficiaryRepo) {
        this.internationalBeneficiaryRepo = internationalBeneficiaryRepo;
    }

    @Override
    public String addInternationalBeneficiary(RetailUser user, InternationalBeneficiaryDTO beneficiary) throws InternetBankingException {
        try {
            InternationalBeneficiary internationalBeneficiary = convertDTOToEntity(beneficiary);
            internationalBeneficiary.setUser(user);
            this.internationalBeneficiaryRepo.save(internationalBeneficiary);
            logger.info("International beneficiary {} has been added for user {}", internationalBeneficiary.toString(), user.getUserName());
            return messageSource.getMessage("beneficiary.add.success", null, locale);
        } catch (Exception e) {
            if (e.getMessage().equalsIgnoreCase("beneficiary.exist"))
                throw new InternetBankingException(messageSource.getMessage("beneficiary.exist", null, locale), e);

            throw new InternetBankingException(messageSource.getMessage("beneficiary.add.failure", null, locale), e);
        }
    }

    @Override
    public String deleteInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException {
        try {
            internationalBeneficiaryRepo.deleteById(beneficiaryId);
            logger.info("Deleted beneficiary with Id{}", beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }


    }

    @Override
    public InternationalBeneficiary getInternationalBeneficiary(Long id) {
        return internationalBeneficiaryRepo.findById(id).get();
    }

    @Override
    public Iterable<InternationalBeneficiary> getInternationalBeneficiaries(RetailUser user) {
        return internationalBeneficiaryRepo.findByUser(user);
    }

    @Override
    public List<InternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<InternationalBeneficiary> internationalBeneficiaries) {
        List<InternationalBeneficiaryDTO> internationalBeneficiaryDTOList = new ArrayList<>();
        for (InternationalBeneficiary internationalBeneficiary : internationalBeneficiaries) {
            InternationalBeneficiaryDTO benDTO = convertEntityToDTO(internationalBeneficiary);
            internationalBeneficiaryDTOList.add(benDTO);
        }
        return internationalBeneficiaryDTOList;
    }

    @Override
    public InternationalBeneficiaryDTO convertEntityToDTO(InternationalBeneficiary internationalBeneficiary) {
        InternationalBeneficiaryDTO internationalBeneficiaryDTO = new InternationalBeneficiaryDTO();
        internationalBeneficiaryDTO.setAccountName(internationalBeneficiary.getAccountName());
        internationalBeneficiaryDTO.setAccountNumber(internationalBeneficiary.getAccountNumber());
        internationalBeneficiaryDTO.setBeneficiaryBank(internationalBeneficiary.getBeneficiaryBank());
        internationalBeneficiaryDTO.setSwiftCode(internationalBeneficiary.getSwiftCode());
        internationalBeneficiaryDTO.setSortCode(internationalBeneficiary.getSortCode());
        internationalBeneficiaryDTO.setBeneficiaryAddress(internationalBeneficiary.getBeneficiaryAddress());
        internationalBeneficiaryDTO.setIntermediaryBankAcctNo(internationalBeneficiary.getIntermediaryBankAcctNo());
        internationalBeneficiaryDTO.setIntermediaryBankName(internationalBeneficiary.getIntermediaryBankName());
        return modelMapper.map(internationalBeneficiary, InternationalBeneficiaryDTO.class);
    }

    @Override
    public InternationalBeneficiary convertDTOToEntity(InternationalBeneficiaryDTO internationalBeneficiaryDTO) {
        return modelMapper.map(internationalBeneficiaryDTO, InternationalBeneficiary.class);
    }


    public void validateBeneficiary(InternationalBeneficiary internationalBeneficiary, User user) {
        if (internationalBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), internationalBeneficiary.getAccountNumber()) != null)
            throw new InternetBankingException("beneficiary.exist");

    }

}
