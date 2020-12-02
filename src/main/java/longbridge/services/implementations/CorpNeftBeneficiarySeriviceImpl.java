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
public class CorpNeftBeneficiarySeriviceImpl implements CorpNeftBeneficiaryService {

    @Autowired
    private CorpNeftBeneficiaryRepo repo;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();

    @Override
    public String addNeftBeneficiary(CorpNeftBeneficiaryDTO beneficiary) {
        try {
            CorpNeftBeneficiary corpNeftBeneficiary = convertDTOToEntity(beneficiary);

            Corporate corporate= getCurrentUser().getCorporate();
            corpNeftBeneficiary.setCorporate(corporate);
            validateBeneficiary(corpNeftBeneficiary);
            repo.save(corpNeftBeneficiary);
            logger.debug("Beneficiary {} has been added", corpNeftBeneficiary.toString());
            User user=getCurrentUser();
            user.setEmailTemplate("mail/beneficiaryMobile.html");
            sendAlert(user,beneficiary.getBeneficiaryAccountName());
            return messageSource.getMessage("beneficiary.add.success", null, locale);
        } catch (DuplicateObjectException e) {
            //throw new DuplicateObjectException("beneficiary.exist");
            return messageSource.getMessage("beneficiary.exist", null, locale);
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.add.failure",null, locale), e);
        }

    }


    @Override
    public String deleteNeftBeneficiary(Long beneficiaryId) {
        try {
            repo.deleteById(beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }
    }

    @Override
    public CorpNeftBeneficiary getNeftBeneficiary(Long id) {
        return repo.findById(id).get();
    }



    @Override
    public Iterable<CorpNeftBeneficiary> getCorpNeftBeneficiaries() {
        return repo.findByCorporate(getCurrentUser().getCorporate());
    }

    @Override
    public List<CorpNeftBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpNeftBeneficiary> neftBeneficiaries) {
        List<CorpNeftBeneficiaryDTO> entities = new ArrayList<>();
        for(CorpNeftBeneficiary beneficiary: neftBeneficiaries){
            CorpNeftBeneficiaryDTO neftBeneficiaryDTO = convertEntityToDTO(beneficiary);
            entities.add(neftBeneficiaryDTO);
        }
        return entities;
    }

    @Override
    public CorpNeftBeneficiaryDTO convertEntityToDTO(CorpNeftBeneficiary neftBeneficiary) {
        return modelMapper.map(neftBeneficiary, CorpNeftBeneficiaryDTO.class);
    }

    @Override
    public CorpNeftBeneficiary convertDTOToEntity(CorpNeftBeneficiaryDTO neftBeneficiaryDTO) {
        return modelMapper.map(neftBeneficiaryDTO, CorpNeftBeneficiary.class);
    }

    private void validateBeneficiary(CorpNeftBeneficiary beneficiary){
        if(repo.existsByCorporate_IdAndBeneficiaryAccountNumber(getCurrentUser().getCorporate().getId(), beneficiary.getBeneficiaryAccountNumber())){
            throw new DuplicateObjectException("Beneficiary already exists");
        }
    }

    private CorporateUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (CorporateUser)userPrincipal.getUser();
        }

        return (null) ;


    }

    @Async
    public  void sendAlert(User user ,String beneficiary) {
        try {
            if (true) {
                String preference = user.getAlertPreference().getCode();
                String customerName = user.getFirstName()+" "+user.getLastName();
                String smsMessage = String.format(messageSource.getMessage("beneficiary.alert.message", null, locale),customerName,beneficiary);

                String alertSub="beneficiary.alert.subject";
                if("mail/beneficiaryMobile.html".equals(user.getEmailTemplate()))alertSub="beneficiary.alert.subject.mobile";


                String alertSubject = messageSource.getMessage(alertSub, null, locale);
                logger.info("alertBen {}",alertSubject);
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

        Email email = new Email.Builder().setRecipient(user.getEmail())
                .setSubject(subject)
                .setTemplate(user.getEmailTemplate())
                .build();
        mailService.sendMail(email,context);

    }
}
