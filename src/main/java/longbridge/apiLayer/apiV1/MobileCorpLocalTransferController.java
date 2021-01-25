package longbridge.apiLayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import longbridge.apiLayer.data.ResponseData;
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
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
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
@RequestMapping(value = "/api/v1/corp/localtransfer")
@Api(description = "Intra Bank Transfer", value = "Corporate Local Transfer", tags = {"Corporate Local Transfer"})
public class MobileCorpLocalTransferController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String message = "Successful";

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
    @Autowired
    CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    @Autowired
    ModelMapper modelMapper;


    @ApiOperation( value = "Corporate Local Transfer Source Account", tags = {"Corporate Local Transfer"})
    @GetMapping(value = "/nairasource/account")
    public ResponseEntity<?> nairaSourceAccount (Principal principal) {

        try{
            List<MobileAccountDTO> mobileAccountDTOS = new ArrayList<>();
            CorporateUser user = corporateUserService.getUserByName(principal.getName());

            if (user !=null) {
                List<Account> accounts = transferUtils.getNairaAccounts(user.getCorporate().getId());
//                accounts.forEach(i->{mobileAccountDTOS.add(modelMapper.map(i, MobileAccountDTO.class));});
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
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
            else
                responseData.setMessage("Invalid User");
            responseData.setCode("99");
                responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        } catch (InternetBankingException e){
            logger.info("Error getting naira source acct {} ", e);
            responseData.setMessage(e.getMessage());
            
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "Charge Fee", tags = {"Corporate Local Transfer"})
    @GetMapping(value = "/fee")
    public ResponseEntity<?> getFee (){

        try{

            String fee = transferUtils.getFee("CMB");
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
            return new ResponseEntity<Object>(responseData,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "CMB Beneficiary List", tags = {"Corporate Local Transfer"})
    @GetMapping(value = "/beneficiary")
    public ResponseEntity<?> getBeneficiary (Principal principal){

        try{
            List<MobileCorpLocalBeneficiaryDTO>  mobileCorpLocalBeneficiaryDTOS =  new ArrayList<>();

            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = user.getCorporate();
            List<CorpLocalBeneficiary> beneficiaries = StreamSupport.stream(corpLocalBeneficiaryService.getCorpLocalBeneficiaries().spliterator(), false)
                    .filter(i -> i.getBeneficiaryBank().equalsIgnoreCase(bankCode))
                    .collect(Collectors.toList());

            beneficiaries = beneficiaries
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(i -> bankCode.equalsIgnoreCase(i.getBeneficiaryBank()))
                    .collect(Collectors.toList());
            logger.info("beneficiary size {} ", beneficiaries.size());

            beneficiaries.forEach(i-> mobileCorpLocalBeneficiaryDTOS.add(modelMapper.map(i, MobileCorpLocalBeneficiaryDTO.class)));
            if (!mobileCorpLocalBeneficiaryDTOS.isEmpty()) {
                responseData.setMessage(message);
                responseData.setData(mobileCorpLocalBeneficiaryDTOS);
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);
            }
            else
                //No beneficiary
                responseData.setMessage(messageSource.getMessage("No beneficiary", null, locale));
            responseData.setData(null);
            responseData.setCode("05");
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





    @ApiOperation(value = "Local transfer API", tags = {"Corporate Local Transfer"})
    @PostMapping(value = "/process")
    public ResponseEntity<?> intrabankTransfer (@RequestBody CorpTransferRequestDTO transferRequestDTO, Principal principal, Locale locale) {

        String token= transferRequestDTO.getToken();
        String msg;
        String error;

        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if (setting != null && setting.isEnabled()) {
            if (token != null && !token.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), token);
                    if (!result) {
                        msg = messageSource.getMessage("token.auth.failure", null, locale);
                        responseData.setMessage(msg);
                        responseData.setCode("99");
                        responseData.setError(true);
                        return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token");
                    error=se.getMessage();
                    responseData.setMessage(error);
                    responseData.setCode("99");
                    responseData.setError(true);
                    return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
                }
            }
        }

        try{

            if (transferRequestDTO.getAddBeneficiaryFlag().equalsIgnoreCase("Y")) {
                //Save the beneficiary
                    CorpLocalBeneficiaryDTO l = new CorpLocalBeneficiaryDTO();
                    try {
                        l.setBeneficiaryBank(bankCode);
                        l.setAccountName(transferRequestDTO.getBeneficiaryAccountName());
                        l.setAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
                        l.setPreferredName(transferRequestDTO.getBeneficiaryPrefferedName());

                        corpLocalBeneficiaryService.addCorpLocalBeneficiary(l);
                    } catch (InternetBankingException de) {
                        logger.error("Error occurred processing transfer",de);

                    }

            }

            transferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));

            try {
                transferUtils.validateTransferCriteria();
                corpTransferService.validateTransfer(transferRequestDTO);
            }
            catch (InternetBankingTransferException e) {
                logger.error("Error validating corp local transfer ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                responseData.setData(null);
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

            }
            catch (Exception e) {
                logger.error("Error validating  transfer ", e);
                responseData.setMessage(e.getMessage());
                responseData.setCode("99");
                responseData.setError(true);
                responseData.setData(null);
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
                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
            else {

                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
        } catch (InternetBankingTransferException e) {
            logger.error("Error initiating a transfer ", e);
            responseData.setMessage(messageSource.getMessage("transfer.error", null, locale));
            responseData.setCode("99");
            responseData.setError(true);
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);

        }
    }

}
