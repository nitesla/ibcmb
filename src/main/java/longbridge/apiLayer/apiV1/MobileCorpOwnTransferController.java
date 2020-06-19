package longbridge.apiLayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
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
@Api(value = "Corporate Own Account Transfer", description = "Own Account Transfer", tags = {"orporate Own Account Transfer"})
@RequestMapping(value = "/api/v1/corp/owntransfer")
public class MobileCorpOwnTransferController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String errmsg;
    String message = "Successful";
    @Value("${bank.code}")
    private String bankCode;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    FinancialInstitutionService financialInstitutionService;
    @Autowired
    CorpTransferService corpTransferService;
    @Autowired
    ConfigurationService configService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    SecurityService securityService;
    @Autowired
    AccountService accountService;
    @Autowired
    IntegrationService integrationService;
    @Autowired
    TransferUtils transferUtils;
//RC 799170

    @ApiOperation(value = "Destination Account", tags = {"Corporate Own Account Transfer"})
    @GetMapping(value = "/destinationaccount")
    public ResponseEntity<?> getDebitSourceAccount (Principal principal){
        List<String> accountList = new ArrayList<>();
        try{
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            if (user !=null) {


                Iterable<Account> accounts = accountService.getAccountsForCredit(user.getCorporate().getCustomerId());
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
                    responseData.setData(null);
                    responseData.setError(true);
                    responseData.setCode("99");
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }

            }
            else
                responseData.setMessage("Invalid User");
            responseData.setData(null);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){
            logger.info("Error getting source account {} ", e);
            errmsg = e.getMessage();
            responseData.setMessage(errmsg);
            responseData.setData(null);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }

    }



    @ApiOperation(value = "Transfer", tags = {"Corporate Own Account Transfer"})
    @PostMapping(value = "/process")
    public ResponseEntity<?> ownAcctTransfer (@RequestBody CorpTransferRequestDTO transferRequestDTO, Principal principal, Locale locale) {

        String custtoken= transferRequestDTO.getToken();
        String msg;
        String error;

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if (setting != null && setting.isEnabled()) {
            if (custtoken != null && !custtoken.isEmpty()) {
                try {
                    boolean response = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), custtoken);
                    if (!response) {
                        msg = messageSource.getMessage("token.auth.failure", null, locale);
                        responseData.setMessage(msg);
                        responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token", se);
                    error=se.getMessage();
                    responseData.setMessage(error);
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            } /*else {
                responseData.setMessage("Token Code is Required");
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }*/
        }

        try{
            transferRequestDTO.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
            try {
                transferUtils.validateTransferCriteria();
                corpTransferService.validateTransfer(transferRequestDTO);

            } catch (InternetBankingTransferException e) {
                logger.error("Error initiating a transfer ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                logger.error("Error initiating a transfer ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }


            String corporateId = "" + corporateUserService.getUserByName(principal.getName()).getCorporate().getId();
            logger.info("corp id {} ", corporateId);
            transferRequestDTO.setCorporateId(corporateId);
            Object object = corpTransferService.addTransferRequest(transferRequestDTO);

            if (object instanceof CorpTransferRequestDTO) {
                transferRequestDTO = (CorpTransferRequestDTO) object;
                responseData.setMessage(transferRequestDTO.getStatusDescription());
                responseData.setData(transferRequestDTO.getStatusDescription());
                responseData.setCode(transferRequestDTO.getStatus());
                responseData.setError(false);
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            } else if (object instanceof String) {
                responseData.setMessage(object.toString());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
            else
              
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        } catch (InternetBankingTransferException e) {
            logger.error("Error initiating a transfer ", e);
            responseData.setMessage(messageSource.getMessage("transfer.error", null, locale));
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }
    }


}
