package longbridge.services.implementations;

import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.UserRegDTO;
import longbridge.dtos.apidtos.MobileRegSecQuestionDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Service
public class UserRegServiceImpl implements UserRegService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    IntegrationService integrationService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    ConfigurationService configService;
    @Autowired
    AccountService accountService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    SecurityService securityService;
    @Autowired
    PasswordPolicyService passwordPolicyService;
    @Autowired
    private CodeService codeService;

    @Override
    public String validateDetails(UserRegDTO userRegDTO) {
        String accountNumber =userRegDTO.getAcctNumber();
        String email = userRegDTO.getEmail();
        String birthDate = userRegDTO.getDob();
        String customerId = "";

        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        AccountDetails account;
        logger.info("Customer Details {} ", details);

        String[] accountTypes = null;
        SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
        if (dto != null && dto.isEnabled()) {
            accountTypes = StringUtils.split(dto.getValue(), ",");
        }

        if (details.getCifId() != null && !details.isCorp()) {

            customerId = details.getCifId();
            account = integrationService.viewAccountDetails(accountNumber);
            logger.info("CustomerId {}", customerId);
            logger.info("Account details {}", account);

            if (accountTypes != null && !ArrayUtils.contains(accountTypes, account.getAcctType())) {
                return messageSource.getMessage("account.nontransactional", null, locale);
            }
            else if (account != null && !"A".equalsIgnoreCase(account.getAcctStatus())) {
                return messageSource.getMessage("account.inactive", null, locale);
            }
            else if (account != null && accountService.isAccountRestricted(account)) {
                return messageSource.getMessage("account.restricted", null, locale);
            }
            else {
                return "true";
            }

        } else if (details.getCifId() != null && details.isCorp()) {
            return messageSource.getMessage("not.retail.account", null, locale);
        } else {
            return messageSource.getMessage("not.valid.account", null, locale);
        }

    }


    @Override
    public String validateIfAcctExist(UserRegDTO userRegDTO){

        try{
            String customerId = "";
            String accountNumber = userRegDTO.getAcctNumber();
            String email = userRegDTO.getEmail();
            logger.debug("Email : {}", email);
            String birthDate =userRegDTO.getDob();
            logger.debug("BirthDate : {}", birthDate);
            CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
            logger.debug("Customer details {} ", details);
            if (details.getCifId() != null) {
                RetailUser user = retailUserService.getUserByCustomerId(details.getCifId());
                List<AccountDTO> account = accountService.getAccountsForReg(details.getCifId());
                logger.info("ACCOUNT {} ", account);
                if (user == null && account.isEmpty()) {
                    logger.debug("User {} does not exist on the platform currently, can go on to register",details.getCifId());
                    customerId = details.getCifId();
                } else {
                    logger.warn("User already exists on the system, cannot be allowed to register");
                    customerId = "";
                }

            } else {
                logger.warn("The account details for {} could not be found. The reasons could be that the account is NOT VERIFIED, CLOSED or DELETED",accountNumber);
                //nothing
                customerId = "";
            }
            return customerId;

        }catch (InternetBankingException e){
            logger.info("acct validation error {} ", e);

        }
        return null;

    }

    @Override
    public List<String> getSecQuestionFromNumber(@PathVariable String cifId){
        String secQuestion;
        try{

            RetailUser user = retailUserService.getUserByCustomerId(cifId);
            List<String> question = null;
            if (user != null) {
                logger.info("USER NAME {}", user.getUserName());
                Map<String, List<String>> qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());

                if (qa != null && !qa.isEmpty()) {
                    question = qa.get("questions");
                    logger.info("question {}", question);

                }
            }
            return question;
        }catch (InternetBankingException e){

            return null;
        }


    }

    @Override
    public String getSecAns(UserRegDTO userRegDTO) {


        String secAnswer = "";
        try {
            RetailUser retailUser = retailUserService.getUserByName(userRegDTO.getUserName());
            Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            if (qa != null) {
                List<String> question = qa.get("answers");
                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");

                if (!secAnswer.equalsIgnoreCase(userRegDTO.getSecAns().toString())) {
                    return "";
                } else {
                    return "true";
                }

            } else {
                return "";
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            return "";
        }


    }

    @Override
    public String sendRegCode(UserRegDTO userRegDTO) {

        String email;
        String birthDate;
        String accountNumber;
        String  code ="";
        try{
            email= userRegDTO.getEmail();
            birthDate = userRegDTO.getDob();
            accountNumber =userRegDTO.getAcctNumber();
            CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
            if (details != null) {
                logger.info("Reg Details : " + details);
                String contact = details.getPhone();
                if(contact != null && !contact.equalsIgnoreCase("") ) {
                    code = generateAndSendRegCode(contact);
                    if (!"".equals(code)) {
                        logger.info("reg code {} and time {} saved successfully",code,new Date());
//                        session.setAttribute("regCode", code);
//                        session.setAttribute("regCodeTime", new Date());
                    } else {
                        return code;
                    }
                }else {
                    return "noPhoneNumber";
                }

            } else {
                //nothing
                return code;
            }

            return code;

        }catch (InternetBankingException e){

            return null;
        }

    }

    private String generateAndSendRegCode(String contact) {
        String code = "";
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        logger.info("Reg Code : {}", n);
        String message = "Your Registration Code is : ";
        message += n;


        boolean sent = integrationService.sendRegCodeSMS(message, contact, "Internet Banking Registration Code");
        logger.info("is reg code sent {}",sent);
        if (sent) {
            return String.valueOf(n);
        }
        logger.info("reg code error {} ", code);
        return code;
    }

    @Override
    public String checkRegCode(UserRegDTO userRegDTO){
        Date regCodeDate = new Date();
        logger.info("reg date {} ", regCodeDate);
        String regCode = userRegDTO.getRegCode();
        String code = userRegDTO.getCode();

        try{
            if(regCodeDate ==null) {
                return messageSource.getMessage("regCode.validate.error", null, locale);
            }
            boolean codeValid = DateFormatter.validate(regCodeDate, new Date());
            logger.info("REGCODE IN SESSION {} ", regCode);
//        Integer reg = Integer.parseInt(regCode);
            ;
            String message = messageSource.getMessage("regCode.validate.expired", null, locale);

            if (code.equals(regCode)) {
                if (codeValid) {
                    return "true";
                } else {
                    return message;
                }
            }
            message = messageSource.getMessage("regCode.validate.failed", null, locale);
            logger.info("REGCODE failure Message{} ", message);
            return message;

    }catch (InternetBankingException e){
            logger.info("Validate Reg code error  {} ", e);
            return null;

        }
    }

    @Override
    public  String usernameCheck(UserRegDTO userRegDTO){

        try{

            String username = userRegDTO.getUserName();
            logger.info("Username : {}", username);
            RetailUser user = retailUserService.getUserByName(username);
            logger.info("USER RETURNED{}", user);
            if (null == user) {
                return "true";
            }
            return "false";

    }catch (InternetBankingException e){
            logger.info("username check error {} ", e);
        }
        return "false";
    }


    @Override
    public  String passwordCheck(UserRegDTO userRegDTO){

        String password = userRegDTO.getPassword();
        String message;
        try{

            message = passwordPolicyService.validate(password, null);
            if (!"".equals(message)) {
                return message;
            }

            return "true";

        }catch (InternetBankingException e){
            logger.info("username check error {} ", e);
        }
        return "false";
    }

    @Override
    public  ArrayList[] getSecurityQuestions(int noOfQuestions){
        List<CodeDTO> secQues = codeService.getCodesByType("SECURITY_QUESTION");
        logger.info("secQuestions {} ", secQues.size());

        ArrayList[] masterList = new ArrayList[noOfQuestions];
        int questionsPerSection = (secQues.size() - (secQues.size() % noOfQuestions)) / noOfQuestions;
        int questnPostn = 0;
        for (int i = 0; i < noOfQuestions; i++) {
            masterList[i] = new ArrayList<>();
            for (int j = 0; j < questionsPerSection; j++) {
                masterList[i].add(secQues.get(questnPostn));
                questnPostn++;
            }

        }
        return masterList;
    }
    @Override
    public  ArrayList[] getSecurityQuestionsMobile(int noOfQuestions){
        List<CodeDTO> secQues = codeService.getCodesByType("SECURITY_QUESTION");
        logger.info("secQuestions {} ", secQues.size());

        ArrayList[] masterList = new ArrayList[noOfQuestions];
        int questionsPerSection = (secQues.size() - (secQues.size() % noOfQuestions)) / noOfQuestions;
        int questnPostn = 0;
        for (int i = 0; i < noOfQuestions; i++) {
            masterList[i] = new ArrayList<>();
            for (int j = 0; j < questionsPerSection; j++) {
                MobileRegSecQuestionDTO mobileRegSecQuestionDTO = new MobileRegSecQuestionDTO();
                mobileRegSecQuestionDTO.setCode(secQues.get(questnPostn).getCode());
                mobileRegSecQuestionDTO.setType(secQues.get(questnPostn).getType());
                masterList[i].add(mobileRegSecQuestionDTO);
                questnPostn++;
            }

        }
        return masterList;
    }

    @Override
    public CustomerDetails customerDetails(String accountNumber, String email, String birthDate) {

        try {
            CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
            return  details;
        } catch (InternetBankingException e) {
          return null;
        }
    }

}
