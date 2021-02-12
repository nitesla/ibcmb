package longbridge.services.implementations;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CorpLocalBeneficiaryRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorpLocalBeneficiaryService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Service
public class CorpLocalBeneficiaryServiceImpl implements CorpLocalBeneficiaryService {

    private final CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo;
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
    public CorpLocalBeneficiaryServiceImpl(CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo,CorporateRepo corporateRepo) {
        this.corpLocalBeneficiaryRepo = corpLocalBeneficiaryRepo;
    }



    @Override
    public String addCorpLocalBeneficiary(CorpLocalBeneficiaryDTO beneficiary) {


        try{
            validateBeneficiary(beneficiary);
            CorpLocalBeneficiary corpLocalBeneficiary=convertDTOToEntity(beneficiary);

            Corporate corporate= getCurrentUser().getCorporate();
            corpLocalBeneficiary.setCorporate(corporate);
            this.corpLocalBeneficiaryRepo.save(corpLocalBeneficiary);
            sendAlert(getCurrentUser(),beneficiary.getAccountName());
            logger.info("Beneficiary {} has been added", corpLocalBeneficiary.getAccountName());
            logger.info("account number is {}" , corpLocalBeneficiary.getAccountName());
            return messageSource.getMessage("beneficiary.add.success",null,locale);


        }catch (DuplicateObjectException e) {
            //throw new DuplicateObjectException("beneficiary.exist");
            return messageSource.getMessage("beneficiary.exist", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(e.getMessage());
        }

    }

    @Override
    public String deleteCorpLocalBeneficiary(Long beneficiaryId) {
        try {
            this.corpLocalBeneficiaryRepo.deleteById(beneficiaryId);
            logger.info("Beneficiary with Id {} deleted", beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success",null,locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }
    }

    @Override
    public CorpLocalBeneficiary getCorpLocalBeneficiary(Long id) {
        return corpLocalBeneficiaryRepo.findById(id).get();
    }



    @Override
    public Iterable<CorpLocalBeneficiary> getCorpLocalBeneficiaries() {
        return corpLocalBeneficiaryRepo.findByCorporate(getCurrentUser().getCorporate());

    }

    @Async
    void sendAlert(User user, String beneficiary) {
        try {
            if (true) {
                String preference = user.getAlertPreference();
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

        if (corpLocalBeneficiaryRepo.existsByCorporate_IdAndAccountNumber(getCurrentUser().getCorporate().getId(),beneficiary.getAccountNumber()))
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
