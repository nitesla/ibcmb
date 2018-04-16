package longbridge.services.implementations;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.Corporate;
import longbridge.repositories.CorpLocalBeneficiaryRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorpLocalBeneficiaryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private CorporateRepo corporateRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public CorpLocalBeneficiaryServiceImpl(CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo,CorporateRepo corporateRepo) {
        this.corpLocalBeneficiaryRepo = corpLocalBeneficiaryRepo;
        this.corporateRepo=corporateRepo;
    }


    @Override
    public String addCorpLocalBeneficiary(CorpLocalBeneficiaryDTO beneficiary) {


       try{
           validateBeneficiary(beneficiary);
           CorpLocalBeneficiary corpLocalBeneficiary=convertDTOToEntity(beneficiary);

           Corporate corporate= corporateRepo.findOne(getCurrentUser().getCorpId());
           corpLocalBeneficiary.setCorporate(corporate);
           this.corpLocalBeneficiaryRepo.save(corpLocalBeneficiary);
           logger.info("Beneficiary {} has been added", corpLocalBeneficiary.getAccountName());
           return messageSource.getMessage("beneficiary.add.success",null,locale);


       }catch (Exception e){
           throw new InternetBankingException(e.getMessage());
       }

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
    public Iterable<CorpLocalBeneficiary> getCorpLocalBeneficiaries(Corporate corporate) {
        return corpLocalBeneficiaryRepo.findByCorporate(corporate);

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
    private void validateBeneficiary(CorpLocalBeneficiaryDTO beneficiary) {

        if (corpLocalBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(getCurrentUser().getCorpId(),beneficiary.getAccountNumber()))
            throw new DuplicateObjectException("beneficiary.exist");
/*
        if (financialInstitutionRepo.findByInstitutionCode(beneficiary.getBeneficiaryBank())==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

        if (integrationService.doNameEnquiry(localBeneficiary.getBeneficiaryBank(), localBeneficiary.getAccountNumber()).getAccountName()==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

    */
    }

   private CustomUserPrincipal getCurrentUser(){


       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (!(authentication instanceof AnonymousAuthenticationToken)) {
           CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
           return userPrincipal;
       }

       return (null) ;


   }

}
