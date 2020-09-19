package longbridge.services.implementations;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.CorpInternationalBeneficiaryRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CorpInternationalBeneficiaryService;
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
 * Created by SYLVESTER on 5/22/2017.
 */
@Service
public class CorpInternationalBeneficiaryServiceImpl implements CorpInternationalBeneficiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CorpInternationalBeneficiaryRepo corpInternationalBeneficiaryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MailService mailService;

    @Autowired
    private IntegrationService integrationService;

    private  final Locale locale = LocaleContextHolder.getLocale();

    public CorpInternationalBeneficiaryServiceImpl(CorpInternationalBeneficiaryRepo corpInternationalBeneficiaryRepo) {
        this.corpInternationalBeneficiaryRepo = corpInternationalBeneficiaryRepo;
    }

    @Override
    public String addCorpInternationalBeneficiary(CorpInternationalBeneficiaryDTO beneficiary) throws InternetBankingException {
        CorpInterBeneficiary corpInterBeneficiary = convertDTOToEntity(beneficiary);
        corpInterBeneficiary.setCorporate(getCurrentUser().getCorporate());
        validateBeneficiary(corpInterBeneficiary,getCurrentUser().getCorporate());
        this.corpInternationalBeneficiaryRepo.save(corpInterBeneficiary);
        sendAlert(getCurrentUser(),beneficiary.getAccountName());
        logger.info("CorpInternational beneficiary has been added for corporate {}", getCurrentUser().getCorporate().getName());
        return messageSource.getMessage("beneficiary.add.success",null,locale);
    }

    @Override
    public String deleteCorpInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException {
        try {
            corpInternationalBeneficiaryRepo.delete(beneficiaryId);
            logger.info("Deleted beneficiary with Id {}", beneficiaryId);
            return messageSource.getMessage("beneficiary.delete.success",null,locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("beneficiary.delete.failure", null, locale), e);
        }
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

        Email email = new Email.Builder().setRecipient(user.getEmail())
                .setSubject(subject)
                .setTemplate("mail/beneficiary.html")
                .build();
        mailService.sendMail(email,context);

    }

    @Override
    public CorpInternationalBeneficiaryDTO getCorpInternationalBeneficiary(Long id) {
        return convertEntityToDTO(corpInternationalBeneficiaryRepo.findOneById(id));
    }

    @Override
    public Iterable<CorpInterBeneficiary> getCorpInternationalBeneficiaries() {
        return corpInternationalBeneficiaryRepo.findByCorporate(getCurrentUser().getCorporate());
    }

    @Override
    public List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInterBeneficiary> corpInternationalBeneficiaries) {
        List<CorpInternationalBeneficiaryDTO> corpInternationalBeneficiaryDTOList = new ArrayList<>();
        for(CorpInterBeneficiary internationalBeneficiary: corpInternationalBeneficiaries){
            CorpInternationalBeneficiaryDTO benDTO = convertEntityToDTO(internationalBeneficiary);
            corpInternationalBeneficiaryDTOList.add(benDTO);
        }
        return corpInternationalBeneficiaryDTOList;
    }

    @Override
    public CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInterBeneficiary corpInterBeneficiary) {
        CorpInternationalBeneficiaryDTO corpInternationalBeneficiaryDTO = new CorpInternationalBeneficiaryDTO();
        corpInternationalBeneficiaryDTO.setAccountName(corpInterBeneficiary.getAccountName());
        corpInternationalBeneficiaryDTO.setAccountNumber(corpInterBeneficiary.getAccountNumber());
        corpInternationalBeneficiaryDTO.setBeneficiaryBank(corpInterBeneficiary.getBeneficiaryBank());
        corpInternationalBeneficiaryDTO.setSwiftCode(corpInterBeneficiary.getSwiftCode());
        corpInternationalBeneficiaryDTO.setSortCode(corpInterBeneficiary.getSortCode());
        corpInternationalBeneficiaryDTO.setBeneficiaryAddress(corpInterBeneficiary.getBeneficiaryAddress());
        corpInternationalBeneficiaryDTO.setIntermediaryBankAcctNo(corpInterBeneficiary.getIntermediaryBankAcctNo());
        corpInternationalBeneficiaryDTO.setIntermediaryBankName(corpInterBeneficiary.getIntermediaryBankName());
        return  modelMapper.map(corpInterBeneficiary,CorpInternationalBeneficiaryDTO.class);
    }

    @Override
    public CorpInterBeneficiary convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO) {
        return  modelMapper.map(internationalBeneficiaryDTO,CorpInterBeneficiary.class);
    }

    public void validateBeneficiary(CorpInterBeneficiary corpInterBeneficiary, Corporate corporate) {
        if (corpInternationalBeneficiaryRepo.findByCorporate_IdAndAccountNumber(corporate.getId(), corpInterBeneficiary.getAccountNumber()) != null)
            throw new DuplicateObjectException(messageSource.getMessage("beneficiary.exist",null,locale));

    }

    private CorporateUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (CorporateUser)userPrincipal.getUser();
        }

        return null ;


    }
}
