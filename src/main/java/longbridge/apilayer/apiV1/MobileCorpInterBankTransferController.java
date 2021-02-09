package longbridge.apilayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.apilayer.data.ResponseData;
import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.apidtos.MobileAccountDTO;
import longbridge.dtos.apidtos.MobileCorpLocalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.Account;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitution;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@Api(value = "Corporate InterBank Transfer", description = "InterBank(NIP)", tags = {"Corporate InterBank Transfer"})
@RequestMapping(value = "/api/v1/corp/interbank")
public class MobileCorpInterBankTransferController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String message ="Successful";

    private final Locale locale = LocaleContextHolder.getLocale();

    @Value("${bank.code}")
    private String bankCode;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    FinancialInstitutionService financialInstitutionService;
    @Autowired
    CorpTransferService corpTransferService;
    @Autowired
    SettingsService configService;
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
    @Autowired
    CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    @Autowired
    ModelMapper modelMapper;




    @ApiOperation(value = "Get Charge", tags = {"Corporate InterBank Transfer"})
    @GetMapping(value = "/charge/{type}")
    public ResponseEntity<?> getCharge (@ApiParam("Enter Transfer Channel either NIP or RTGS") @PathVariable String type){

        try{
            if (type.equalsIgnoreCase("NIP")){
                String charge = integrationService.getFee(type).getFeeValue();
                responseData.setMessage(message);
                responseData.setData(charge);
                responseData.setError(false);
                responseData.setCode("00");
                return  new ResponseEntity<Object>(responseData, HttpStatus.OK);

            }
            else if (type.equalsIgnoreCase("RTGS")){
                String charge = integrationService.getFee(type).getFeeValue();
                responseData.setMessage(message);
                responseData.setData(charge);
                responseData.setCode("00");
                responseData.setError(false);
                return  new ResponseEntity<Object>(responseData, HttpStatus.OK);

            }
            else
                responseData.setMessage("INVALID CHANNEL");
            responseData.setCode("99");
                responseData.setError(true);
            return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){
            logger.info("Charge error {} ", e);
            responseData.setMessage(e.getLocalizedMessage());
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }

    }
    @ApiOperation(value = "Get Source Account", tags = {"Corporate InterBank Transfer"})
    @GetMapping(value = "/nairasource/account")
    public ResponseEntity<?> nairaSourceAccount (Principal principal){

        try{
            List<MobileAccountDTO> mobileAccountDTOS = new ArrayList<>();

            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            if (user != null) {
                List<Account> accountList = new ArrayList<>();

                Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCorporate().getAccounts());

                StreamSupport.stream(accounts.spliterator(), false)
                        .filter(Objects::nonNull)
                        .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))

                        .forEach(accountList::add);


                if (!accountList.isEmpty()) {
                    responseData.setMessage(message);
                    responseData.setData(accountList);
                    responseData.setCode("00");
                    responseData.setError(false);
                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                }
                else
                    responseData.setMessage("No Account");
                    responseData.setData(null);
                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
            else
                responseData.setMessage("Invalid User");
            responseData.setCode("99");
            responseData.setError(true);
            responseData.setData(null);
                return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){

            logger.info("Error getting naira source acct {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(true);
            responseData.setData(null);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Corporate Inter Bank Beneficiary List", tags = {"Corporate InterBank Transfer"})
    @GetMapping(value = "/beneficiary")
    public ResponseEntity<?> getBeneficiary (){

        try{

            List<MobileCorpLocalBeneficiaryDTO> mobileCorpLocalBeneficiaryDTOS = new ArrayList<>();
            List<CorpLocalBeneficiary> beneficiaries = StreamSupport.stream(corpLocalBeneficiaryService.getCorpLocalBeneficiaries().spliterator(), false)
                    .filter(i -> !i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode()))
                    .collect(Collectors.toList());

            beneficiaries
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(i ->
                            {
                                FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank());

                                if (financialInstitution != null)
                                    i.setBeneficiaryBank(financialInstitution.getInstitutionName());
                            }

                    );

            beneficiaries.forEach(i-> mobileCorpLocalBeneficiaryDTOS.add(modelMapper.map(i, MobileCorpLocalBeneficiaryDTO.class)));
            if (!mobileCorpLocalBeneficiaryDTOS.isEmpty()) {
                responseData.setMessage(message);
                responseData.setData(mobileCorpLocalBeneficiaryDTOS);
                responseData.setCode("00");
                responseData.setError(false);
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else
                //no beneficiary
                responseData.setMessage("No beneficiary");
            responseData.setData(null);
            responseData.setCode("05");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "NIP Transfer", tags = {"Corporate InterBank Transfer"})
    @PostMapping(value = "/nip")
    public ResponseEntity<?> interbankTransfer (@RequestBody CorpTransferRequestDTO transferRequestDTO, Principal principal, Locale locale) {
        String errorMessage;
        String failure;
        String token = transferRequestDTO.getToken();

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        logger.info("corporate user {} ", principal.getName());
        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if (setting != null && setting.isEnabled()) {
            if (token != null && !token.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
                    if (!result) {
                      String  msg = messageSource.getMessage("token.auth.failure", null, locale);
                        responseData.setMessage(msg);
                        responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token");
                    failure=se.getMessage();
                    responseData.setMessage(failure);
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

            if (transferRequestDTO.getAddBeneficiaryFlag().equalsIgnoreCase("Y")) {
                //Save the beneficiary
                CorpLocalBeneficiaryDTO beneficiary = new CorpLocalBeneficiaryDTO();
                try {
                    beneficiary.setBeneficiaryBank(transferRequestDTO.getBeneficiaryBank());
                    beneficiary.setAccountName(transferRequestDTO.getBeneficiaryAccountName());
                    beneficiary.setAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
                    beneficiary.setPreferredName(transferRequestDTO.getBeneficiaryPrefferedName());

                    corpLocalBeneficiaryService.addCorpLocalBeneficiary(beneficiary);
                } catch (InternetBankingException de) {
                    logger.error("Error occurred processing transfer",de);
                    responseData.setMessage(de.getMessage());
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

                }

            }

            FinancialInstitution institution = financialInstitutionService.getFinancialInstitutionByCode(transferRequestDTO.getBeneficiaryBank());
            if (institution ==null){
                responseData.setMessage(messageSource.getMessage(messageSource.getMessage("transfer.beneficiary.invalid", null, locale), null,locale));
                responseData.setCode("99");
                responseData.setError(true);
                return  new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }

            transferRequestDTO.setFinancialInstitution(institution);
            transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);

            try {
                transferUtils.validateTransferCriteria();
                corpTransferService.validateTransfer(transferRequestDTO);
            }  catch (InternetBankingTransferException e) {
                logger.error("Error validating  transfer ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
            logger.error("Error validating  transfer ", e);
            responseData.setMessage(e.getMessage());
                responseData.setCode("99");
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
                responseData.setError(false);
                responseData.setCode(transferRequestDTO.getStatus());
              return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            } else if (object instanceof String) {
                responseData.setMessage(object.toString());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
            else
                responseData.setMessage("");
            responseData.setCode("99");
            responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);


        }
        catch (InternetBankingTransferException e) {
            logger.error("Error initiating a transfer ", e);
         errorMessage =  messageSource.getMessage("transfer.error", null, locale);
            responseData.setMessage(errorMessage);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }
    }



}
