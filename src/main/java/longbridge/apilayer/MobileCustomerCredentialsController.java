package longbridge.apilayer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.apilayer.data.ResponseData;
import longbridge.dtos.UserRetrievalDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustResetPassword;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@Api(value = "Customer Credentials", description = "Customer Credential Retrieval", tags = {"Customer Credential"})
@RequestMapping(value = "/api/retrieval")
public class MobileCustomerCredentialsController {


    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String failure;
    private Locale locale = LocaleContextHolder.getLocale();
    String message ="Successful";


    @Autowired
    RetailUserService retailUserService;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    UserRetrievalService userRetrievalService;
    @Autowired
    SecurityService securityService;
    @Autowired
    CorporateService corporateService;
    @Autowired
    AccountService accountService;
    @Autowired
    MessageSource messageSource;



    //Retail Username Retrieval
    @ApiOperation(value = "Retail get Security Question for Username Retrieval")
    @GetMapping(value = "/retail/securityquestion/{acctNum}")
    public ResponseEntity<?> SecurityQuestion (@ApiParam("Account Number") @PathVariable String acctNum){

        try{
            Account account = accountService.getAccountByAccountNumber(acctNum);
            if (account !=null) {
                RetailUser user = retailUserService.getUserByCustomerId(account.getCustomerId());
                Map<String, List<String>> qa = securityService.getUserQA(user.getEntrustId(), user.getEntrustGroup());

                if (! qa.isEmpty()){
                    List<String> securityQuestion = qa.get("questions");
                    responseData.setMessage(message);
                    responseData.setData(securityQuestion);
                    responseData.setError(false);
                    responseData.setCode("00");
                    return new ResponseEntity<>(responseData, HttpStatus.OK);
                }

                else
                    responseData.setMessage("Could not get Security Question from server, please try again");
                    responseData.setError(true);
                responseData.setCode("99");
                    return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
            }
            else
                responseData.setMessage("Ensure you put in a valid account number");
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){
            logger.info("Retail Security question  Error {}",e);
            failure= e.getMessage();
            responseData.setMessage(failure);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }


    @ApiOperation(value = "Retail Forgot Username ")
    @PostMapping(value = "/retail/forgotusername")
    public ResponseEntity<?> forgotUsername (@RequestBody UserRetrievalDTO userRetrievalDTO){

        try{
            String userNameRetrieval = userRetrievalService.userNameRetrieval(userRetrievalDTO);
            if (userNameRetrieval .equalsIgnoreCase("true")) {
                responseData.setMessage("Your username has been sent to your registered email ");
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage("Invalid answer provided to security questions");
                 responseData.setError(true);
            responseData.setCode("99");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){
            logger.info("Retail User forgot username error {} ",e);
            failure= e.getMessage();
            responseData.setMessage(failure);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }

    //Retail Forgot Password
    @ApiOperation(value = "Retail get Security Question for Password Retrieval")
    @GetMapping(value = "/retail/forgotpassword/{userName}")
    public ResponseEntity<?> forgotPassword (@PathVariable String userName){

        try{
            RetailUser retailUser = retailUserService.getUserByName(userName);
            if (retailUser !=null) {
                Map<String, List<String>> qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());

                if (qa != null && !qa.isEmpty()) {
                    List<String> securityQuestion = qa.get("questions");
                    if (!securityQuestion.isEmpty()) {
                        responseData.setMessage(message);
                        responseData.setData(securityQuestion);
                        responseData.setError(false);
                        responseData.setCode("00");
                        return new ResponseEntity<>(responseData, HttpStatus.OK);
                    } else

                        responseData.setMessage("Could not get Security Question from server, please try again");
                       responseData.setError(true);
                       responseData.setData(null);
                    responseData.setCode("99");
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            }
            else
                responseData.setMessage("Invalid Credentials");
            responseData.setData(null);
                responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);


        }catch (InternetBankingException e){
            logger.info("Retail User forgot password error {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

        }

    }


    @ApiOperation(value = "Confirms Security Question and sends Reset Password (Retail)")
    @PostMapping(value = "/retail/sendresetpassword")
    public ResponseEntity<?> sendResetPassword (@RequestBody UserRetrievalDTO userRetrievalDTO) {


        try {
            logger.info("user name {} ", userRetrievalDTO.getUserName());

            Map<String, List<String>> qa;
            RetailUser retailUser = retailUserService.getUserByName(userRetrievalDTO.getUserName());
            retailUser.setEmailTemplate("mail/forgotpasswordMobile");
            logger.info("retail user about getting its password {} ", retailUser.getCustomerId());
            //            Corporate corporate = corporateService.getCorporateByCorporateId(userRetrievalDTO.getCorpID());

            if (retailUser != null) {

                //confirm security question is correct
                qa = securityService.getUserQA(retailUser.getEntrustId(), retailUser.getEntrustGroup());
                List<String> answers = qa.get("answers");
                List<String> providedAnswers = userRetrievalDTO.getSecQesAns();
                String result = StringUtil.compareAnswers(providedAnswers, answers);

                if (result.equalsIgnoreCase("true")) {
                    try {
                        String sendTempPassword = userRetrievalService.sendGeneratedPassword(userRetrievalDTO);
                        if (sendTempPassword.equalsIgnoreCase("true")) {
                            responseData.setMessage("A reset code has been sent to your email address.");
                           responseData.setError(false);
                           responseData.setData(null);
                           responseData.setCode("00");
                            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                        }
                        else
                            responseData.setMessage(sendTempPassword);
                       responseData.setError(true);
                        responseData.setCode("99");
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

                    } catch (InternetBankingException e) {
                        logger.info("Retail User forgot password error {} ", e);
                        responseData.setMessage(e.getMessage());
                        responseData.setError(true);
                        responseData.setCode("99");
                        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

                    }

                } else
                responseData.setMessage(messageSource.getMessage("sec.ans.failed", null, locale));
               responseData.setError(true);
               responseData.setCode("99");
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

            }
        } catch (InternetBankingException e) {
            logger.error("Error validating security questions and answers", e);
            responseData.setMessage(e.getMessage());
           responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
    }




    @PostMapping(value = "/retail/getpassword")
    public ResponseEntity<?> getPassword (@RequestBody UserRetrievalDTO userRetrievalDTO) {


        try {
            logger.info("user name {} ", userRetrievalDTO.getUserName());

            RetailUser retailUser = retailUserService.getUserByName(userRetrievalDTO.getUserName());
//            logger.info("retail user about getting its password {} ", retailUser.getCustomerId());

            if (retailUser != null) {


                        String getPassword = userRetrievalService.retrievePassword(userRetrievalDTO);
                        if (getPassword !=null) {

                            responseData.setMessage(message);
                            responseData.setError(false);
                            responseData.setData(getPassword);
                            responseData.setCode("00");
                            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                        }
                        else{

                            responseData.setMessage(messageSource.getMessage("message.username.check", null, locale));
                            responseData.setError(true);
                            responseData.setData(null);
                            responseData.setCode("99");
                            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                        }

                    }

                    else{

                responseData.setMessage(messageSource.getMessage("message.username.check", null, locale));
                responseData.setError(true);
                responseData.setData(null);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }




        } catch (InternetBankingException | NullPointerException e) {
            logger.error("Retail User forgot password error {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }

//        return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Verifies the rest password sent (Retail)")
    @PostMapping(value = "/retail/verifyresetpassword")
    public ResponseEntity<?> verifyResetPassword (@RequestBody UserRetrievalDTO userRetrievalDTO){

        try{
            String verifyResetPassword = userRetrievalService.verifyGeneratedPassword(userRetrievalDTO);
            if (verifyResetPassword.equalsIgnoreCase("true")) {
                responseData.setMessage(verifyResetPassword);
               responseData.setError(false);
               responseData.setCode("00");
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage(verifyResetPassword);
            responseData.setError(true);
            responseData.setData(null);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);


        }catch (InternetBankingException e){
            logger.info("Retail User verify send password error {} ",e);
            failure=e.getMessage();
            responseData.setMessage(failure);
           responseData.setError(true);
           responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
    }


    @ApiOperation(value = "Retail Checks if the new password confonds with password policy")
    @PostMapping(value = "/retail/checkpasswordpolicy")
    public ResponseEntity<?> CheckPasswordPolicy (@RequestBody UserRetrievalDTO userRetrievalDTO){

        try{
            String chkPasswordPolicy = userRetrievalService.verifyPasswordPolicy(userRetrievalDTO);
            if (chkPasswordPolicy.equalsIgnoreCase("true")){
                responseData.setMessage(chkPasswordPolicy);
               responseData.setError(false);
               responseData.setData(null);
                responseData.setCode("00");
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage(chkPasswordPolicy);
            responseData.setData(null);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){
            logger.info("Corporate User chk password policy {} ",e);
            failure = e.getMessage();
            responseData.setMessage(failure);
            responseData.setData(null);
           responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }


    @ApiOperation(value = "Retail ForgotPassword")
    @PostMapping(value = "/retail/forgotpassword")
    public ResponseEntity<?> validateToken (@RequestBody UserRetrievalDTO userRetrievalDTO){

        String username = userRetrievalDTO.getUserName();
        try{
            String validateToken = userRetrievalService.validateToken(userRetrievalDTO);
            if (validateToken.equalsIgnoreCase("true")){

                RetailUser retailUser = retailUserService.getUserByName(username);
                logger.debug("Retail user retrieved {}",retailUser);
                if (retailUser == null){
                    logger.error("Retail username {} returned null object",username);
                    responseData.setMessage("Invalid Username");
                    responseData.setData(null);
                    responseData.setError(true);
                    responseData.setCode("99");
                    return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
                }

                CustResetPassword custResetPassword = new CustResetPassword();
                custResetPassword.setNewPassword(userRetrievalDTO.getNewPassword());
                custResetPassword.setConfirmPassword(userRetrievalDTO.getConfirmPassword());

                String message = retailUserService.resetPasswordMobileUser(retailUser,custResetPassword);
                if (message.equalsIgnoreCase("Password changed successfully")) {


                    responseData.setMessage(message);
                    responseData.setData(null);
                   responseData.setError(false);
                    responseData.setCode("00");
                    return  new ResponseEntity<Object>(responseData,HttpStatus.OK);
                }
                else
                    responseData.setMessage(message);
                    responseData.setError(true);
                responseData.setData(null);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
            else
                responseData.setMessage(validateToken);
           responseData.setError(true);
            responseData.setData(null);
            responseData.setCode("99");
            return  new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);



        }catch (InternetBankingException e){
            logger.info("Retail User verify send password error {} ",e);
            responseData.setMessage(e.getMessage());
           responseData.setError(true);
            responseData.setData(null);
            responseData.setCode("99");
            return  new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }

    }



    //Corporate Username Retrieval
    @ApiOperation(value = "Corporate get Security Question for Username Retrieval")
    @PostMapping(value = "/corp/securityquestion")
    public ResponseEntity<?> corporateUserSecurityQuestion (@RequestBody UserRetrievalDTO userRetrievalDTO){


        try{
            logger.info("email {} ", userRetrievalDTO.getUserEmail());
            String msg ="Please supply valid Corporate ID. and email";
            String corpId=userRetrievalDTO.getCorpID();
            String email = userRetrievalDTO.getUserEmail();
            Corporate corporate = corporateService.getCorporateByCorporateId(corpId);
            logger.info("corporate {} ", corporate);
            if(corporate != null) {
                CorporateUser corporateUser = corporateUserService.getUserByCifAndEmailIgnoreCase(corporate, email);
                logger.info("corporate user {} ", corporateUser);
                if(corporateUser != null){

                    Map<String, List<String>> qa =  securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
                    if (!qa.isEmpty()){
                    List<String> securityQuestion = qa.get("questions");
                        responseData.setMessage(message);
                        responseData.setData(securityQuestion);
                        responseData.setError(false);
                        responseData.setCode("00");
                    return new ResponseEntity<>(responseData, HttpStatus.OK);
                    }
                    else
                        responseData.setMessage("Could not get Security Question from server, please try again");
                        responseData.setError(true);
                    responseData.setCode("99");
                    responseData.setData(null);
                        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
                }
                responseData.setMessage(msg);
                responseData.setError(true);
                responseData.setData(null);
                responseData.setCode("99");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

            }
            else
                responseData.setMessage(msg);
                responseData.setError(true);
            responseData.setData(null);
            responseData.setCode("99");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){
            logger.info("corporate user Security question  Error {}",e);
            failure = e.getMessage();
            responseData.setMessage(failure);
            responseData.setError(true);
            responseData.setData(null);
            responseData.setCode("99");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "Corporate Forgot Username ")
    @PostMapping(value = "/corp/forgotusername")
    public ResponseEntity<?> corporateForgotUsername (@RequestBody UserRetrievalDTO userRetrievalDTO){

        try{
            logger.info("am here");
            String userNameRetrieval = userRetrievalService.corporateUserNameRetrieval(userRetrievalDTO);
            if (userNameRetrieval.equalsIgnoreCase("true")) {
                responseData.setMessage("Your username has been sent to your registered email ");
                responseData.setCode("00");
                responseData.setError(false);
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            responseData.setMessage("Invalid answer provided to security questions");
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){
            logger.info("Corporate User forgot username error {} ",e);
            failure = e.getMessage();
            responseData.setMessage(failure);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }


    //Corporate Forgot Password

    @ApiOperation(value = "Corporate get Security Question for Password Retrieval")
    @PostMapping(value = "/corp/forgotpassword/getsecurityquestion")
    public ResponseEntity<?> CorpForgotPassword (@RequestBody UserRetrievalDTO userRetrievalDTO){
        String username =  userRetrievalDTO.getUserName();
        String corporateId = userRetrievalDTO.getCorpID();
        logger.info("corp info {},{} ", username,corporateId);

        try{
            CorporateUser corporateUser = corporateUserService.getUserByNameAndCorporateId(username, corporateId);
            if (corporateUser !=null) {
                Map<String, List<String>> qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());

                if (qa != null && !qa.isEmpty()) {
                    List<String> securityQuestion = qa.get("questions");
                    if (!securityQuestion.isEmpty()) {
                        responseData.setMessage(message);
                        responseData.setData(securityQuestion);
                       responseData.setError(false);
                       responseData.setCode("00");
                        return new ResponseEntity<>(responseData, HttpStatus.OK);
                    } else

                    responseData.setMessage(messageSource.getMessage("corp.reg.inconplete.failed", null, locale));
                    responseData.setData(null);
                    responseData.setCode("99");
                    responseData.setError(true);
                       responseData.setCode("99");
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            }
            else
                responseData.setMessage("Invalid Credentials");
            responseData.setData(null);
            responseData.setCode("99");
                responseData.setError(true);
                responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);


        }catch (InternetBankingException e){
            logger.info("Retail User forgot password error {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

        }
    }

    @ApiOperation(value = "Confirms Security Question and sends Reset Password (Corporate)")
    @PostMapping(value = "/corp/sendresetpassword")
    public ResponseEntity<?> sendCorpResetPassword (@RequestBody UserRetrievalDTO userRetrievalDTO) {


        try {
            logger.info("user name {} ", userRetrievalDTO.getUserName());

            Map<String, List<String>> qa;
            CorporateUser corporateUser = corporateUserService.getUserByName(userRetrievalDTO.getUserName());
            corporateUser.setEmailTemplate("mail/forgotpasswordMobile");

            if (corporateUser != null) {

                //confirm security question is correct
                qa = securityService.getUserQA(corporateUser.getEntrustId(), corporateUser.getEntrustGroup());
                List<String> answers = qa.get("answers");
                List<String> providedAnswers = userRetrievalDTO.getSecQesAns();
                String result = StringUtil.compareAnswers(providedAnswers, answers);

                if (result.equalsIgnoreCase("true")) {
                    try {
                        String sendPassword = userRetrievalService.sendCorpGeneratedPassword(userRetrievalDTO);
                        if (sendPassword.equalsIgnoreCase("true")) {
                            responseData.setMessage("A reset code has been sent to your email address.");
                            responseData.setData(null);
                            responseData.setCode("00");
                           responseData.setError(false);
                            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                        }
                        else
                            responseData.setMessage(sendPassword);
                        responseData.setData(null);
                            responseData.setCode("99");
                            responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    } catch (InternetBankingException e) {
                        logger.info("Retail User forgot password error {} ", e);
                        responseData.setMessage(e.getMessage());
                        responseData.setData(null);
                        responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

                    }

                } else
                    responseData.setMessage(messageSource.getMessage("sec.ans.failed", null, locale));
                    responseData.setCode("99");
                responseData.setData(null);
                     responseData.setError(true);
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

            }
        } catch (InternetBankingException e) {
            logger.error("Error validating security questions and answers", e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
    }




    @ApiOperation(value = "Verifies the rest password sent (Corporate)")
    @PostMapping(value = "/corp/verifyresetpassword")
    public ResponseEntity<?> verifyCorpResetPassword (@RequestBody UserRetrievalDTO userRetrievalDTO){

        try{
            String verifyCorpResetPassword = userRetrievalService.verifyCorpGeneratedPassword(userRetrievalDTO);
            if (verifyCorpResetPassword.equalsIgnoreCase("true")) {
                responseData.setMessage(verifyCorpResetPassword);
                responseData.setData(null);
                responseData.setCode("00");
                responseData.setError(false);
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage(verifyCorpResetPassword);
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);


        }catch (InternetBankingException e){
            logger.info("Corporate User verify send password error {} ",e);
            failure=e.getMessage();
            responseData.setMessage(failure);
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
    }

    @ApiOperation(value = "Corporate Checks if the new password confonds with password policy")
    @PostMapping(value = "/corp/checkpasswordpolicy")
    public ResponseEntity<?> corporateCheckPasswordPolicy (@RequestBody UserRetrievalDTO userRetrievalDTO){

        try{
            String chkPasswordPolicy = userRetrievalService.verifyCorpPasswordPolicy(userRetrievalDTO);
            if (chkPasswordPolicy.equalsIgnoreCase("true")){
            responseData.setMessage(chkPasswordPolicy);
                responseData.setData(null);
                responseData.setCode("00");
                responseData.setError(false);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage(chkPasswordPolicy);
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){
            logger.info("Corporate User chk password policy {} ",e);
            failure = e.getMessage();
            responseData.setMessage(failure);
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "Corporate ForgotPassword")
    @PostMapping(value = "/corp/forgotpassword")
    public ResponseEntity<?> validateCorpToken (@RequestBody UserRetrievalDTO userRetrievalDTO){

        String username = userRetrievalDTO.getUserName();
        try{
            String validateToken = userRetrievalService.validateCorpToken(userRetrievalDTO);
            if (validateToken.equalsIgnoreCase("true")){

                CorporateUser corporateUser = corporateUserService.getUserByName(username);

                if (corporateUser == null){
                    logger.error("Corp username {} returned null object",username);
                    responseData.setMessage("Invalid Username");
                    responseData.setData(null);
                    responseData.setCode("99");
                    responseData.setError(true);

                    return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
                }


                //change password
                CustResetPassword custResetPassword = new CustResetPassword();
                custResetPassword.setNewPassword(userRetrievalDTO.getNewPassword());
                custResetPassword.setConfirmPassword(userRetrievalDTO.getConfirmPassword());

                String message = corporateUserService.resetPasswordForMobileUsers(corporateUser,custResetPassword);
                if (message.equalsIgnoreCase("Password changed successfully")) {
                    responseData.setMessage(message);
                    responseData.setData(null);
                    responseData.setCode("00");
                    responseData.setError(false);
                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                }
                else
                    responseData.setMessage(message);
                responseData.setData(null);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
            else
                responseData.setMessage(validateToken);
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return  new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);



        }catch (InternetBankingException e){
            logger.info("Retail User verify send password error {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return  new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping(value = "/corp/getpassword")
    public ResponseEntity<?> getCorporatePassword (@RequestBody UserRetrievalDTO userRetrievalDTO) {


        try {
            logger.info("user name {} ", userRetrievalDTO.getUserName());


            CorporateUser corporateUser = corporateUserService.getUserByName(userRetrievalDTO.getUserName());

            if (corporateUser != null) {

                logger.info("corporate user about getting its password {} ", corporateUser.getCorporate().getCustomerId());

//                if (result.equalsIgnoreCase("true") && validateToken.equalsIgnoreCase("true")) {
                    try {
                        String getPassword = userRetrievalService.retrievePassword(userRetrievalDTO);
                        if ( getPassword !=null) {
                            responseData.setMessage(message);
                            responseData.setError(false);
                            responseData.setData(getPassword);
                            responseData.setCode("00");
                            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                        }
                        else{

                            responseData.setMessage(messageSource.getMessage("message.username.check", null, locale));
                            responseData.setError(true);
                            responseData.setData(null);
                            responseData.setCode("99");
                            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                        }


                    } catch (InternetBankingException e) {
                        logger.info("Coprorate User forgot password error {} ", e);
                        responseData.setMessage(e.getMessage());
                        responseData.setError(true);
                        responseData.setCode("99");
                        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

                    }

                } else
                    responseData.setMessage(messageSource.getMessage("message.username.check", null, locale));
                responseData.setError(true);
                responseData.setData(null);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        } catch (InternetBankingException e) {
            logger.error("Error validating security questions and answers", e);
            responseData.setMessage(messageSource.getMessage("message.username.check", null, locale));
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
    }



}
