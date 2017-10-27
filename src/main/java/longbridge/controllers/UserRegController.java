package longbridge.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.AccountDetails;
import longbridge.api.CustomerDetails;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.RegistrationForm;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Wunmi Sowunmi on 18/04/2017.
 */
@Controller
public class UserRegController {

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private RetailUserService retailUserService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ConfigurationService configService;

    private Locale locale = LocaleContextHolder.getLocale();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${antiphishingimagepath}")
    private String imagePath;

    @Value("${phishing.image.folder}")
    private String fullImagePath;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @PostMapping("/rest/accountdetails")
    public
    @ResponseBody
    String getAccountDetailsFromNumber(HttpServletRequest webRequest) {

        logger.info("Customer Registration Started.");
        logClientRequestHeaders(webRequest);
        String customerId = "";
        String accountNumber = webRequest.getParameter("accountNumber");
        logger.info("Account number : " + accountNumber);
        String email = webRequest.getParameter("email");
        logger.info("Email : " + email);
        String birthDate = webRequest.getParameter("birthDate");
        logger.info("BirthDate : " + birthDate);
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
            } else if (account != null && !"A".equalsIgnoreCase(account.getAcctStatus())) {
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

    private void logClientRequestHeaders(HttpServletRequest request) {

        Enumeration<String> headers = request.getHeaderNames();
        StringBuilder builder = new StringBuilder();

        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            builder.append(header + " : " + request.getHeader(header) + "\n");
        }

        logger.info(builder.toString());
    }

    @PostMapping("/rest/accountexists")
    public
    @ResponseBody
    String validateExists(WebRequest webRequest) {
        String customerId = "";
        String accountNumber = webRequest.getParameter("accountNumber");
        logger.debug("Account Number : " + accountNumber);
        String email = webRequest.getParameter("email");
        logger.debug("Email : " + email);
        String birthDate = webRequest.getParameter("birthDate");
        logger.debug("BirthDate : " + birthDate);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        logger.debug("Customer details {} ", details);
        if (details.getCifId() != null) {
            RetailUser user = retailUserService.getUserByCustomerId(details.getCifId());
            List<AccountDTO> account = accountService.getAccounts(details.getCifId());
            logger.info("ACCOUNT {} ", account);
            if (user == null && account.isEmpty()) {
                logger.debug("User {} does not exist on the platform currently, can go on to register",details.getCifId());
                customerId = details.getCifId();
            } else {
                logger.warn("User already exists on the system, cannot be allowed to register");
                customerId = "";
            }

        } else {
            //nothing
            customerId = "";
        }
        return customerId;
    }

    @PostMapping("/rest/retail/accountname/accountNumber")
    public
    @ResponseBody
    String getAccountNameFromNumber(WebRequest webRequest) {
        String accountNumber = webRequest.getParameter("accountNumber");
        String customerId = "";
        String userEmail = "";
//    	logger.info("Account nUmber : " + accountNumber);
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account != null) {
            customerId = account.getCustomerId();
//            logger.info("Account number : " + customerId);
        } else {
            //nothing
            customerId = "";
        }
        logger.info("Customer ID is {}", customerId);
        return customerId;
    }

    @GetMapping("/rest/secQues/{cifId}")
    public
    @ResponseBody
    List<String> getSecQuestionFromNumber(@PathVariable String cifId, HttpSession session) {
        String secQuestion = "";
        logger.info("cifId : " + cifId);

        RetailUser user = retailUserService.getUserByCustomerId(cifId);
//        logger.info("USER NAME {}", user);
        List<String> question = null;
        if (user != null) {
            logger.info("USER NAME {}", user.getUserName());
            session.removeAttribute("username");
            session.setAttribute("username", user.getUserName());
            Map<String, List<String>> qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());
            //List<String> sec = null;

            if (qa != null && !qa.isEmpty()) {
                session.setAttribute("retSecQestnAndAnsFU", qa);
//                logger.info("qs {}",qa);
                question = qa.get("questions");
                secQuestion = question.stream().filter(Objects::nonNull).findFirst().orElse("");
                logger.info("question {}", question);

            }
        }

        return question;
    }

    @GetMapping("/rest/getSecQues/{username}")
    public
    @ResponseBody
    String getSecQues(@PathVariable String username) {
        String secQuestion = "";
        logger.debug("Username in Controller : " + username);

        RetailUser user = retailUserService.getUserByName(username);
        if (user != null) {
            logger.info("USER NAME {}", user.getUserName());
            try {
                Map<String, List<String>> qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());
                logger.info("QQQAAAA {}", qa);
                //List<String> sec = null;
                if (qa != null && !qa.isEmpty()) {
                    List<String> question = qa.get("questions");
                    secQuestion = question.stream().filter(Objects::nonNull).findFirst().orElse("");

                }
            } catch (Exception e) {
                logger.info(e.getMessage());
                secQuestion = "";
            }

        } else {
            secQuestion = "";
        }

        return secQuestion;
    }


    @GetMapping("/rest/secAnswer/{answer}/{username}")
    public
    @ResponseBody
    String getSecAns(@PathVariable String answer, @PathVariable String username) {

        //confirm security question is correct
        String secAnswer = "";
        try {
            RetailUser retailUser = retailUserService.getUserByName(username);
            Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
            //List<String> sec = null;
            if (qa != null) {
                List<String> question = qa.get("answers");
                secAnswer = question.stream().filter(Objects::nonNull).findFirst().orElse("");
                logger.info("user answer {}", answer);
                logger.info("answer {}", secAnswer);

                if (!secAnswer.equalsIgnoreCase(answer)) {
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
        //return (String) session.getAttribute("username");
    }

    @GetMapping("/rest/getTokenSerials/{username}")
    public
    @ResponseBody
    String[] getTokenSerials(@PathVariable String username) {
        String no[] = new String[0];
        logger.info("Username in Controller : " + username);

        RetailUser user = retailUserService.getUserByName(username);
        if (user != null) {
            logger.info("USER NAME {}", user.getUserName());
            try {
                String sn = securityService.getTokenSerials(user.getEntrustId(), user.getEntrustGroup());
                logger.info("SERIALS {}", sn);
                //List<String> sec = null;
                if (sn != null && !sn.isEmpty()) {
                    String myList[] = sn.trim().split(",");

                    logger.info("SERIALS {}", myList);
                    return myList;
                } else {
                    return no;
                }

            } catch (Exception e) {
                logger.info(e.getMessage());
            }

        } else {
            return no;
        }

        return no;
    }


    @PostMapping("/rest/regCode")
    public
    @ResponseBody
    String sendRegCode(WebRequest webRequest, HttpSession session) {

        String code = "";
        String accountNumber = webRequest.getParameter("accountNumber");
        logger.info("Account Number : " + accountNumber);
        String email = webRequest.getParameter("email");
        logger.info("Email : " + email);
        String birthDate = webRequest.getParameter("birthDate");
        logger.info("BirthDate : " + birthDate);
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
        if (details != null) {
            logger.info("Reg Code : " + details);
            String contact = details.getPhone();
            if(contact != null && !contact.equalsIgnoreCase("") ) {
                code = generateAndSendRegCode(contact);
                if (!"".equals(code)) {
                    session.setAttribute("regCode", code);
                    session.setAttribute("regCodeTime", new Date());
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
    }

    private String generateAndSendRegCode(String contact) {
        String code = "";
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        logger.info("Reg Code : " + n);
        String message = "Your Registration Code is : ";
        message += n;


        CompletableFuture<ObjectNode> sent = integrationService.sendSMS(message, contact, "Internet Banking Registration Code");
        if (sent != null) {
            return String.valueOf(n);
        }
        return code;
    }

    @PostMapping("/rest/regCode/check")
    public
    @ResponseBody
    String checkRegCode(WebRequest webRequest, HttpSession session) {
        String code = webRequest.getParameter("code");
        logger.info("Code : " + code);
        String regCode = (String) session.getAttribute("regCode");
        Date regCodeDate = (Date) session.getAttribute("regCodeTime");
//        logger.info("the regcode validity {}", DateFormatter.validate(regCodeDate,new Date()));
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
    }


    @PostMapping("/rest/username/check")
    public
    @ResponseBody
    String checkUsername(WebRequest webRequest) {

        String username = webRequest.getParameter("username");
        logger.info("Username : " + username);
        RetailUser user = retailUserService.getUserByName(username);
        logger.info("USER RETURNED{}", user);
        if (null == user) {
            return "true";
        }
        return "false";
    }

    @GetMapping("/rest/username/{username}")
    public
    @ResponseBody
    String usernameCheck(@PathVariable String username) {
        RetailUser user = retailUserService.getUserByName(username);
        logger.info("USER RETURNED{}", user);
        if (user == null) {
            return "false";
        }
        return user.getUserName();
    }


    @PostMapping("/rest/password/password")
    public
    @ResponseBody
    String checkRegPassword(WebRequest webRequest) {
        String password = webRequest.getParameter("password");
        logger.info("the password {}",password);
        String message = null;
//        try {
            message = passwordPolicyService.validate(password, null);
            if (!"".equals(message)) {
                return message;
            }
//        } catch (Exception e) {
//            logger.error("Error Validating ",e);
//            return e.getMessage();
//        }

        return "true";
    }


    @PostMapping("/rest/password/check")
    public
    @ResponseBody
    String checkPassword(WebRequest webRequest) {
        String password = webRequest.getParameter("password");
        String username = webRequest.getParameter("username");
        RetailUser user = retailUserService.getUserByName(username);
//        logger.info("the user name {} and password {}",user.getUserName(),password);E
        String message = passwordPolicyService.validate(password, user);
//        logger.info("the message {}",message);
        if (!"".equals(message)) {
            return message;
        }
        return "true";
    }


    @GetMapping("/reporttoken")
    public String showReportToken(Model model) {
        return "cust/reporttoken";
    }

    @PostMapping("/reporttoken")
    public
    @ResponseBody
    String reportToken(WebRequest webRequest) {
        Iterator<String> iterator = webRequest.getParameterNames();

        while (iterator.hasNext()) {
            logger.info(iterator.next());
        }

        String username = webRequest.getParameter("username");
        String token = webRequest.getParameter("token");
        try {

            if ("".equals(username) || username == null) {
                logger.error("No username found");
                return "false";
            }
            if ("".equals(token) || token == null) {
                logger.error("No token selected");
                return "false";
            }

            RetailUser retailUser = retailUserService.getUserByName(username);
            Boolean res = securityService.deActivateToken(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
            if (res) {
                return "true";
            } else {
                return "false";
            }

        } catch (Exception e) {
            return "false";
        }
    }


//    @GetMapping("/forgot/username")
//    public String showForgotUsername(Model model) {
//        logger.info("forget username 2");
//        RetrieveUsernameForm retrieveUsernameForm= new RetrieveUsernameForm();
//        retrieveUsernameForm.step = "1";
//        model.addAttribute("retUsernameForm", retrieveUsernameForm);
//        return "cust/forgotusername";
//    }
//
//    @PostMapping("/forgot/username")
//    public
//    @ResponseBody
//    String forgotUsername(WebRequest webRequest) {
//        Iterator<String> iterator = webRequest.getParameterNames();
//logger.info("forget username");
//        while(iterator.hasNext()){
//            logger.info(iterator.next());
//        }
////
////
////        String accountNumber = webRequest.getParameter("acct");
////        String securityQuestion = webRequest.getParameter("securityQuestion");
////        String securityAnswer = webRequest.getParameter("securityAnswer");
//        String customerId = webRequest.getParameter("customerId");
//        try {
//            if ("".equals(customerId) || customerId == null) {
//                logger.error("Account Number not valid");
//                return "false";
//            }
//
//            RetailUser user = retailUserService.getUserByCustomerId(customerId);
//
//            //confirm security question is correct
//            String secAnswer="";
//            Map<String, List<String>> qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());
//            //List<String> sec = null;
//            if (qa != null){
////                List<String> questions= qa.get("questions");
//                List<String> answers= qa.get("answers");
//                String result = StringUtil.compareAnswers(webRequest,answers);
////                    secAnswer = answers.stream().filter(Objects::nonNull).findFirst().orElse("");
//
//                if (result.equalsIgnoreCase("true")){
//                    logger.debug("User Info {}:", user.getUserName());
//                    //Send Username to Email
//                    Email email = new Email.Builder()
//                            .setRecipient(user.getEmail())
//                            .setSubject(messageSource.getMessage("retrieve.username.subject",null,locale))
//                            .setBody(String.format(messageSource.getMessage("retrieve.username.message",null,locale),user.getFirstName(), user.getUserName()))
//                            .build();
//                    mailService.send(email);
//                    return "true";
//                }
//
//            }else {
//                return "false";
//            }
//
//
//
//        }catch (InternetBankingException e){
//            return "false";
//        }
//
//        return "false";
//    }
//

//
//    @PostMapping("/token/authenticate")
//    public
//    @ResponseBody
//    String performTokenAuthentication(WebRequest webRequest, HttpServletResponse webResponse) {
//        String username = webRequest.getParameter("username");
//        String tokenString = webRequest.getParameter("tokenString");
//        //TODO
//        boolean result = securityService.performTokenValidation(username, tokenString);
//        webResponse.addHeader("contentType", "application/json");
//        return "{'success': " + result + "}";
//    }


    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        session.removeAttribute("regCodeTime");
        session.removeAttribute("regCode");
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.step = "1";
        model.addAttribute("registrationForm", registrationForm);

        File phish = new File(fullImagePath);
        List<String> images = new ArrayList<String>();
        if (phish.isDirectory()) { // make sure it's a directory
            for (final File f : phish.listFiles(IMAGE_FILTER)) {
                images.add(f.getName());
                logger.info("FILE NAME {}", f.getName());
            }
        }

        model.addAttribute("images", images);
        model.addAttribute("imagePath", imagePath);

        List<String> policies = passwordPolicyService.getPasswordRules();
        model.addAttribute("policies", policies);


//        List<SecurityQuestions> secQues = securityQuestionService.getSecQuestions();
//        logger.info("security questions "+secQues);
//        int noOfQuestions = securityService.getMinUserQA();
//
//        ArrayList[] masterList = new ArrayList[noOfQuestions];
//
//        //init arrays
//        for (int idx=0; idx < noOfQuestions ; ++idx) {
//           masterList[idx] = new ArrayList();
//        }
//
//        //porpulate arrays
//        for(int idx=0 ; idx < secQues.size() ; ++idx){
//            masterList[idx % noOfQuestions].add(secQues.get(idx));
//        }
//
//        for (ArrayList m:masterList
//                ) {
//            logger.info("master question "+masterList);
//        }

        List<CodeDTO> secQues = codeService.getCodesByType("SECURITY_QUESTION");
        int noOfQuestions = securityService.getMinUserQA();
//        logger.info("num of qs on entrust {}",noOfQuestions);
        ArrayList[] masterList = new ArrayList[noOfQuestions];
        int questionsPerSection = (secQues.size() - (secQues.size() % noOfQuestions)) / noOfQuestions;
//        logger.info("question per section {}",questionsPerSection);
        int questnPostn = 0;
        for (int i = 0; i < noOfQuestions; i++) {
            masterList[i] = new ArrayList<>();
            for (int j = 0; j < questionsPerSection; j++) {
                masterList[i].add(secQues.get(questnPostn));
                questnPostn++;
            }

        }
        logger.info("master question length" + masterList.length);

        for (int i = 0; i < masterList.length; i++) {
//            logger.info("master question "+i+" "+masterList[i]);
        }
//        logger.trace("master question "+ Arrays.toString(masterList));


        logger.info("MASTER LIST {}", masterList);
        model.addAttribute("secQuestions", masterList);
        model.addAttribute("noOfQuestions", noOfQuestions);

        PasswordStrengthDTO passwordStrengthDTO = passwordPolicyService.getPasswordStrengthParams();
        logger.info("Password Strength {}" + passwordStrengthDTO);
        model.addAttribute("passwordStrength", passwordStrengthDTO);

        //logger.info("MIN SEC {}", noOfQuestions);

        return "cust/register/registration";
    }

    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "jpeg", "jpg", "gif", "png", "bmp" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };


    @PostMapping("/register")
    public
    @ResponseBody
    String addUser(WebRequest webRequest, RedirectAttributes redirectAttributes) {
        Iterator<String> iterator = webRequest.getParameterNames();

        while (iterator.hasNext()) {
            logger.info(iterator.next());
        }

        String accountNumber = webRequest.getParameter("accountNumber");
        String email = webRequest.getParameter("email");
        String dob = webRequest.getParameter("birthDate");
        String userName = webRequest.getParameter("userName");
        String password = webRequest.getParameter("password");
        String confirmPassword = webRequest.getParameter("confirm");
        String customerId = webRequest.getParameter("customerId");
        String phishing = webRequest.getParameter("phishing");
        String caption = webRequest.getParameter("caption");
        String noOfQuestions = webRequest.getParameter("noOfQuestions");
        logger.info("no of questions {} ", noOfQuestions);

        logger.info("------------------------------------------------");
        logger.info("New user email: {}", email);
        logger.info("New user date of birth: {}", dob);


        String bvn = "";
//        String secQuestion = webRequest.getParameter("securityQuestion1");
//        String secAnswer = webRequest.getParameter("securityAnswer1");
        List<String> secQuestions = new ArrayList<>();
        List<String> securityAnswers = new ArrayList<>();
        logger.info("Customer Id {}:", customerId);


        if (phishing == null || "".equals(phishing)) {
            return messageSource.getMessage("phishing.empty", null, locale);
        }

        if (noOfQuestions != null) {
            for (int i = 0; i < Integer.parseInt(noOfQuestions); i++) {
                secQuestions.add(webRequest.getParameter("securityQuestion" + i));
                securityAnswers.add(webRequest.getParameter("securityAnswer" + i));

                logger.info(" sec questions list {}", secQuestions);
                logger.info("sec answer list {}", securityAnswers);
            }
        }
        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, dob);


        if (details.getCifId() == null || details.getCifId().isEmpty()) {
            logger.error("Account Number not valid");
            return messageSource.getMessage("account.reg.error", null, locale);
        }

        try {
            doesUserExist(customerId);
        } catch (InternetBankingException e) {
            return messageSource.getMessage("user.reg.exists", null, locale);
        }


        if (details.getBvn() != null && !details.getBvn().isEmpty()) {
//            logger.error("No Bvn found");
            bvn = details.getBvn();
        }


        //confirm passwords are the same
        boolean isValid = password.trim().equalsIgnoreCase(confirmPassword.trim());
        if (!isValid) {
            logger.error("Passwords do not match");
            return messageSource.getMessage("password.mismatch", null, locale);
        }

        //password meets policy


        //security questions
//        String securityQuestion = secQuestion;
//        String securityAnswer = secAnswer;
//        logger.info("Question" + secQuestion);
//        logger.info("Answer" + secAnswer);


        //phishing image
        //byte[] encodedBytes = Base64.encodeBase64(phishing.getBytes());
        File image = new File(fullImagePath, phishing);
        Long length = image.length();
        // length <= Integer.MAX_VALUE;
        //TODO: check file is not bigger than max int
        byte buffer[] = new byte[length.intValue()];


        try {
            FileInputStream fis = new FileInputStream(image);
            int cnt = fis.read(buffer);
            //ensure cnt == length
        } catch (Exception e) {
            //TODO: handle exception
        }

        String encPhishImage = java.util.Base64.getEncoder().encodeToString(buffer);
        logger.info("ENCODED STRING " + encPhishImage);

//        System.out.println("encodedBytes " + new String(encodedBytes));
//        String encPhishImage = new String(encodedBytes);

        RetailUserDTO retailUserDTO = new RetailUserDTO();
        retailUserDTO.setUserName(userName);
        retailUserDTO.setEmail(details.getEmail());
        retailUserDTO.setPassword(password);
        retailUserDTO.setCustomerId(customerId);
        retailUserDTO.setBvn(bvn);
        retailUserDTO.setSecurityQuestion(secQuestions);
        retailUserDTO.setSecurityAnswer(securityAnswers);
        retailUserDTO.setPhishingSec(encPhishImage);
        retailUserDTO.setCaptionSec(caption);
        try {
            String message = retailUserService.addUser(retailUserDTO, details);
            logger.info("MESSAGE", message);
//            redirectAttributes.addAttribute("success", "true");
            return "true";
        } catch (InternetBankingSecurityException e) {
            logger.error("Error creating retail user", e);
            //redirectAttributes.addFlashAttribute(messageSource.getMessage("user.add.failure", null, locale));
            return e.getMessage();
        } catch (InternetBankingException e) {
            logger.error("Error creating retail user", e);

            return e.getMessage();
        }

    }

    private void doesUserExist(String customerId) {
        RetailUser user = retailUserService.getUserByCustomerId(customerId);
        if (user != null) {
            throw new InternetBankingException(messageSource.getMessage("user.reg.exists", null, locale));
        }

        List<AccountDTO> accounts = accountService.getAccounts(customerId);
        if (!accounts.isEmpty()) {
            throw new InternetBankingException(messageSource.getMessage("user.reg.exists", null, locale));
        }
    }


    @GetMapping("/rest/redirect/login")
    public String redirectToLogin(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.reg.success", null, locale));
        return "redirect:/login/retail";
    }


}
