package longbridge.apiLayer;

import io.swagger.annotations.Api;
import longbridge.api.CustomerDetails;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.*;
import longbridge.dtos.apidtos.MobileRegSecQuestionDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.ImageUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@Api(value = "Self Registration", description = "Retail Registration", tags = {"Self Registration"})
@RequestMapping(value = "/api/register")
public class MobileUserRegController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();
    ResponseData responseData = new ResponseData();
    String message = "Successful";

    @Autowired
    UserRegService userRegService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    IntegrationService integrationService;
    @Autowired
    AccountService accountService;
    @Autowired
    SecurityService securityService;
    @Autowired
    MessageSource messageSource;

    @Value("${phishing.image.folder}")
    private String fullImagePath;

    @Value("${antiphishingimagepath}")
    private String imagePath;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping(value = "/validate/accountdetails")
    public ResponseEntity<?> validateAccountDetails (@RequestBody UserRegDTO userRegDTO){

        String accountNumber = userRegDTO.getAcctNumber();
        String email = userRegDTO.getEmail();
        logger.debug("Email : " + email);
        String birthDate =userRegDTO.getDob();
        logger.debug("BirthDate : " + birthDate);
        String customerId ="";

        if(birthDate == ""){
            birthDate = "19-20-1970";
        }
        if(email == ""){
            email = "ib@coronationmb.com";
        }

        try{
            logger.info("am here");
            String validateAcctDetails = userRegService.validateDetails(userRegDTO);
            if (validateAcctDetails.equalsIgnoreCase("true")){

                String sendCode = userRegService.sendRegCode(userRegDTO);
                if (sendCode.equalsIgnoreCase("true")) {
                    CustomerDetails details = integrationService.isAccountValid(accountNumber, email, birthDate);
                    logger.debug("Customer details {} ", details);
                    if (details.getCifId() != null) {
                        RetailUser user = retailUserService.getUserByCustomerId(details.getCifId());
                        List<AccountDTO> account = accountService.getAccountsForReg(details.getCifId());
                        logger.info("ACCOUNT {} ", account);
                        if (user == null && account.isEmpty()) {
                            logger.debug("User {} does not exist on the platform currently, can go on to register",details.getCifId());
                            customerId = details.getCifId();
                            responseData.setMessage("Registration code has been sent to your registered phone number. If you do not receive a message after 3 minutes, please retry.");
                            responseData.setData(null);
                            responseData.setError(false);
                            responseData.setCode("00");
                            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                        } else {
                            logger.warn("User already exists on the system, cannot be allowed to register");
                            responseData.setMessage("This account already exists on our internet banking platform, Please try logging in.");
                            responseData.setError(true);
                            responseData.setData(null);
                            responseData.setCode("99");
                            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                        }

                    } else {
                        logger.warn("The account details for {} could not be found. The reasons could be that the account is NOT VERIFIED, CLOSED or DELETED",accountNumber);
                        responseData.setMessage("The account details for {} could not be found. The reasons could be that the account is NOT VERIFIED, CLOSED or DELETED");
                        responseData.setError(true);
                        responseData.setData(null);
                        responseData.setCode("99");
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }


                }
                else
                    responseData.setError(true);
                    responseData.setMessage("Failed to send registration code. Please try again.");
                responseData.setCode("99");
                    return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
            }
            else
                responseData.setMessage(validateAcctDetails);
                 responseData.setError(true);
            responseData.setCode("99");
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){
            logger.info("Retail user registration validate account error {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.info("Retail user registration validate account error 2 {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }


    @PostMapping(value = "/validate/profiledetails")
    public ResponseEntity<?> profileDetails (@RequestBody UserRegDTO userRegDTO) {

        try{

            String validateRegCode = userRegService.checkRegCode(userRegDTO);
            if (validateRegCode.equalsIgnoreCase("true")){
                String validateUsername =userRegService.usernameCheck(userRegDTO);
                if (validateUsername.equalsIgnoreCase("true")){

                    String validatePassword = userRegService.passwordCheck(userRegDTO);
                    if (validatePassword.equalsIgnoreCase("true")) {

                try {
                    int noOfQuestions = securityService.getMinUserQA();
                    logger.info("no of secQuestions {} ", noOfQuestions);
                    ArrayList[] secQuestion = userRegService.getSecurityQuestionsMobile(noOfQuestions);
                    logger.info("SecQuestion mobile size {} ", secQuestion.length);

                    responseData.setMessage(message);
                    responseData.setData(secQuestion);
                    responseData.setError(false);
                    responseData.setCode("00");
                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                }catch (Exception e){
                    logger.info("Sec question error {} ", e);
                    responseData.setMessage(e.getMessage());
                    responseData.setError(true);
                    responseData.setData(null);
                    responseData.setCode("99");
                    return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
                    }
                    else
                        responseData.setMessage(validatePassword);
                        responseData.setError(true);
                        responseData.setData(null);
                    responseData.setCode("99");
                    return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

                }
                responseData.setMessage(validateUsername);
                responseData.setError(true);
                responseData.setData(null);
                responseData.setCode("99");
                return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
            responseData.setMessage(validateRegCode);
            responseData.setError(true);
            responseData.setCode("99");
            return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){

        logger.info("profile details verification {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/secqurityquestion")
    public ResponseEntity<?> securityQuestion (@RequestBody UserRegDTO userRegDTO) {

        try{
            int noOfQuestions = securityService.getMinUserQA();
            ArrayList[] masterList = userRegService.getSecurityQuestions(noOfQuestions);
            return  new ResponseEntity<Object>(masterList,HttpStatus.OK);
        }catch (InternetBankingException e){
            logger.info("security question error {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }
    }

@PostMapping(value = "/process")
    public ResponseEntity<?> register (@RequestBody UserRegDTO userRegDTO) {
    String acctNum = userRegDTO.getAcctNumber();
    String email = userRegDTO.getEmail();
    String birthDate = userRegDTO.getDob();
    String customerId;
    String phising;

    if (birthDate == "") {
        birthDate = "19-20-1970";
    }
    if (email == "") {
        email = "ib@coronationmb.com";
    }

    RetailUserDTO retailUserDTO = new RetailUserDTO();


    if (userRegDTO.getPhising() == null || "".equals(userRegDTO.getPhising())) {
        responseData.setMessage(messageSource.getMessage("phishing.empty", null, locale));
        responseData.setData(null);
        responseData.setCode("99");
        responseData.setError(true);
        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
    }
    else
        phising= userRegDTO.getPhising();

    try {
        CustomerDetails details = userRegService.customerDetails(acctNum, email, birthDate);
        if (details.getCifId() == null || details.getCifId().isEmpty()) {
            logger.error("Account Number not valid");
            responseData.setMessage(messageSource.getMessage("account.reg.error", null, locale));
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        } else {
            try {
                customerId = details.getCifId();
                doesUserExist(customerId);

                //confirm passwords are the same
                boolean isValid = userRegDTO.getPassword().trim().equals(userRegDTO.getConfirmPassword().trim());
                if (!isValid) {
                    logger.error("Passwords do not match");
                    responseData.setMessage(messageSource.getMessage("password.mismatch", null, locale));
                    responseData.setData(null);
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }

                String encPhishImage = ImageUtil.phisingImage(fullImagePath,phising);


                retailUserDTO.setUserName(userRegDTO.getUserName());
                retailUserDTO.setEmail(details.getEmail());
                retailUserDTO.setPassword(userRegDTO.getPassword());
                retailUserDTO.setCustomerId(customerId);
                retailUserDTO.setBvn(details.getBvn());
                retailUserDTO.setSecurityQuestion(userRegDTO.getSecQes());
                retailUserDTO.setSecurityAnswer(userRegDTO.getSecAns());
                retailUserDTO.setPhishingSec(encPhishImage);
                retailUserDTO.setCaptionSec(userRegDTO.getCaption());


                try {
                    String message = retailUserService.addUser(retailUserDTO, details);
                    logger.info("MESSAGE {}", message);
                  responseData.setMessage(message);
                  responseData.setData(null);
                  responseData.setError(false);
                  responseData.setCode("00");
                    return  new ResponseEntity<Object>(responseData, HttpStatus.OK);
                } catch (InternetBankingSecurityException e) {
                    logger.error("Error creating retail user", e);
                    responseData.setMessage(e.getMessage());
                    responseData.setData(null);
                    responseData.setError(true);
                    responseData.setCode("99");
                    return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

                }


            } catch (InternetBankingException ex) {
                logger.info("Customer validation {} ", ex);
                responseData.setMessage(messageSource.getMessage("user.reg.exists", null, locale));
                responseData.setData(null);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
            catch (Exception ex) {
                logger.info("Customer validation {} ", ex);
                responseData.setMessage("Image Conversion Error");
                responseData.setData(null);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
        }

    } catch (InternetBankingException ex) {
        logger.info("Customer validation {} ", ex);
        responseData.setMessage(messageSource.getMessage("user.reg.exists", null, locale));
        responseData.setData(null);
        responseData.setCode("99");
        responseData.setError(true);
        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

    }
}




    private void doesUserExist(String customerId) {
        RetailUser user = retailUserService.getUserByCustomerId(customerId);
        if (user != null) {
            throw new InternetBankingException(messageSource.getMessage("user.reg.exists", null, locale));
        }

        List<AccountDTO> accounts = accountService.getAccountsForReg(customerId);
        if (!accounts.isEmpty()) {
            throw new InternetBankingException(messageSource.getMessage("user.reg.exists", null, locale));
        }
    }

}
