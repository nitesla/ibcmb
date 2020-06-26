package longbridge.apiLayer.apiV1;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.api.AccountDetails;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.apidtos.MobileAccountDTO;
import longbridge.dtos.apidtos.MobileAccountStatementDTO;
import longbridge.dtos.apidtos.MobileTransactionHistoryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.utils.TransferUtils;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionHistory;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/account")
@Api(value = "Retail Account", description = "Account Details", tags = {"Account"})
public class MobileAccountController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData =new ResponseData();
    String error;
    String message = "Successful";
    BigDecimal bal;




    @Autowired
    AccountService accountService;
    @Autowired
    IntegrationService integrationService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TransferUtils transferUtils;


    @ApiOperation(value = "List All Customer's Account", tags = {"Account"})
    @GetMapping(value = "/all")
    public ResponseEntity<?> getAccount (Principal principal){


        try{
            List<MobileAccountDTO> mobileAccountDTOS = new ArrayList<>();
            RetailUser user = retailUserService.getUserByName(principal.getName());
            if (user !=null) {
                Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());
                accounts.forEach(i->{
                    mobileAccountDTOS.add(modelMapper.map(i, MobileAccountDTO.class));
                    Map<String,BigDecimal>  balance= integrationService.getBalance(i.getAccountNumber());

                    for (MobileAccountDTO mobileAccountDTO:mobileAccountDTOS){
                        mobileAccountDTO.setAcctBalance(balance.get("AvailableBalance"));
                    }


                });
                logger.info("account size {} ", mobileAccountDTOS.size());

                if (!mobileAccountDTOS.isEmpty()) {
//                    for (MobileAccountDTO mobileAccountDTO:mobileAccountDTOS) {
//                       Map<String,BigDecimal>  balance= integrationService.getBalance(mobileAccountDTO.getAccountNumber());
//                        if (!balance.isEmpty()) {
//                            balance.get("AvailableBalance");
//                            logger.info("Acct balance {} ", balance.get("AvailableBalance"));
//                            mobileAccountDTO.setAcctBalance(balance.get("AvailableBalance"));
//                            responseData.setMessage(message);
//                            responseData.setData(mobileAccountDTO);
//                            responseData.setError(false);
//                            responseData.setCode("00");
//                        }
//                        else
//                            mobileAccountDTO.setAcctBalance(bal);
                        responseData.setMessage(message);
                        responseData.setData(mobileAccountDTOS);
                        responseData.setError(false);
                        responseData.setCode("00");
//                    }

                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                } else {

                    responseData.setMessage("Not Found");
                    responseData.setData(mobileAccountDTOS);
                   responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            }
            else
           
            responseData.setMessage("Invalid User");
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }


    @ApiOperation(value = "Customise Retail Account using Account Id", tags = {"Account"})
    @PostMapping(value = "/customiseaccount/{id}")
    public ResponseEntity<?> customiseRetailAccount (@ApiParam("Account Id") @PathVariable Long id,@RequestBody CustomizeAccount customizeAccount){

        try{
            logger.info("new name {}",customizeAccount.toString());

        String customiseAcct = accountService.customizeAccount(id,customizeAccount.getPreferredName());
        responseData.setMessage(customiseAcct);
        responseData.setData(null);
        responseData.setCode("00");
        responseData.setError(false);
        return  new ResponseEntity<Object>(responseData,HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Customise Accout error {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
           responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }



    @ApiOperation(value = "View specific Account", tags = {"Account"})
    @PostMapping(value = "/view/{id}")
    public ResponseEntity<?> viewAccounts (@ApiParam("Account Id") @PathVariable Long id){

        try{

            AccountDTO accountDTO = accountService.getAccount(id);
            AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());
            return  new ResponseEntity<Object>(account,HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("View  Accout Details error {} ", e);
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Account Statement", tags = {"Account"})
    @PostMapping(value = "/accountstatement")
    public ResponseEntity<?> viewAccountStatement (@RequestBody MobileAccountStatementDTO mobileAccountStatementDTO){
        Date from = null;
        Date to = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try{

//            List<MobileAccountStatementDTO> mobileAccountStatementDTOS = new ArrayList<>();
            logger.info("acct num {} ", mobileAccountStatementDTO.getAcctNum());
            String acctNum = mobileAccountStatementDTO.getAcctNum();
            String tranType = mobileAccountStatementDTO.getTranType();

            String fromDate = mobileAccountStatementDTO.getFromDate();
            String toDate = mobileAccountStatementDTO.getToDate();

            AccountStatement accountStatement = null;
            try {
                from = format.parse(fromDate);
                to = format.parse(toDate);
                logger.info("from date {} to date {} type {}",fromDate,toDate,tranType);

                accountStatement = integrationService.getFullAccountStatement(acctNum,from,to,tranType);
                MobileAccountStatementDTO mobileAccountStatementDTOS = modelMapper.map(accountStatement, MobileAccountStatementDTO.class);

                if ( mobileAccountStatementDTOS !=null) {
                    responseData.setMessage(message);
                    responseData.setData(mobileAccountStatementDTOS);
                    responseData.setCode("00");
                    responseData.setError(false);
                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                }
                else
                    responseData.setMessage("No Record");
                responseData.setData(mobileAccountStatementDTOS);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            } catch (ParseException e) {
                logger.info("Error getting account statement {} ", e);
                responseData.setMessage(e.getMessage());
                responseData.setData(null);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
            }
            catch (InternetBankingException e) {
                logger.info("Error getting account statement {} ", e);
                logger.info("Error getting account statement message {} ", e.getMessage());
                responseData.setMessage(e.getMessage());
                responseData.setData(null);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
            }

        }catch (InternetBankingException e){
            logger.info("Error getting account statement {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Last Ten Transaction", tags = {"Account"})
    @GetMapping(value = "/last10tran/{id}")
    public ResponseEntity<?> lastTenTransaction (@ApiParam ("Account Id") @PathVariable Long id){

        try{

            AccountDTO account = accountService.getAccount(id);
            if (account !=null) {
                String LAST_TEN_TRANSACTION = "10";
                List<MobileTransactionHistoryDTO> mobileTransactionHistoryDTOS = new ArrayList<>();

                try {
                    List<TransactionHistory> transactionHistoryList = integrationService.getLastNTransactions(account.getAccountNumber(), LAST_TEN_TRANSACTION);
                    transactionHistoryList.forEach(i->{mobileTransactionHistoryDTOS.add(modelMapper.map(i, MobileTransactionHistoryDTO.class));});
                    if (!mobileTransactionHistoryDTOS.isEmpty()) {

                        responseData.setMessage(message);
                        responseData.setData(mobileTransactionHistoryDTOS);
                        responseData.setCode("00");
                        responseData.setError(false);
                        return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                    } else {

                        responseData.setMessage("No Record");
                        responseData.setData(mobileTransactionHistoryDTOS);
                        responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

                    }
                }catch (InternetBankingException e){
                    logger.info("Error getting alast ten transaction {} ", e.getMessage());
                    responseData.setMessage(e.getMessage());
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

                }

            }
            else
                responseData.setMessage("Invalid Account");
            responseData.setCode("99");
               responseData.setError(true);
            responseData.setData(null);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);


        }catch (InternetBankingException e){
            logger.info("Error getting last ten transaction {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

}
