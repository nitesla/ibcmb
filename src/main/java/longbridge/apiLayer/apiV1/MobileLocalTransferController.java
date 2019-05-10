package longbridge.apiLayer.apiV1;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.*;
import longbridge.dtos.apidtos.MobileAccountDTO;
import longbridge.dtos.apidtos.MobileRetailBeneficiaryDTO;
import longbridge.exception.*;
import longbridge.models.*;
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

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.codehaus.groovy.tools.shell.util.Logger.io;

@RestController
@Api(value = "Retail Local Transfer", description = "Intra Bank Transfer / CMB Transfer", tags = {"Retail Local Transfer"})
@RequestMapping(value = "/api/v1/localtransfer")
public class MobileLocalTransferController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();

    private final Locale locale = LocaleContextHolder.getLocale();
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
    ConfigurationService configService;
    @Autowired
    TransferUtils transferUtils;
    @Autowired
    LocalBeneficiaryService localBeneficiaryService;
    @Autowired
    ModelMapper modelMapper;


    @GetMapping(value = "/localfinancialinst")
    public ResponseEntity<?> getDebitSourceAccount (){

        try{
            List <FinancialInstitutionDTO> financialInstitutionDTO = financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL);

            return new ResponseEntity<Object>(financialInstitutionDTO, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
          
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get Account")
    @GetMapping(value = "/nairaaccount")
    public ResponseEntity<?> getNairaAccount (){
        List<MobileAccountDTO> mobileAccountDTOS = new ArrayList<>();

        try{
            List<Account> accounts = transferUtils.getNairaAccounts();
//            accounts.forEach(i->{mobileAccountDTOS.add(modelMapper.map(i, MobileAccountDTO.class));});
            if (!accounts.isEmpty()) {
                responseData.setMessage(message);
                responseData.setData(accounts);
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

        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
          responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/fee")
    public ResponseEntity<?> getFee (){

        try{

            String fee = transferUtils.getFee("INTRABANK");
            responseData.setMessage(message);
            responseData.setData(fee);
            responseData.setCode("00");
            responseData.setError(false);
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Retail Local Beneficiary List")
    @GetMapping(value = "/beneficiary")
    public ResponseEntity<?> getBeneficiary (Principal principal){


        try{
            List<MobileRetailBeneficiaryDTO> mobileRetailBeneficiaryDTOS = new ArrayList<>();

            Iterable<LocalBeneficiary> cmbBeneficiaries = localBeneficiaryService.getBankBeneficiaries();
            logger.info("cmbBeneficiaries size {} ", cmbBeneficiaries);

            List<LocalBeneficiary> beneficiaries = StreamSupport.stream(cmbBeneficiaries.spliterator(), false)
                    .collect(Collectors.toList());
            beneficiaries.forEach(i ->
                    {
                        i.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank()).getInstitutionName());
                        if (i.getPreferredName() == null && i.getAccountName() != null) i.setPreferredName(i.getAccountName());
                    }
            );
            logger.info("beneficiary size {} ", beneficiaries.size());

            beneficiaries.forEach(i->{mobileRetailBeneficiaryDTOS.add(modelMapper.map(i, MobileRetailBeneficiaryDTO.class));});
            if (!mobileRetailBeneficiaryDTOS.isEmpty()) {
                responseData.setMessage(message);
                responseData.setData(mobileRetailBeneficiaryDTOS);
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage(messageSource.getMessage("05",null,locale));
            responseData.setData(null);
            responseData.setCode("05");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){

            logger.info("Error getting source account {} ", e);
            responseData.setMessage(e.getMessage());
            responseData.setError(false);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "CMB Transfer")
    @PostMapping(value = "/process")
    public ResponseEntity<?> intrabankTransfer (@RequestBody TransferRequestDTO transferRequestDTO, Principal principal, Locale locale) throws TransferException {
        String errorMessage;
        String token= transferRequestDTO.getToken();
        String msg;
        String error;

        RetailUser user = retailUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");

        if (setting != null && setting.isEnabled()) {
            if (token != null && !token.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), token);
                    if (!result) {
                        msg = messageSource.getMessage("token.auth.failure", null, locale);
                        responseData.setMessage(msg);
                        responseData.setData(null);
                        responseData.setError(true);
                        responseData.setCode("99");
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token");
                    error=se.getMessage();
                    responseData.setMessage(error);
                    responseData.setError(true);
                    responseData.setCode("99");
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseData.setMessage("Token Code is Required");
                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
        }

        try{

            if (transferRequestDTO.getAddBeneficiaryFlag().equalsIgnoreCase("Y")) {
                //Save the beneficiary
                LocalBeneficiaryDTO beneficiary = new LocalBeneficiaryDTO();
                try {
                    beneficiary.setBeneficiaryBank(bankCode);
                    beneficiary.setAccountName(transferRequestDTO.getBeneficiaryAccountName());
                    beneficiary.setAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
                    beneficiary.setPreferredName(transferRequestDTO.getBeneficiaryPrefferedName());
                    localBeneficiaryService.addLocalBeneficiaryMobileApi(beneficiary);
                } catch (InternetBankingException de) {
                    logger.error("Error occurred processing transfer",de);

                }

            }
            transferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
            try {

                transferUtils.validateTransferCriteria();
                transferService.validateTransfer(transferRequestDTO);

            } catch (InternetBankingTransferException e) {
                logger.error("Error making CMB transfer {} ", e);
                responseData.setMessage(e.getMessage());
                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }catch (Exception e) {
                logger.error("Error making transfer {} ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }


            transferRequestDTO = transferService.makeTransfer(transferRequestDTO);
            String status = transferRequestDTO.getStatusDescription();
            responseData.setMessage(status);
            responseData.setData(transferRequestDTO.getStatusDescription());
            responseData.setError(false);
            responseData.setCode(transferRequestDTO.getStatus());
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);
        } catch (InternetBankingTransferException e) {
            logger.error("Error making transfer", e);
            responseData.setMessage(transferErrorService.getMessage(e));
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);


        }
    }

}
