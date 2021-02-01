package longbridge.apiLayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.*;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/api/v1/owntransfer")
@Api(value = "Retail Own Transfer", description = "Retail Own Account Transfer", tags = {"Retail Own Transfer"})
public class MobileOwnTransferController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String error;
    String message = "Successful";

    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    AccountService accountService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    FinancialInstitutionService financialInstitutionService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    TransferService transferService;
    @Autowired
    SecurityService securityService;
    @Autowired
    TransferErrorService transferErrorService;
    @Autowired
    SettingsService configService;
    @Autowired
    TransferUtils transferUtils;

//22213677655

    @ApiOperation(value = "Retail Source Debit Account")
    @GetMapping(value = "/destinationaccount")
    public ResponseEntity<?> getDebitSourceAccount (Principal principal){

        try{
            logger.info("Retail user {} ", principal.getName());
            RetailUser user = retailUserService.getUserByName(principal.getName());

            if (user != null){
                List<String> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForCredit(user.getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)

                    .forEach(i -> accountList.add(i.getAccountNumber()));

            if (!accountList.isEmpty()) {
                responseData.setMessage(message);
                responseData.setData(accountList);
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else{
                responseData.setMessage("No Account");
                responseData.setData(accountList);
                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }

            }
            else
                responseData.setMessage("Invalid User ");
                responseData.setData(null);
                responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            error = e.getMessage();
            responseData.setMessage(error);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }

    }




    @ApiOperation(value = "Own Transfer")
    @PostMapping(value = "/process")
    public ResponseEntity <?> ownTransfer(@ApiParam(" Retail Transfer Request DTO")@RequestBody TransferRequestDTO transferRequestDTO, Principal principal, Locale locale) throws TransferException {

        String errorMessage;
        String tokenCode = transferRequestDTO.getToken();
        String response;

        RetailUser user = retailUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
        System.out.println("Entrust is Enabled"+setting.isEnabled());
        if (setting != null && setting.isEnabled()) {

            if (tokenCode != null && !tokenCode.isEmpty()) {
                System.out.println("tokenCode"+tokenCode);

                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                    if (!result) {
                        responseData.setMessage(messageSource.getMessage("token.auth.failure", null, locale));
                        responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token {} ", se);
                    error=se.getMessage();
                    responseData.setMessage(error);
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            }
        }

        try{


            transferRequestDTO.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
            transferRequestDTO.setBeneficiaryAccountName(accountService.getAccountByAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber()).getAccountName());

            try {

                transferUtils.validateTransferCriteria();
                transferService.validateTransfer(transferRequestDTO);

            } catch (Exception e) {
                logger.error("Error making transfer {} ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }

            transferRequestDTO = transferService.makeTransfer(transferRequestDTO);
            responseData.setMessage(transferRequestDTO.getStatusDescription());
            responseData.setData(transferRequestDTO.getStatusDescription());
            responseData.setError(false);
            responseData.setCode(transferRequestDTO.getStatus());
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);


        } catch (InternetBankingTransferException e) {
            logger.error("Error making transfer", e);
            errorMessage = transferErrorService.getMessage(e);
            responseData.setMessage(errorMessage);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);


        }

    }
}
