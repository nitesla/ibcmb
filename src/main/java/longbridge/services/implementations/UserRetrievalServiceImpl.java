package longbridge.services.implementations;


import longbridge.dtos.UserRetrievalDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferExceptions;
import longbridge.models.*;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class UserRetrievalServiceImpl implements UserRetrievalService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    AccountService accountService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    MailService mailService;
    @Autowired
    SecurityService securityService;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    CorporateService corporateService;
    @Autowired
    PasswordPolicyService passwordPolicyService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    RetailUserRepo retailUserRepo;
    @Autowired
    CorporateUserRepo corporateUserRepo;

    @Override
    public String userNameRetrieval(UserRetrievalDTO userRetrievalDTO) {

        try {
            Map<String, List<String>> qa =  null;
            Account account = accountService.getAccountByAccountNumber(userRetrievalDTO.getAccountNum());
            if(account != null) {
                String customerId = account.getCustomerId();

                RetailUser user = retailUserService.getUserByCustomerId(customerId);
                logger.info("retail user about getting its username {} ", user.getCustomerId());


                //confirm security question is correct
                    qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());
                    List<String> answers = qa.get("answers");
                List<String> providedAnswers = userRetrievalDTO.getSecQesAns();
                    String result = StringUtil.compareAnswers(providedAnswers, answers);

                    if (result.equalsIgnoreCase("true")) {
                        logger.debug("User Info {}:", user.getUserName());

                        String fullName = user.getFirstName() + " " + user.getLastName();

                        Context context = new Context();
                        context.setVariable("fullName", fullName);
                        context.setVariable("username", user.getUserName());

                        Email email = new Email.Builder()
                                .setRecipient(user.getEmail())
                                .setSubject(messageSource.getMessage("retrieve.username.subject", null, locale))
                                .setTemplate("mail/usernameretrieval")
                                .build();
                        mailService.sendMail(email, context);
                        return "true";
                    }

            }

        }catch (InternetBankingException e){
          logger.info("User name Retrieval error {} ", e);
        }

        return "false";
    }

    @Override
    public String corporateUserNameRetrieval(UserRetrievalDTO userRetrievalDTO) {

        try {
            logger.info("email {} ",userRetrievalDTO.getUserEmail());

            Map<String, List<String>> qa =  null;
            CorporateUser corporateUser = corporateUserService.getUserByEmail(userRetrievalDTO.getUserEmail());
            logger.info("corporate user about getting its username {} ", corporateUser);
            //            Corporate corporate = corporateService.getCorporateByCorporateId(userRetrievalDTO.getCorpID());

            if(corporateUser != null) {

                logger.info("corporate user about getting its username {} ", corporateUser);


                //confirm security question is correct
                qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
                List<String> answers = qa.get("answers");
                List<String> providedAnswers = userRetrievalDTO.getSecQesAns();
                String result = StringUtil.compareAnswers(providedAnswers, answers);

                if (result.equalsIgnoreCase("true")){
                    logger.debug("User Info {}:",userRetrievalDTO.getUserEmail() );


                    Context context = new Context();
                    context.setVariable("fullName", corporateUser.getFirstName()+" "+corporateUser.getLastName());
                    context.setVariable("username", corporateUser.getUserName());

                    Email email = new Email.Builder()
                            .setRecipient(corporateUser.getEmail())
                            .setSubject(messageSource.getMessage("retrieve.username.subject",null,locale))
                            .setTemplate("mail/usernameretrieval")
                            .build();
                    mailService.sendMail(email,context);
                    return "true";
                }

            }

        }catch (InternetBankingException e){
            logger.info("User name Retrieval error {} ", e);
        }

        return "false";
    }

    @Override
    public String confirmSecurityQuestion(UserRetrievalDTO userRetrievalDTO) {

        try {
            logger.info("user name {} ",userRetrievalDTO.getUserName());

            Map<String, List<String>> qa =  null;
            RetailUser retailUser = retailUserService.getUserByName(userRetrievalDTO.getUserName());
            logger.info("retail user about getting its password {} ", retailUser.getCustomerId());
            //            Corporate corporate = corporateService.getCorporateByCorporateId(userRetrievalDTO.getCorpID());

            if(retailUser != null) {

                //confirm security question is correct
                qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
                List<String> answers = qa.get("answers");
                List<String> providedAnswers = userRetrievalDTO.getSecQesAns();
                String result = StringUtil.compareAnswers(providedAnswers, answers);

                if (result.equalsIgnoreCase("true")){
                    return "true";
                }

            }

        }catch (InternetBankingException e){
            logger.error("Error validating security questions and answers",e);
            return messageSource.getMessage("sec.ans.failed", null, locale);
        }

        return "false";
    }

    @Override
    public String sendGeneratedPassword(UserRetrievalDTO userRetrievalDTO) {

    try{

        RetailUser retailUser = retailUserService.getUserByName(userRetrievalDTO.getUserName());
        String tempPassword = passwordPolicyService.generatePassword();
        retailUser.setTempPassword(passwordEncoder.encode(tempPassword));
        String fullName = retailUser.getFirstName() + " " + retailUser.getLastName();
        retailUserRepo.save(retailUser);
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("resetCode",tempPassword);
        logger.info("reset tem password {} ",tempPassword);

        Email email = new Email.Builder()
                .setRecipient(retailUser.getEmail())
                .setSubject(messageSource.getMessage("reset.password.subject", null, locale))
                .setTemplate("mail/forgotpassword")
                .build();
        mailService.sendMail(email,context);
        return "true";

    }catch (MailException me) {
        logger.error("Mail error occurred", me);
        return messageSource.getMessage("mail.failure", null, locale);
    } catch (Exception e){
        logger.error("Error occurred", e);
        return messageSource.getMessage("reset.send.password.failure", null, locale);
    }

    }


    @Override
    public String retrievePassword(UserRetrievalDTO userRetrievalDTO) {
        String tempPassword;

        try {

            RetailUser retailUser = retailUserService.getUserByName(userRetrievalDTO.getUserName());
            if (retailUser !=null){
             tempPassword = retailUser.getPassword();
                return tempPassword;
            }
            else{
                CorporateUser corporateUser = corporateUserService.getUserByName(userRetrievalDTO.getUserName());
                if (corporateUser !=null){
                    tempPassword = corporateUser.getPassword();
                    return tempPassword;
                }

            }
            return null;

        } catch (Exception e) {
            logger.error("Error occurred", e);
            return messageSource.getMessage("password.failure", null, locale);
        }
    }



    @Override
    public String verifyGeneratedPassword(UserRetrievalDTO userRetrievalDTO) {

        try{
            String genpassword = userRetrievalDTO.getGeneratedPassword();
            RetailUser retailUser = retailUserService.getUserByName(userRetrievalDTO.getUserName());
            if (retailUser !=null) {
                boolean match = passwordEncoder.matches(genpassword, retailUser.getTempPassword());
                if (match) {
                    return "true";
                }
                return messageSource.getMessage("reset.password.gpv.failed", null, locale);
            }
            else
                return "Invalid User";

        }catch (InternetBankingException me) {
            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
        }
    }

    @Override
    public String validateToken(UserRetrievalDTO userRetrievalDTO) {

        try{
            String username = userRetrievalDTO.getUserName();
            String token = userRetrievalDTO.getToken();
            logger.info("user token {} ", token);

            RetailUser retailUser = retailUserService.getUserByName(username);
            if (!token.isEmpty() && token !=null) {
                boolean message = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (message) {
                    return "true";
                }
            }
            return messageSource.getMessage("token.auth.failed", null, locale);
        }catch (InternetBankingException e){
            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
            return e.getMessage();
        }
    }

    @Override
    public String verifyPasswordPolicy(UserRetrievalDTO userRetrievalDTO) {

        String password = userRetrievalDTO.getConfirmPassword();
        String username = userRetrievalDTO.getUserName();

        try{
            RetailUser user = retailUserService.getUserByName(username);
            if (user !=null){
                String message = passwordPolicyService.validate(password, user);
                if (!"".equals(message)){
                    return message;
                }
                return "true";
            }
            else
                return "Invalid User";
        }catch (InternetBankingException me) {
            throw  new InternetBankingException();

        }
    }


    @Override
    public String sendCorpGeneratedPassword(UserRetrievalDTO userRetrievalDTO) {
        String username = userRetrievalDTO.getUserName();
        try{

            CorporateUser corporateUser = corporateUserService.getUserByName(username);
            String tempPassword = passwordPolicyService.generatePassword();
            corporateUser.setTempPassword(passwordEncoder.encode(tempPassword));
            logger.info("temp password {} ", tempPassword);
            String fullName = corporateUser.getFirstName() + " " + corporateUser.getLastName();
            corporateUserRepo.save(corporateUser);

            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("resetCode",tempPassword);

            Email email = new Email.Builder()
                    .setRecipient(corporateUser.getEmail())
                    .setSubject(messageSource.getMessage("reset.password.subject", null, locale))
                    .setTemplate("mail/forgotpassword")
                    .build();
            mailService.sendMail(email,context);
            return "true";

        }catch (MailException me) {
            logger.error("Mail error occurred", me);
            return messageSource.getMessage("mail.failure", null, locale);
        } catch (Exception e){
            logger.error("Error occurred", e);
            return messageSource.getMessage("reset.send.password.failure", null, locale);
        }

    }


    @Override
    public String verifyCorpGeneratedPassword(UserRetrievalDTO userRetrievalDTO) {

        try{
            String genpassword = userRetrievalDTO.getGeneratedPassword();
            CorporateUser corporateUser = corporateUserService.getUserByName(userRetrievalDTO.getUserName());
            if (corporateUser !=null) {
                boolean match = passwordEncoder.matches(genpassword, corporateUser.getTempPassword());
                if (match) {
                    return "true";
                }
                return messageSource.getMessage("reset.password.gpv.failed", null, locale);
            }
            else
                return "Invalid User";
        }catch (InternetBankingException me) {
            return messageSource.getMessage("reset.password.gpv.failed", null, locale);
        }
    }


    @Override
    public String verifyCorpPasswordPolicy(UserRetrievalDTO userRetrievalDTO) {

        String password = userRetrievalDTO.getConfirmPassword();
        String username = userRetrievalDTO.getUserName();

        try{
            CorporateUser user = corporateUserService.getUserByName(username);
            if (user !=null){
            String message = passwordPolicyService.validate(password, user);
            if (!"".equals(message)){
                return message;
            }
            return "true";
            }
            else
                return "Invalid User";
        }catch (InternetBankingException me) {
            throw  new InternetBankingException();

        }
    }



    @Override
    public String validateCorpToken(UserRetrievalDTO userRetrievalDTO) {

        try{
            String username = userRetrievalDTO.getUserName();
            String token = userRetrievalDTO.getToken();
            CorporateUser corporateUser = corporateUserService.getUserByName(username);
            if (corporateUser !=null) {
                if (!token.isEmpty() && token != null) {

                    boolean message = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
                    if (message) {
                        return "true";
                    }
                }
            }
            else
                return "Invalid user";
            return messageSource.getMessage("token.auth.failed", null, locale);
        }catch (InternetBankingException e){
            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
            return e.getMessage();
        }
    }

    @Override
    public String getBvn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();

            if(currentUser == null){
                throw new InternetBankingTransferException(messageSource.getMessage("user.invalid",null,locale));
            }
            if (currentUser.getCorpId() == null) {

                RetailUser retailUser = retailUserService.getUserByName(currentUser.getUsername());

                if (retailUser.getBvn() == null || "NO BVN".equalsIgnoreCase(retailUser.getBvn()) || retailUser.getBvn().isEmpty()) {

                    throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());
                }

            } else {
                CorporateUser user  = (CorporateUser)currentUser.getUser();
                if(CorpUserType.AUTHORIZER.equals(user.getCorpUserType())){
                    throw new InternetBankingTransferException(messageSource.getMessage("transfer.initiate.disallowed",null,locale));

                }

                Corporate corporate = corporateService.getCorp(currentUser.getCorpId());
                if (corporate == null) throw new InternetBankingTransferException(TransferExceptions.NO_BVN.toString());

                if ((corporate.getRcNumber() == null || corporate.getRcNumber().isEmpty() ) && (corporate.getTaxId()==null ||corporate.getTaxId().isEmpty() )){
                    throw new InternetBankingTransferException(TransferExceptions.NO_RC.toString());
                }

            }


        }
        return  null;
    }


}
