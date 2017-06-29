package longbridge.services.implementations;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpInterBen;
import longbridge.models.CorporateUser;
import longbridge.repositories.CorpInternationalBeneficiaryRepo;
import longbridge.services.CorpInternationalBeneficiaryService;
import longbridge.utils.Verifiable;
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
 * Created by SYLVESTER on 5/22/2017.
 */
@Service
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
    @Verifiable(operation="Add_Corp_Inter_Beneficiary",description="Add Corperate International Beneficiary")
    public String addCorpInternationalBeneficiary(CorporateUser user, CorpInternationalBeneficiaryDTO beneficiary) throws InternetBankingException {
        CorpInterBen corpInterBen = convertDTOToEntity(beneficiary);
        corpInterBen.setCorporate(user.getCorporate());
        this.corpInternationalBeneficiaryRepo.save(corpInterBen);
        logger.info("CorpInternational beneficiary {} has been added for user {}", corpInterBen.toString(),user.getUserName());
        return messageSource.getMessage("beneficiary.add.success",null,locale);
    }

    @Override
    @Verifiable(operation="Delete_Corp_Inter_Beneficiary",description="Delete Corperate International Beneficiary")
    public String deleteCorpInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException {
        corpInternationalBeneficiaryRepo.delete(beneficiaryId);
        logger.info("Deleted beneficiary with Id{}", beneficiaryId);
        return messageSource.getMessage("beneficiary.delete.success",null,locale);

    }

    @Override
    public CorpInterBen getCorpInternationalBeneficiary(Long id) {
        return corpInternationalBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<CorpInterBen> getCorpInternationalBeneficiaries(CorporateUser user) {
        return corpInternationalBeneficiaryRepo.findByCorporate(user.getCorporate());
    }

    @Override
    public List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInterBen> corpInternationalBeneficiaries) {
        List<CorpInternationalBeneficiaryDTO> corpInternationalBeneficiaryDTOList = new ArrayList<>();
        for(CorpInterBen internationalBeneficiary: corpInternationalBeneficiaries){
            CorpInternationalBeneficiaryDTO benDTO = convertEntityToDTO(internationalBeneficiary);
            corpInternationalBeneficiaryDTOList.add(benDTO);
        }
        return corpInternationalBeneficiaryDTOList;
    }

    @Override
    public CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInterBen corpInterBen) {
        CorpInternationalBeneficiaryDTO corpInternationalBeneficiaryDTO = new CorpInternationalBeneficiaryDTO();
        corpInternationalBeneficiaryDTO.setAccountName(corpInterBen.getAccountName());
        corpInternationalBeneficiaryDTO.setAccountNumber(corpInterBen.getAccountNumber());
        corpInternationalBeneficiaryDTO.setBeneficiaryBank(corpInterBen.getBeneficiaryBank());
        corpInternationalBeneficiaryDTO.setSwiftCode(corpInterBen.getSwiftCode());
        corpInternationalBeneficiaryDTO.setSortCode(corpInterBen.getSortCode());
        corpInternationalBeneficiaryDTO.setBeneficiaryAddress(corpInterBen.getBeneficiaryAddress());
        corpInternationalBeneficiaryDTO.setIntermediaryBankAccountNumber(corpInterBen.getIntermediaryBankAcctNo());
        corpInternationalBeneficiaryDTO.setIntermediaryBankName(corpInterBen.getIntermediaryBankName());
        return  modelMapper.map(corpInterBen,CorpInternationalBeneficiaryDTO.class);
    }

    @Override
    public CorpInterBen convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO) {
        return  modelMapper.map(internationalBeneficiaryDTO,CorpInterBen.class);
    }
}
