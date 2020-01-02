package longbridge.apiLayer.apiV1;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.dtos.apidtos.MobileRetailBeneficiaryDTO;
import longbridge.exception.*;
import longbridge.models.Account;
import longbridge.models.FinancialInstitution;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
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
@Api(value = "Retail InterBank Transfer", description = "InterBank Transfer (NIP)", tags = {"Retail InterBank Transfer"})
@RequestMapping(value = "/api/v1/interbank")
public class MobileInterBankTransferController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String message = "Successful";

    private final Locale locale = LocaleContextHolder.getLocale();

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
    ConfigurationService configService;
    @Autowired
    TransferUtils transferUtils;
    @Autowired
    LocalBeneficiaryService localBeneficiaryService;
    @Autowired
    ModelMapper modelMapper;



//transferUtils.getFee("NIP")

    @ApiOperation(value = "Get Transfer Charge")
    @GetMapping(value = "{tranAmount}/fee")
    public ResponseEntity<?> getTransFerFee(@PathVariable("tranAmount") String tranAmount){

        try{

            String fee =  transferUtils.getFee("NIP",tranAmount);
            responseData.setMessage(message);
            responseData.setData(fee);
            responseData.setCode("00");
            responseData.setError(false);
            return  new ResponseEntity<Object>(responseData,HttpStatus.OK);
        }catch (InternetBankingException e){
            logger.info("Transfer Limit error {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get Source Account(s)")
    @GetMapping(value = "/sourceaccount")
    public ResponseEntity<?> getDebitSourceAccount (Principal principal){

        try{
            RetailUser user = retailUserService.getUserByName(principal.getName());
            if (user != null) {

                List<String> accountList = new ArrayList<>();

                Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());

                StreamSupport.stream(accounts.spliterator(), false)
                        .filter(Objects::nonNull)
                        .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                        .forEach(i -> accountList.add(i.getAccountNumber()));

                if (!accountList.isEmpty()) {
                    responseData.setMessage(message);
                    responseData.setData(accountList);
                    responseData.setCode("00");
                    responseData.setError(false);
                    return new ResponseEntity<Object>(responseData, HttpStatus.OK);
                }
                else {
                    responseData.setMessage("No Account");
                    responseData.setData(null);
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            }
            else
                responseData.setMessage("Invalid User");
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setData(null);
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Retail Inter Bank Beneficiary List")
    @GetMapping(value = "/beneficiary")
    public ResponseEntity<?> getBeneficiary (){

        try{
            List<MobileRetailBeneficiaryDTO> mobileRetailBeneficiaryDTOS = new ArrayList<>();

            List<LocalBeneficiary> beneficiaries = StreamSupport.stream(localBeneficiaryService.getLocalBeneficiaries().spliterator(), false)
                    .filter(i -> !i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode()))
                    .collect(Collectors.toList());

            beneficiaries
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(i ->
                            {
                                FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank());

                                if (financialInstitution != null)
                                    i.setBeneficiaryBank(financialInstitution.getInstitutionCode());
                            }
                    );
            beneficiaries.forEach(i->{mobileRetailBeneficiaryDTOS.add(modelMapper.map(i, MobileRetailBeneficiaryDTO.class));});

            if (!mobileRetailBeneficiaryDTOS.isEmpty()) {
                responseData.setMessage(message);
                responseData.setData(mobileRetailBeneficiaryDTOS);
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else
                //No beneficiary
                responseData.setMessage(messageSource.getMessage("05",null,locale));
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


    @ApiOperation(value = "Retail Inter Transfer")
    @PostMapping(value = "/nip")
    public ResponseEntity<?> interbankTransfer (@RequestBody TransferRequestDTO transferRequestDTO, Principal principal, Locale locale) throws TransferException {
        String errorMessage;
        String tokenCode = transferRequestDTO.getToken();

        RetailUser user = retailUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");

        if (setting != null && setting.isEnabled()) {
            if (tokenCode != null && !tokenCode.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                    if (!result) {
                        responseData.setMessage(messageSource.getMessage("token.auth.failure", null, locale));
                       responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token for inter bank transfer {} ", se);
                    errorMessage=se.getMessage();
                    responseData.setMessage(errorMessage);
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseData.setMessage("Token Code is Required");
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
        }


        try{
            if (transferRequestDTO.getAddBeneficiaryFlag().equalsIgnoreCase("Y")) {
                //Save the beneficiary
                LocalBeneficiaryDTO beneficiary = new LocalBeneficiaryDTO();
                try {
                    beneficiary.setBeneficiaryBank(transferRequestDTO.getBeneficiaryBank());
                    beneficiary.setAccountName(transferRequestDTO.getBeneficiaryAccountName());
                    beneficiary.setAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
                    beneficiary.setPreferredName(transferRequestDTO.getBeneficiaryPrefferedName());

                    localBeneficiaryService.addLocalBeneficiary(beneficiary);
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
                transferService.validateTransfer(transferRequestDTO);

            } catch (InternetBankingTransferException e) {
                logger.error("Error making inter bank transfer {} ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }catch (Exception e) {
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
