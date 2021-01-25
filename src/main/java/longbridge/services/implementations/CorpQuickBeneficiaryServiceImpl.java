package longbridge.services.implementations;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CorpQuickBeneficiaryRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorpQuickBeneficiaryService;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Locale;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Service
public class CorpQuickBeneficiaryServiceImpl implements CorpQuickBeneficiaryService {

    private final CorpQuickBeneficiaryRepo corpQuickBeneficiaryRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private MailService mailService;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public CorpQuickBeneficiaryServiceImpl(CorpQuickBeneficiaryRepo corpQuickBeneficiaryRepo, CorporateRepo corporateRepo) {
        this.corpQuickBeneficiaryRepo = corpQuickBeneficiaryRepo;
    }



    @Override
    public String addCorpQuickBeneficiary(CorpLocalBeneficiaryDTO beneficiary) {


        try{
            validateBeneficiary(beneficiary);
            CorpQuickBeneficiary corpQuickBeneficiary=convertDTOToEntity(beneficiary);

            Corporate corporate= getCurrentUser().getCorporate();
            corpQuickBeneficiary.setCorporate(corporate);
            this.corpQuickBeneficiaryRepo.save(corpQuickBeneficiary);
            sendAlert(getCurrentUser(),beneficiary.getAccountName());
            logger.info("Beneficiary {} has been added", corpQuickBeneficiary.toString());
            logger.info("account number is {}" , corpQuickBeneficiary.getAccountName());
            return messageSource.getMessage("beneficiary.add.success",null,locale);


        }catch (DuplicateObjectException e) {
            //throw new DuplicateObjectException("beneficiary.exist");
            return messageSource.getMessage("beneficiary.exist", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(e.getMessage());
        }

    }

//    @Override
//    public String deleteCorpLocalBeneficiary(Long beneficiaryId) {
//        try {
//            this.corpLocalBeneficiaryRepo.deleteById(beneficiaryId);
//            logger.info("Beneficiary with Id {} deleted", beneficiaryId);
//            return messageSource.getMessage("beneficiary.delete.success",null,locale);
//        } catch (Exception e) {
//            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
//        }
//    }

    @Override
    public CorpQuickBeneficiary getCorpQuickBeneficiary(Long id) {
        return corpQuickBeneficiaryRepo.findById(id).get();
    }



    @Override
    public Iterable<CorpQuickBeneficiary> getCorpQuickBeneficiaries() {
        return corpQuickBeneficiaryRepo.findByCorporate(getCurrentUser().getCorporate());

    }

    @Async
    void sendAlert(User user, String beneficiary) {
        try {
            if (true) {
                String preference = user.getAlertPreference().getCode();
                String customerName = user.getFirstName()+" "+user.getLastName();
                String smsMessage = String.format(messageSource.getMessage("beneficiary.alert.message", null, locale),customerName,beneficiary);

                String alertSubject = messageSource.getMessage("beneficiary.alert.subject", null, locale);
                if ("SMS".equalsIgnoreCase(preference)) {
                    integrationService.sendSMS(smsMessage,user.getPhoneNumber(),  alertSubject);

                } else if ("EMAIL".equalsIgnoreCase(preference)) {
                    sendMail(user,alertSubject,beneficiary);

                } else if ("BOTH".equalsIgnoreCase(preference)) {

                    integrationService.sendSMS(smsMessage,user.getPhoneNumber(),  alertSubject);
                    sendMail(user,alertSubject,beneficiary);
                }

            }
        } catch (Exception e) {
            logger.error("Error occurred", e);
        }

    }

    private void sendMail(User user, String subject, String beneficiary){
        String fullName = user.getFirstName()+" "+user.getLastName();
        Context context = new Context();
        context.setVariable("fullName",fullName);
        context.setVariable("beneficiaryName",beneficiary);
        String mailTemplate="";
       try {

           if (user.getEmailTemplate().equals("mail/beneficiaryMobile.html")) {
               mailTemplate = user.getEmailTemplate();
               subject = messageSource.getMessage("beneficiary.alert.subject.mobile", null, locale);
           }
       }catch(NullPointerException e){
           logger.info("temp not found");
           mailTemplate="mail/beneficiary.html";
       }

        Email email = new Email.Builder().setRecipient(user.getEmail())
                .setSubject(subject)
                .setTemplate(mailTemplate)
                .build();
        mailService.sendMail(email,context);

    }

//    @Override
//    public List<CorpLocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries) {
//        List<CorpLocalBeneficiaryDTO> corplocalBeneficiaryDTOList = new ArrayList<>();
//        for(CorpLocalBeneficiary localBeneficiary: corpLocalBeneficiaries){
//            CorpLocalBeneficiaryDTO benDTO = convertEntityToDTO(localBeneficiary);
//            corplocalBeneficiaryDTOList.add(benDTO);
//        }
//        return corplocalBeneficiaryDTOList;
//    }

    @Override
    public CorpLocalBeneficiaryDTO convertEntityToDTO(CorpQuickBeneficiary corpQuickBeneficiary) {

        CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO = new CorpLocalBeneficiaryDTO();
        corpLocalBeneficiaryDTO.setAccountName(corpQuickBeneficiary.getAccountName());
        corpLocalBeneficiaryDTO.setAccountNumber(corpQuickBeneficiary.getAccountNumber());
        corpLocalBeneficiaryDTO.setBeneficiaryBank(corpQuickBeneficiary.getBeneficiaryBank());
        corpLocalBeneficiaryDTO.setPreferredName(corpQuickBeneficiary.getPreferredName());
        corpLocalBeneficiaryDTO.setFirstname(corpQuickBeneficiary.getOthernames());
        corpLocalBeneficiaryDTO.setLastname(corpQuickBeneficiary.getLastname());

        return  modelMapper.map(corpQuickBeneficiary,CorpLocalBeneficiaryDTO.class);

    }

    @Override
    public CorpQuickBeneficiary convertDTOToEntity(CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO) {
        CorpQuickBeneficiary corpQuickBeneficiary=new CorpQuickBeneficiary();
        corpQuickBeneficiary.setAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpQuickBeneficiary.setAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());
        corpQuickBeneficiary.setBeneficiaryBank(corpLocalBeneficiaryDTO.getBeneficiaryBank());
        corpQuickBeneficiary.setPreferredName(corpLocalBeneficiaryDTO.getPreferredName());
        corpQuickBeneficiary.setOthernames(corpLocalBeneficiaryDTO.getFirstname());
        corpQuickBeneficiary.setLastname(corpLocalBeneficiaryDTO.getLastname());

        return  corpQuickBeneficiary;

    }

    private void validateBeneficiary(CorpLocalBeneficiaryDTO beneficiary) {

        if (corpQuickBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(getCurrentUser().getCorporate().getId(),beneficiary.getAccountNumber()))
            throw new DuplicateObjectException("beneficiary.exist");
/*
        if (financialInstitutionRepo.findByInstitutionCode(beneficiary.getBeneficiaryBank())==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

        if (integrationService.doNameEnquiry(localBeneficiary.getBeneficiaryBank(), localBeneficiary.getAccountNumber()).getAccountName()==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

    */
    }

    private CorporateUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (CorporateUser)userPrincipal.getUser();
        }

        return (null) ;


    }

}
