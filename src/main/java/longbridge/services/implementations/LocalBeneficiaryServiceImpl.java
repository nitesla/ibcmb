package longbridge.services.implementations;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.Email;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.FinancialInstitutionRepo;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.services.LocalBeneficiaryService;
import longbridge.services.MailService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class LocalBeneficiaryServiceImpl implements LocalBeneficiaryService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FinancialInstitutionRepo financialInstitutionRepo;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ConfigurationService configService;
    private Locale locale = LocaleContextHolder.getLocale();
    private LocalBeneficiaryRepo localBeneficiaryRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${bank.code}")
    private String bankCode;


    @Autowired
    public LocalBeneficiaryServiceImpl(LocalBeneficiaryRepo localBeneficiaryRepo) {
        this.localBeneficiaryRepo = localBeneficiaryRepo;
    }

    @Override
    public String addLocalBeneficiaryMobileApi(LocalBeneficiaryDTO beneficiary) throws InternetBankingException {

        try {
            LocalBeneficiary localBeneficiary = convertDTOToEntity(beneficiary);
            localBeneficiary.setUser(getCurrentUser());
            validateBeneficiary(localBeneficiary);
            localBeneficiaryRepo.save(localBeneficiary);
            logger.debug("Beneficiary {} has been added", localBeneficiary.toString());
            User user=getCurrentUser();
            user.setEmailTemplate("mail/beneficiaryMobile.html");
            sendAlert(user,beneficiary.getAccountName());
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
    public String addLocalBeneficiary(LocalBeneficiaryDTO beneficiary) throws InternetBankingException {

        try {
            LocalBeneficiary localBeneficiary = convertDTOToEntity(beneficiary);
            localBeneficiary.setUser(getCurrentUser());
            validateBeneficiary(localBeneficiary);
            localBeneficiaryRepo.save(localBeneficiary);
            logger.debug("Beneficiary {} has been added", localBeneficiary.toString());
            User user=getCurrentUser();
            user.setEmailTemplate("mail/beneficiary.html");
            sendAlert(user,beneficiary.getAccountName());
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
    public String deleteLocalBeneficiary(Long beneficiaryId) {

        try {
            this.localBeneficiaryRepo.deleteById(beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }


    }

    @Override
    public LocalBeneficiary getLocalBeneficiary(Long id) {
        return localBeneficiaryRepo.findById(id).get();
    }

    /*@Override
    public Iterable<LocalBeneficiary> getLocalBeneficiaries(RetailUser user) {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIsNotNull(user);
    }
*/
    @Override
    public Iterable<LocalBeneficiary> getLocalBeneficiaries() {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIsNotNull(getCurrentUser());
    }


    @Override
    public Iterable<LocalBeneficiary> getBankBeneficiaries() {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIgnoreCase(getCurrentUser(),bankCode );
    }

    @Override
    public boolean doesBeneficiaryExist(RetailUser user, LocalBeneficiaryDTO beneficiaryDTO) {
        LocalBeneficiary localBeneficiary = localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), beneficiaryDTO.getAccountNumber());
        return localBeneficiary!=null;
    }

    @Override
    public List<LocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<LocalBeneficiary> localBeneficiaries) {
        List<LocalBeneficiaryDTO> localBeneficiaryDTOList = new ArrayList<>();
        for (LocalBeneficiary localBeneficiary : localBeneficiaries) {
            LocalBeneficiaryDTO benDTO = convertEntityToDTO(localBeneficiary);
            localBeneficiaryDTOList.add(benDTO);
        }
        return localBeneficiaryDTOList;
    }

    @Override
    public LocalBeneficiaryDTO convertEntityToDTO(LocalBeneficiary localBeneficiary) {
        LocalBeneficiaryDTO localBeneficiaryDTO = new LocalBeneficiaryDTO();
        localBeneficiaryDTO.setAccountName(localBeneficiary.getAccountName());
        localBeneficiaryDTO.setAccountNumber(localBeneficiary.getAccountNumber());
        localBeneficiaryDTO.setBeneficiaryBank(localBeneficiary.getBeneficiaryBank());
        localBeneficiaryDTO.setPreferredName(localBeneficiary.getPreferredName());
        return modelMapper.map(localBeneficiary, LocalBeneficiaryDTO.class);
    }

    @Override
    public LocalBeneficiary convertDTOToEntity(LocalBeneficiaryDTO localBeneficiaryDTO) {
        return modelMapper.map(localBeneficiaryDTO, LocalBeneficiary.class);
    }

   /* private void validateBeneficiary(LocalBeneficiary localBeneficiary, User user) {
        if (localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), localBeneficiary.getAccountNumber()) != null)
            throw new DuplicateObjectException("beneficiary.exist");

        if (financialInstitutionRepo.findByInstitutionCode(localBeneficiary.getBeneficiaryBank())==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

     *//* if (!bankCode.equalsIgnoreCase(localBeneficiary.getBeneficiaryBank())){
          logger.error("local beneficiary is "+localBeneficiary);
          NEnquiryDetails details=integrationService.doNameEnquiry(localBeneficiary.getBeneficiaryBank(), localBeneficiary.getAccountNumber());
          if (details==null || details.getAccountName()==null )
              throw new InternetBankingException("transfer.beneficiary.invalid");
      }*//*
    }
*/
    private void validateBeneficiary(LocalBeneficiary localBeneficiary) {
        if (localBeneficiaryRepo.findByUser_IdAndAccountNumber(getCurrentUser().getId(), localBeneficiary.getAccountNumber()) != null)
            throw new DuplicateObjectException("beneficiary.exist");

        if (financialInstitutionRepo.findByInstitutionCode(localBeneficiary.getBeneficiaryBank())==null)
            throw new InternetBankingException("transfer.beneficiary.invalid");

     /* if (!bankCode.equalsIgnoreCase(localBeneficiary.getBeneficiaryBank())){
          logger.error("local beneficiary is "+localBeneficiary);
          NEnquiryDetails details=integrationService.doNameEnquiry(localBeneficiary.getBeneficiaryBank(), localBeneficiary.getAccountNumber());
          if (details==null || details.getAccountName()==null )
              throw new InternetBankingException("transfer.beneficiary.invalid");
      }*/
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


                String alertSubject = String.format(messageSource.getMessage(alertSub, null, locale));
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


    public RetailUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("auh {}",authentication);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (RetailUser)userPrincipal.getUser();
        }

        return (null) ;


    }



}
