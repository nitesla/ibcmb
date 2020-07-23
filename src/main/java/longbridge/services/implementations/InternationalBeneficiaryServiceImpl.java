package longbridge.services.implementations;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.InternationalBeneficiaryService;
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

@Service
public class InternationalBeneficiaryServiceImpl implements InternationalBeneficiaryService {


    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MessageSource messageSource;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private InternationalBeneficiaryRepo internationalBeneficiaryRepo;
    private Locale locale = LocaleContextHolder.getLocale();


    public InternationalBeneficiaryServiceImpl() {
    }

    @Autowired
    public InternationalBeneficiaryServiceImpl(InternationalBeneficiaryRepo internationalBeneficiaryRepo) {
        this.internationalBeneficiaryRepo = internationalBeneficiaryRepo;
    }

    @Override
    public String addInternationalBeneficiary(InternationalBeneficiaryDTO beneficiary) throws InternetBankingException {
        try {
            InternationalBeneficiary internationalBeneficiary = convertDTOToEntity(beneficiary);
            internationalBeneficiary.setUser(getCurrentUser());
            validateBeneficiary(internationalBeneficiary,getCurrentUser());
            this.internationalBeneficiaryRepo.save(internationalBeneficiary);
            logger.info("International beneficiary {} has been added for user {}", internationalBeneficiary.toString(), getCurrentUser().getUserName());
            return messageSource.getMessage("beneficiary.add.success", null, locale);


        }
        catch(DuplicateObjectException doe){
            throw doe;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.add.failure", null, locale), e);
        }
    }



    @Override
    public String deleteInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException {
        try {
            internationalBeneficiaryRepo.delete(beneficiaryId);
            logger.info("Deleted beneficiary with Id{}", beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }


    }

    @Override
    public InternationalBeneficiaryDTO getInternationalBeneficiary(Long id) {
        return convertEntityToDTO(internationalBeneficiaryRepo.findOneById(id));
    }

    @Override
    public Iterable<InternationalBeneficiary> getInternationalBeneficiaries() {
        return internationalBeneficiaryRepo.findByUser(getCurrentUser());
    }

    @Override
    public boolean doesBeneficiaryExist(RetailUser user, InternationalBeneficiaryDTO internationalBeneficiaryDTO) {
        InternationalBeneficiary internationalBeneficiary = internationalBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), internationalBeneficiaryDTO.getAccountNumber());
        return internationalBeneficiary!=null;
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
        return internationalBeneficiaryDTO;
    }

    @Override
    public InternationalBeneficiary convertDTOToEntity(InternationalBeneficiaryDTO internationalBeneficiaryDTO) {
        return modelMapper.map(internationalBeneficiaryDTO, InternationalBeneficiary.class);
    }




    public void validateBeneficiary(InternationalBeneficiary internationalBeneficiary, User user) {
        if (internationalBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), internationalBeneficiary.getAccountNumber()) != null)
            throw new DuplicateObjectException(messageSource.getMessage("beneficiary.exist",null,locale));

    }


    private RetailUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (RetailUser)userPrincipal.getUser();
        }

        return (null) ;


    }

}
