package longbridge.apiLayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.*;
import longbridge.forms.CustomizeAccount;
import longbridge.models.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;


@RestController
@Api(value = "Retail Transfer", description = "Retail Transfer Info")
@RequestMapping(value = "/api/v1/transfer")
public class MobileTransferController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Locale locale = LocaleContextHolder.getLocale();
    private  ResponseData responseData = new ResponseData();
    @Autowired
    TransferUtils transferUtils;
    @Autowired
    AccountService accountService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    SecurityService securityService;
    @Autowired
    TransferService transferService;
    @Autowired
    TransferErrorService transferErrorService;
    @Autowired
    FinancialInstitutionService financialInstitutionService;
    @Autowired
    MessageSource message;
    @Autowired
    UserRetrievalService userRetrievalService;


    @ApiOperation(value = "Retail Transfer Limit")
    @GetMapping(value = "/limit/{acctNumber}/{channel}")
    public ResponseEntity<?> getTransFerLimit(@ApiParam("Account Number") @PathVariable String acctNumber, @ApiParam("Transfer Channel, enter (NIP for Interbank Transfer),(INTRABANK for CMB Transfer)") @PathVariable String channel){

        try{

           String limit =  transferUtils.getLimit(acctNumber, channel);
           responseData.setMessage("Successful");
           responseData.setData(limit);
          responseData.setCode("00");
           responseData.setError(false);
            return  new ResponseEntity<Object>(responseData,HttpStatus.OK);
        }catch (InternetBankingException e){
            logger.info("Transfer Limit error {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(false);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping(value = "/{accountId}/currency")
    public ResponseEntity<?> getAccountCurrency (@PathVariable String accountId){

        try{

            String accountCurrency = accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
            return  new ResponseEntity<Object>(accountCurrency,HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Account currency error {} ", e);
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{accountNo}/intrabank/nameenquiry")
    public ResponseEntity<?> intraBankNameEnquiry (@PathVariable String accountNo){

        try{

            String intraBankName = transferUtils.doIntraBankkNameLookup(accountNo);
            return new ResponseEntity<Object>(intraBankName, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting intra bank account name {} ", e);
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/validatebvn", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> validateBVN (){

            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (!(authentication instanceof AnonymousAuthenticationToken)) {
                    CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();

                    if(currentUser == null){
                        responseData.setMessage(message.getMessage(message.getMessage("user.invalid", null, locale), null,locale));
                        responseData.setError(true);
                        return  new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

                    }

                        RetailUser retailUser = retailUserService.getUserByName(currentUser.getUsername());

                        if (retailUser.getBvn() == null || "NO BVN".equalsIgnoreCase(retailUser.getBvn()) || retailUser.getBvn().isEmpty()) {
                            responseData.setMessage(TransferExceptions.NO_BVN.toString());
                            responseData.setError(true);
                           

                            return  new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
//
                        }
                        else
                            responseData.setMessage("BVN is availabe");
                        responseData.setError(false);
                    return  new ResponseEntity<Object>(responseData, HttpStatus.OK);


                }
            } catch (InternetBankingTransferException e) {
                responseData.setMessage(e.getMessage());
               responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
        responseData.setMessage(TransferExceptions.NO_BVN.toString());
       
        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }



    @GetMapping(value = "/{accountNo}/{bank}/interbank/nameenquiry")
    public ResponseEntity<?> interBankNameEnquiry (@PathVariable String accountNo, String bank){

        try{

            String interBankName = transferUtils.doInterBankNameLookup(bank,accountNo);
            responseData.setMessage(interBankName);
           responseData.setError(false);
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting inter bank account name {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
           
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/balance/{accountNo}")
    public ResponseEntity<?> getAcctBalance (@PathVariable String accountNo){

        try{

            String acctBalance = transferUtils.getBalance(accountNo);
            return new ResponseEntity<Object>(acctBalance, HttpStatus.OK);

        }catch (InternetBankingException e){

            logger.info("Error getting account balance {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
           
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
    }


    @GetMapping(value = "/bvn")
    public ResponseEntity<?> getBvn (){

        try{
         String bvn = userRetrievalService.getBvn();
         responseData.setMessage(bvn);
            return new ResponseEntity<Object>( responseData, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting  bvn {} ", e);
            responseData.setMessage(e.getMessage());
           
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }
    }


}
