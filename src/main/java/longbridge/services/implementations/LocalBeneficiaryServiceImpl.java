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
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Date;
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
    public String addLocalBeneficiary(RetailUser user, LocalBeneficiaryDTO beneficiary) throws InternetBankingException {

        try {
            LocalBeneficiary localBeneficiary = convertDTOToEntity(beneficiary);
            localBeneficiary.setUser(user);
            validateBeneficiary(localBeneficiary, user);
            localBeneficiaryRepo.save(localBeneficiary);
            logger.trace("Beneficiary {} has been added", localBeneficiary.toString());
            sendAlert(user,beneficiary.getAccountName());
            return messageSource.getMessage("beneficiary.add.success", null, locale);
        } catch (Exception e) {
            //throw new InternetBankingException(messageSource.getMessage("beneficiary.add.failure",null, locale), e);
            e.printStackTrace();
            throw new InternetBankingException(e.getMessage(),e);
        }


    }

    @Override
    public String deleteLocalBeneficiary(Long beneficiaryId) {

        try {
            this.localBeneficiaryRepo.delete(beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }


    }

    @Override
    public LocalBeneficiary getLocalBeneficiary(Long id) {
        return localBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<LocalBeneficiary> getLocalBeneficiaries(RetailUser user) {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIsNotNull(user);
    }

    @Override
    public Iterable<LocalBeneficiary> getBankBeneficiaries(RetailUser user) {
        return localBeneficiaryRepo.findByUserAndBeneficiaryBankIgnoreCase(user,bankCode );
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

    private void validateBeneficiary(LocalBeneficiary localBeneficiary, User user) {
        if (localBeneficiaryRepo.findByUser_IdAndAccountNumber(user.getId(), localBeneficiary.getAccountNumber()) != null)
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
    private  void sendAlert(User user ,String beneficiary) {
        try {
             if (true) {
                String preference = user.getAlertPreference().getCode();


                 String customerName = user.getFirstName()+" "+user.getLastName();


                 String smsMessage = String.format(messageSource.getMessage("beneficiary.alert.message", null, locale),customerName,beneficiary);

                String alertSubject = String.format(messageSource.getMessage("beneficiary.alert.subject", null, locale));
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
            logger.error("EXCEPTION OCCURRED {}", e);
        }

    }

    private void sendMail(User user, String subject, String beneficiary){

        String fullName = user.getFirstName()+" "+user.getLastName();
        Context context = new Context();
        context.setVariable("fullName",fullName);
        context.setVariable("beneficiaryName",beneficiary);

        Email email = new Email.Builder().setRecipient(user.getEmail())
                .setSubject(subject)
                .setTemplate("mail/beneficiary.html")
                .build();
        mailService.sendMail(email,context);
    }

}
