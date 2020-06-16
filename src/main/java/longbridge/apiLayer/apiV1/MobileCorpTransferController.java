package longbridge.apiLayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.apiLayer.data.ResponseData;
import longbridge.exception.InternetBankingException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.services.*;
import longbridge.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.StreamSupport;

@RestController
@Api(value = "Corporate Transfer", description = "Corporate Transfer", tags = {"Corporate Transfer"})
@RequestMapping(value = "api/v1/corp/transfer")
public class MobileCorpTransferController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String error;
    Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    TransferUtils transferUtils;
    @Autowired
    AccountService accountService;
    @Autowired
    SecurityService securityService;
    @Autowired
    TransferErrorService transferErrorService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    FinancialInstitutionService financialInstitutionService;
    @Autowired
    ConfigurationService configService;
    @Autowired
    CorpTransferService transferService;
    @Autowired
    CorpTransferService corpTransferService;
    @Autowired
    UserRetrievalService userRetrievalService;




    @ApiOperation(value = "Corporate Transfer Limit")
    @GetMapping(value = "/limit/{accountNo}/{channel}")
    public ResponseEntity<?> getLimit (@ApiParam("Account Number") @PathVariable String accountNo, @ApiParam("Transfer Channel, enter (NIP for Interbank Transfer),(INTRABANK for CMB Transfer)") @PathVariable String channel){

        try{

            String limit = transferUtils.getLimit(accountNo, channel);
            responseData.setMessage("Successful");
            responseData.setData(limit);
            responseData.setCode("00");
            responseData.setError(false);
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting transfer limit{} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setData(null);
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/destination/account/{accountNum}")
    public ResponseEntity<?> destinationAccount (@PathVariable String accountNum){

        try{

            List<String> accountList = new ArrayList<>();


            Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountNum).getCustomerId());
            logger.info("destination account {} ", accounts);

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountNum))
                    .forEach(i -> accountList.add(i.getAccountNumber()));

            return new ResponseEntity<Object>(accountList, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting transfer limit{} ", e);
            responseData.setMessage(e.getMessage());
          
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Display RcNo")
    @GetMapping(value = "/bvn")
    public ResponseEntity<?> getBvn (){

        try{
            String corpRcNo = userRetrievalService.getBvn();
            responseData.setMessage(corpRcNo);
            return new ResponseEntity<Object>( responseData, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting  RcNo {} ", e);
            responseData.setMessage(e.getMessage());
          
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
    }



//    @PostMapping(value = "/authorize/{token}")
//    public  ResponseEntity<?> addAuthorization (Principal principal, CorpTransReqEntry corpTransReqEntry, @PathVariable String token){
//
//        CorporateUser user = corporateUserService.getUserByName(principal.getName());
//
//        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");
//
//        if (setting != null && setting.isEnabled()) {
//            if (token != null && !token.isEmpty()) {
//                try {
//                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), token);
//                    if (!result) {
//                        logger.error("Error authenticating token");
//                        responseData.setMessage(messageSource.getMessage("token.auth.failure", null, locale));
//                      
//                        return  new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
//                    }
//                } catch (InternetBankingSecurityException se) {
//                    logger.error("Error authenticating token");
//                    responseData.setMessage(se.getMessage());
//                  
//                    return  new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
//                }
//            } else {
//                responseData.setMessage("Token code is required");
//              
//                return  new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
//
//            }
//        }
//
//        try {
//            String message = corpTransferService.addAuthorization(corpTransReqEntry);
//            responseData.setMessage(message);
//          
//            return  new ResponseEntity(responseData, HttpStatus.OK);
//        } catch (TransferAuthorizationException te) {
//            logger.error("Failed to authorize transfer", te);
//            responseData.setMessage(te.getMessage());
//          
//            return  new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
//        } catch (InternetBankingTransferException te) {
//            logger.error("Error making transfer", te);
//            responseData.setMessage(te.getMessage());
//          
//            return  new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
//        } catch (InternetBankingException ibe) {
//            logger.error("Failed to authorize transfer", ibe);
//            responseData.setMessage(ibe.getMessage());
//          
//            return  new ResponseEntity(responseData, HttpStatus.BAD_REQUEST);
//
//        }
//
//    }


//    @GetMapping(value = "/balance/{accountNo}")
//    public ResponseEntity<?> getAcctBalance (@PathVariable String accountNo){
//
//        try{
//
//            String balance = transferUtils.getBalance(accountNo);
//            return new ResponseEntity<Object>(balance, HttpStatus.OK);
//        }catch (InternetBankingException e){
//
//            logger.info("Error getting account balance{} ", e);
//            responseData.setMessage(e.getMessage());
//          
//            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @GetMapping(value = "/currency/{accountNo}")
//    public ResponseEntity<?> getAcctCurrency (@PathVariable String accountNo){
//
//        try{
//
//            Account account = accountService.getAccountByAccountNumber(accountNo);
//            if (account != null) {
//                String ccy = account.getCurrencyCode();
//                    responseData.setMessage(ccy);
//                  
//                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
//
//            }
//            else
//                responseData.setMessage("Invalid Account Number");
//              
//                return new ResponseEntity<Object> (responseData,HttpStatus.BAD_REQUEST);
//        }catch (InternetBankingException e){
//
//            logger.info("Error getting account currency {} ", e);
//            responseData.setMessage(e.getMessage());
//          
//            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
//        }
//    }





//    @GetMapping(value = "/local/nameenquiry/{acctNum}")
//    public ResponseEntity<?> nameEnquiry (@PathVariable String acctNum){
//
//        try{
//
//            String name = transferUtils.doIntraBankkNameLookup(acctNum);
//            return new ResponseEntity<Object>(name, HttpStatus.OK);
//        }catch (InternetBankingException e){
//
//            logger.info("Error doing local name enquiry t{} ", e);
//            responseData.setMessage(e.getMessage());
//          
//            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
//        }
//    }
//
//
//    @GetMapping(value = "/inter/nameenquiry/{acctNum}/{bank}")
//    public ResponseEntity<?> interBankNameEnquiry (@PathVariable String acctNum, @PathVariable String bank){
//
//        try{
//
//            String name = transferUtils.doInterBankNameLookup(acctNum, bank);
//            return new ResponseEntity<Object>(name, HttpStatus.OK);
//        }catch (InternetBankingException e){
//
//            logger.info("Error doing inter bank name enquiry {} ", e);
//            responseData.setMessage(e.getMessage());
//          
//            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
//        }
//    }


}
