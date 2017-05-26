package longbridge.services.implementations;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;
import longbridge.repositories.CorpLocalBeneficiaryRepo;
import longbridge.services.CorpLocalBeneficiaryService;
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

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Service
public class CorpLocalBeneficiaryServiceImpl implements CorpLocalBeneficiaryService {

    private CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public CorpLocalBeneficiaryServiceImpl(CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo) {
        this.corpLocalBeneficiaryRepo = corpLocalBeneficiaryRepo;
    }


    @Override
    public String addCorpLocalBeneficiary(CorporateUser user, CorpLocalBeneficiaryDTO beneficiary) {
        CorpLocalBeneficiary corpLocalBeneficiary=convertDTOToEntity(beneficiary);
        corpLocalBeneficiary.setUser(user);
        this.corpLocalBeneficiaryRepo.save(corpLocalBeneficiary);
        logger.trace("Beneficiary {} has been added");
        return messageSource.getMessage("beneficiary.add.success",null,locale);

    }

    @Override
    public String deleteCorpLocalBeneficiary(Long beneficiaryId) {
        this.corpLocalBeneficiaryRepo.delete(beneficiaryId);
        logger.info("Beneficiary with Id {} deleted", beneficiaryId);
        return messageSource.getMessage("beneficiary.delete.success",null,locale);

    }

    @Override
    public CorpLocalBeneficiary getCorpLocalBeneficiary(Long id) {
        return corpLocalBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<CorpLocalBeneficiary> getCorpLocalBeneficiaries(CorporateUser user) {
        return corpLocalBeneficiaryRepo.findByUser(user);

    }

    @Override
    public List<CorpLocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries) {
        List<CorpLocalBeneficiaryDTO> corplocalBeneficiaryDTOList = new ArrayList<>();
        for(CorpLocalBeneficiary localBeneficiary: corpLocalBeneficiaries){
            CorpLocalBeneficiaryDTO benDTO = convertEntityToDTO(localBeneficiary);
            corplocalBeneficiaryDTOList.add(benDTO);
        }
        return corplocalBeneficiaryDTOList;
    }

    @Override
    public CorpLocalBeneficiaryDTO convertEntityToDTO(CorpLocalBeneficiary corpLocalBeneficiary) {

        CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO = new CorpLocalBeneficiaryDTO();
        corpLocalBeneficiaryDTO.setAccountName(corpLocalBeneficiary.getAccountName());
        corpLocalBeneficiaryDTO.setAccountNumber(corpLocalBeneficiary.getAccountNumber());
        corpLocalBeneficiaryDTO.setBeneficiaryBank(corpLocalBeneficiary.getBeneficiaryBank());
        corpLocalBeneficiaryDTO.setPreferredName(corpLocalBeneficiary.getPreferredName());
        return  modelMapper.map(corpLocalBeneficiary,CorpLocalBeneficiaryDTO.class);

    }

    @Override
    public CorpLocalBeneficiary convertDTOToEntity(CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO) {
        CorpLocalBeneficiary corpLocalBeneficiary=new CorpLocalBeneficiary();
        corpLocalBeneficiary.setAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpLocalBeneficiary.setAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());
        corpLocalBeneficiary.setBeneficiaryBank(corpLocalBeneficiaryDTO.getBeneficiaryBank());
        corpLocalBeneficiary.setPreferredName(corpLocalBeneficiaryDTO.getPreferredName());
        return  corpLocalBeneficiary;

    }
}
