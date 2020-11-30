package longbridge.services.implementations;

import longbridge.dtos.NeftBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.NeftBeneficiaryRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import longbridge.services.NeftBeneficiaryService;
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
public class NeftBeneficiarySeriviceImpl implements NeftBeneficiaryService {

    @Autowired
    private NeftBeneficiaryRepo repo;

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
    public String addNeftBeneficiary(NeftBeneficiaryDTO beneficiary) {
        try {
            NeftBeneficiary localBeneficiary = convertDTOToEntity(beneficiary);
            localBeneficiary.setUser(getCurrentUser());
            validateBeneficiary(localBeneficiary);
            repo.save(localBeneficiary);
            logger.debug("Beneficiary {} has been added", localBeneficiary.toString());
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
    public String addNeftBeneficiaryMobileApi(NeftBeneficiaryDTO beneficiary) {
        return null;
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
    public NeftBeneficiary getNeftBeneficiary(Long id) {
        return repo.getOne(id);
    }

    @Override
    public Iterable<NeftBeneficiary> getNeftBeneficiaries() {
        return repo.findByUser(getCurrentUser());
    }

    @Override
    public Iterable<NeftBeneficiary> getCorpNeftBeneficiaries() {
        return repo.findByUser(getCorporateUser());
    }

    @Override
    public Iterable<NeftBeneficiary> getBankBeneficiaries() {
        return null;
    }

    @Override
    public boolean doesBeneficiaryExist(RetailUser user, NeftBeneficiaryDTO beneficiaryDTO) {
        return repo.existsByUser_IdAndBeneficiaryAccountNumber(user.getId(), beneficiaryDTO.getBeneficiaryAccountNumber());
    }

    @Override
    public List<NeftBeneficiaryDTO> convertEntitiesToDTOs(Iterable<NeftBeneficiary> neftBeneficiaries) {
        List<NeftBeneficiaryDTO> entities = new ArrayList<>();
        for(NeftBeneficiary beneficiary: neftBeneficiaries){
            NeftBeneficiaryDTO neftBeneficiaryDTO = convertEntityToDTO(beneficiary);
            entities.add(neftBeneficiaryDTO);
        }
        return entities;
    }

    @Override
    public NeftBeneficiaryDTO convertEntityToDTO(NeftBeneficiary neftBeneficiary) {
        return modelMapper.map(neftBeneficiary, NeftBeneficiaryDTO.class);
    }

    @Override
    public NeftBeneficiary convertDTOToEntity(NeftBeneficiaryDTO neftBeneficiaryDTO) {
        return modelMapper.map(neftBeneficiaryDTO, NeftBeneficiary.class);
    }

    private void validateBeneficiary(NeftBeneficiary beneficiary){
        if(repo.existsByUser_IdAndBeneficiaryAccountNumber(getCurrentUser().getId(), beneficiary.getBeneficiaryAccountNumber())){
            throw new DuplicateObjectException("Beneficiary already exists");
        }
    }

    public RetailUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("auh {}",authentication);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (RetailUser)userPrincipal.getUser();
        }

        return (null) ;
    }

    private CorporateUser getCorporateUser(){

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
