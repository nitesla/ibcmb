package longbridge.services.implementations;

import longbridge.dtos.CorpNeftBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CorpNeftBeneficiaryRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorpNeftBeneficiaryService;
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

@Service
public class CorpNeftBeneficiaryServiceImpl implements CorpNeftBeneficiaryService {

    private final CorpNeftBeneficiaryRepo corpNeftBeneficiaryRepo;
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

    public CorpNeftBeneficiaryServiceImpl(CorpNeftBeneficiaryRepo corpNeftBeneficiaryRepo) {
        this.corpNeftBeneficiaryRepo = corpNeftBeneficiaryRepo;
    }

    @Override
    public String addCorpNeftBeneficiary(CorpNeftBeneficiaryDTO beneficiary) {

        try{
            validateBeneficiary(beneficiary);
            CorpNeftBeneficiary corpNeftBeneficiary=convertDTOToEntity(beneficiary);

            Corporate corporate= getCurrentUser().getCorporate();
            corpNeftBeneficiary.setUser(corporate);
            this.corpNeftBeneficiaryRepo.save(corpNeftBeneficiary);
            sendAlert(getCurrentUser(),beneficiary.getBeneficiaryAccountName());
            logger.info("Beneficiary {} has been added", corpNeftBeneficiary.getBeneficiaryAccountName());
            logger.info("account number is {}" , corpNeftBeneficiary.getBeneficiaryAccountNumber());
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
    public String deleteCorpNeftBeneficiary(Long beneficiaryId) {
        try {
            this.corpNeftBeneficiaryRepo.deleteById(beneficiaryId);;
            logger.info("Beneficiary with Id {} deleted", beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success",null,locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }
    }

    @Override
    public CorpNeftBeneficiary getCorpNeftBeneficiary(Long id) {
        return corpNeftBeneficiaryRepo.getOne(id);
    }

    @Override
    public Iterable<CorpNeftBeneficiary> getCorpNeftBeneficiaries() {
        return corpNeftBeneficiaryRepo.findByUser(getCurrentUser().getCorporate());
    }

    @Override
    public List<CorpNeftBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpNeftBeneficiary> corpNeftBeneficiaries) {
        List<CorpNeftBeneficiaryDTO> corplocalBeneficiaryDTOList = new ArrayList<>();
        for(CorpNeftBeneficiary neftBeneficiary: corpNeftBeneficiaries){
            CorpNeftBeneficiaryDTO benDTO = convertEntityToDTO(neftBeneficiary);
            corplocalBeneficiaryDTOList.add(benDTO);
        }
        return corplocalBeneficiaryDTOList;
    }

    @Override
    public CorpNeftBeneficiaryDTO convertEntityToDTO(CorpNeftBeneficiary corpNeftBeneficiary) {
        return  modelMapper.map(corpNeftBeneficiary,CorpNeftBeneficiaryDTO.class);
    }

    @Override
    public CorpNeftBeneficiary convertDTOToEntity(CorpNeftBeneficiaryDTO corpNeftBeneficiaryDTO) {
        return  modelMapper.map(corpNeftBeneficiaryDTO,CorpNeftBeneficiary.class);
    }

    @Async
    private  void sendAlert(User user , String beneficiary) {
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

    private void validateBeneficiary(CorpNeftBeneficiaryDTO beneficiary) {

        if (corpNeftBeneficiaryRepo.existsByUser_IdAndBeneficiaryAccountNumber(getCurrentUser().getCorporate().getId(),beneficiary.getBeneficiaryAccountNumber()))
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
