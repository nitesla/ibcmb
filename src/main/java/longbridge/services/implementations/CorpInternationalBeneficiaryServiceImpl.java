package longbridge.services.implementations;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpInternationalBeneficiary;
import longbridge.models.CorporateUser;
import longbridge.repositories.CorpInternationalBeneficiaryRepo;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.services.CorpInternationalBeneficiaryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public class CorpInternationalBeneficiaryServiceImpl implements CorpInternationalBeneficiaryService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CorpInternationalBeneficiaryRepo corpInternationalBeneficiaryRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public CorpInternationalBeneficiaryServiceImpl(CorpInternationalBeneficiaryRepo corpInternationalBeneficiaryRepo){
        this.corpInternationalBeneficiaryRepo=corpInternationalBeneficiaryRepo;
    }

    @Override
    public String addCorpInternationalBeneficiary(CorporateUser user, CorpInternationalBeneficiaryDTO beneficiary) throws InternetBankingException {
        CorpInternationalBeneficiary corpInternationalBeneficiary = convertDTOToEntity(beneficiary);
        corpInternationalBeneficiary.setUser(user);
        this.corpInternationalBeneficiaryRepo.save(corpInternationalBeneficiary);
        logger.info("CorpInternational beneficiary {} has been added for user {}", corpInternationalBeneficiary.toString(),user.getUserName());
        return messageSource.getMessage("beneficiary.add.success",null,locale);
    }

    @Override
    public String deleteCorpInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException {
        corpInternationalBeneficiaryRepo.delete(beneficiaryId);
        logger.info("Deleted beneficiary with Id{}", beneficiaryId);
        return messageSource.getMessage("beneficiary.delete.success",null,locale);

    }

    @Override
    public CorpInternationalBeneficiary getCorpInternationalBeneficiary(Long id) {
        return corpInternationalBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<CorpInternationalBeneficiary> getCorpInternationalBeneficiaries(CorporateUser user) {
        return corpInternationalBeneficiaryRepo.findByUser(user);
    }

    @Override
    public List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInternationalBeneficiary> corpInternationalBeneficiaries) {
        List<CorpInternationalBeneficiaryDTO> corpInternationalBeneficiaryDTOList = new ArrayList<>();
        for(CorpInternationalBeneficiary internationalBeneficiary: corpInternationalBeneficiaries){
            CorpInternationalBeneficiaryDTO benDTO = convertEntityToDTO(internationalBeneficiary);
            corpInternationalBeneficiaryDTOList.add(benDTO);
        }
        return corpInternationalBeneficiaryDTOList;
    }

    @Override
    public CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInternationalBeneficiary corpInternationalBeneficiary) {
        CorpInternationalBeneficiaryDTO corpInternationalBeneficiaryDTO = new CorpInternationalBeneficiaryDTO();
        corpInternationalBeneficiaryDTO.setAccountName(corpInternationalBeneficiary.getAccountName());
        corpInternationalBeneficiaryDTO.setAccountNumber(corpInternationalBeneficiary.getAccountNumber());
        corpInternationalBeneficiaryDTO.setBeneficiaryBank(corpInternationalBeneficiary.getBeneficiaryBank());
        corpInternationalBeneficiaryDTO.setSwiftCode(corpInternationalBeneficiary.getSwiftCode());
        corpInternationalBeneficiaryDTO.setSortCode(corpInternationalBeneficiary.getSortCode());
        corpInternationalBeneficiaryDTO.setBeneficiaryAddress(corpInternationalBeneficiary.getBeneficiaryAddress());
        corpInternationalBeneficiaryDTO.setIntermediaryBankAccountNumber(corpInternationalBeneficiary.getIntermediaryBankAccountNumber());
        corpInternationalBeneficiaryDTO.setIntermediaryBankName(corpInternationalBeneficiary.getIntermediaryBankName());
        return  modelMapper.map(corpInternationalBeneficiary,CorpInternationalBeneficiaryDTO.class);
    }

    @Override
    public CorpInternationalBeneficiary convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO) {
        return  modelMapper.map(internationalBeneficiaryDTO,CorpInternationalBeneficiary.class);
    }
}
