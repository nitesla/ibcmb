package longbridge.services.implementations;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.Email;
import longbridge.models.QuickBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.QuickBeneficiaryRepo;
import longbridge.repositories.QuicktellerBankCodeRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import longbridge.services.QuickBeneficiaryService;
import longbridge.services.SettingsService;
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

import java.util.Locale;

@Service
public class QuickBeneficiaryServiceImpl implements QuickBeneficiaryService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private QuicktellerBankCodeRepo quicktellerBankCodeRepo;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SettingsService configService;
    private final Locale locale = LocaleContextHolder.getLocale();
    private final QuickBeneficiaryRepo quickBeneficiaryRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${bank.code}")
    private String bankCode;


    @Autowired
    public QuickBeneficiaryServiceImpl(QuickBeneficiaryRepo quickBeneficiaryRepo) {
        this.quickBeneficiaryRepo = quickBeneficiaryRepo;
    }

    @Override
    public String addQuickBeneficiary(LocalBeneficiaryDTO beneficiary) throws InternetBankingException {

        try {
            QuickBeneficiary quickBeneficiary = convertDTOToEntity(beneficiary);
            quickBeneficiary.setUser(getCurrentUser());
            validateBeneficiary(quickBeneficiary);
            quickBeneficiaryRepo.save(quickBeneficiary);
            logger.debug("Beneficiary {} has been added", quickBeneficiary.toString());
//            User user=getCurrentUser();
//            user.setEmailTemplate("mail/beneficiary.html");
//            sendAlert(user,beneficiary.getAccountName());
            return messageSource.getMessage("beneficiary.add.success", null, locale);
        } catch (DuplicateObjectException e) {
            //throw new DuplicateObjectException("beneficiary.exist");
            return messageSource.getMessage("beneficiary.exist", null, locale);
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.add.failure",null, locale), e);
        }


    }

    /*@Override
    public String deleteLocalBeneficiary(Long beneficiaryId) {

        try {
            this.localBeneficiaryRepo.deleteById(beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }


    }*/

    @Override
    public QuickBeneficiary getQuickBeneficiary(Long id) {
        return quickBeneficiaryRepo.findById(id).get();
    }


    @Override
    public Iterable<QuickBeneficiary> getQuickBeneficiaries() {
        return quickBeneficiaryRepo.findByUserAndBeneficiaryBankIsNotNull(getCurrentUser());
    }


   /* @Override
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
    }*/

    @Override
    public LocalBeneficiaryDTO convertEntityToDTO(QuickBeneficiary quickBeneficiary) {
        LocalBeneficiaryDTO localBeneficiaryDTO = new LocalBeneficiaryDTO();
        localBeneficiaryDTO.setAccountName(quickBeneficiary.getAccountName());
        localBeneficiaryDTO.setAccountNumber(quickBeneficiary.getAccountNumber());
        localBeneficiaryDTO.setBeneficiaryBank(quickBeneficiary.getBeneficiaryBank());
        localBeneficiaryDTO.setPreferredName(quickBeneficiary.getPreferredName());
        localBeneficiaryDTO.setFirstname(quickBeneficiary.getOthernames());
        localBeneficiaryDTO.setLastname(quickBeneficiary.getLastname());
        return modelMapper.map(quickBeneficiary, LocalBeneficiaryDTO.class);
    }

    @Override
    public QuickBeneficiary convertDTOToEntity(LocalBeneficiaryDTO localBeneficiaryDTO) {
        logger.info("Quick Beneficiary: {}", localBeneficiaryDTO);
        QuickBeneficiary quickBeneficiary = new QuickBeneficiary();
        quickBeneficiary.setAccountName(localBeneficiaryDTO.getAccountName());
        quickBeneficiary.setAccountNumber(localBeneficiaryDTO.getAccountNumber());
        quickBeneficiary.setBeneficiaryBank(localBeneficiaryDTO.getBeneficiaryBank());
        quickBeneficiary.setPreferredName(localBeneficiaryDTO.getPreferredName());
        quickBeneficiary.setOthernames(localBeneficiaryDTO.getFirstname());
        quickBeneficiary.setLastname(localBeneficiaryDTO.getLastname());
        return modelMapper.map(localBeneficiaryDTO, QuickBeneficiary.class);
    }

    private void validateBeneficiary(QuickBeneficiary quickBeneficiary) {
        if (quickBeneficiaryRepo.findByUser_IdAndAccountNumber(getCurrentUser().getId(), quickBeneficiary.getAccountNumber()) != null)
            throw new DuplicateObjectException("beneficiary.exist");

//        if (quicktellerBankCodeRepo.findByBankCode(quickBeneficiary.getBeneficiaryBank())==null)
//            logger.info("Beneficiary bank: {}", quickBeneficiary.getBeneficiaryBank());
//            throw new InternetBankingException("transfer.beneficiary.invalid");
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
