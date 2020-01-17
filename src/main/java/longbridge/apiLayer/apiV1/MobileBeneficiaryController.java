package longbridge.apiLayer.apiV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.apidtos.MobileCorpLocalBeneficiaryDTO;
import longbridge.dtos.apidtos.MobileRetailBeneficiaryDTO;
import longbridge.dtos.apidtos.MobileRetailUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import org.modelmapper.ModelMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@RestController
@Api(value = "Beneficiary Management", description = "Beneficiary Management", tags = {"Beneficiary Management"})
@RequestMapping(value = "/api/v1/beneficiary")
public class MobileBeneficiaryController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    @Autowired
    CorpInternationalBeneficiaryService corpInternationalBeneficiaryService;
    @Autowired
    LocalBeneficiaryService localBeneficiaryService;
    @Autowired
    FinancialInstitutionService financialInstitutionService;
    @Autowired
    ConfigurationService configService;
    @Autowired
    SecurityService securityService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    private ModelMapper modelMapper;



    @ApiOperation(value = "Add Local Corporate Beneficiary", tags = {"Beneficiary Management"})
    @PostMapping(value = "/corp/local/add",produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<?> CorporateLocalBeneficiary (@RequestBody CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, Principal principal, Locale locale) {
        String failure;
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        getCurrentUser().setEmailTemplate("mail/beneficiaryMobile.html");

        try {

            String message = corpLocalBeneficiaryService.addCorpLocalBeneficiary(corpLocalBeneficiaryDTO);
            responseData.setMessage(message);
            responseData.setError(false);
            responseData.setCode("00");
            return new ResponseEntity<Object>(responseData, HttpStatus.OK);

        } catch (InternetBankingException e) {

            String msg = messageSource.getMessage("beneficiary.add.failure", null, locale);
            responseData.setMessage(msg);
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
        }


    }


    @ApiOperation(value = "Delete Local Corporate Beneficiary", tags = {"Beneficiary Management"})
    @PostMapping(value = "/corp/local/delete")
    public ResponseEntity<?> DeleteCorpLocalBeneficiary (@RequestBody CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO) {

        String failure;
        String token = corpLocalBeneficiaryDTO.getToken();
        logger.info("am here 2"+token);


              try {

                  String message = corpLocalBeneficiaryService.deleteCorpLocalBeneficiary(corpLocalBeneficiaryDTO.getId());
                  responseData.setMessage(message);
                  responseData.setError(false);
                  responseData.setData(null);
                  responseData.setCode("00");
                  return new ResponseEntity<>(responseData, HttpStatus.OK);
              } catch (InternetBankingException e) {
                  logger.error("Beneficiary Error", e);
                  failure = e.getMessage();
                  responseData.setMessage(failure);
                  responseData.setData(null);
                  responseData.setError(true);
                  responseData.setCode("99");
                  return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
              }

          }



    @ApiOperation(value = "View  Specific Local Corporate Beneficiary", tags = {"Beneficiary Management"})
    @GetMapping(value = "/corp/local/view/{beneficiaryId}")
    public ResponseEntity<?> viewCorpLocalBeneficiary (@ApiParam("Beneficiary Id") @PathVariable Long beneficiaryId){

        String msg = "Successful";
        try{
            CorpLocalBeneficiary viewLocalBeneficiary =corpLocalBeneficiaryService.getCorpLocalBeneficiary(beneficiaryId);
            MobileCorpLocalBeneficiaryDTO mobileCorpLocalBeneficiaryDTO = modelMapper.map(viewLocalBeneficiary, MobileCorpLocalBeneficiaryDTO.class);
            responseData.setData(mobileCorpLocalBeneficiaryDTO);
            responseData.setError(false);
            responseData.setMessage(msg);
            return new ResponseEntity<>(viewLocalBeneficiary, HttpStatus.OK);
        }catch (InternetBankingException e){
            logger.info("view  Corporate Beneficiary Error",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "View All Local Corporate Beneficiary", tags = {"Beneficiary Management"})
    @GetMapping(value = "/corp/local/view/all")
    public ResponseEntity<?> viewAllCorpLocalBeneficiary (){
        String msg = "Successful";


        try{
            List<MobileCorpLocalBeneficiaryDTO> mobileCorpLocalBeneficiaryDTOs = new ArrayList<>();

            Iterable<CorpLocalBeneficiary> viewLocalBeneficiary =corpLocalBeneficiaryService.getCorpLocalBeneficiaries();
            viewLocalBeneficiary.forEach(i->{mobileCorpLocalBeneficiaryDTOs.add(modelMapper.map(i, MobileCorpLocalBeneficiaryDTO.class));});
            logger.info("Mobile corp {} ",mobileCorpLocalBeneficiaryDTOs.size());

            if (!mobileCorpLocalBeneficiaryDTOs.isEmpty()){
                responseData.setMessage(msg);
                responseData.setError(false);
                responseData.setCode("00");
                responseData.setData(mobileCorpLocalBeneficiaryDTOs);
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else{
                responseData.setMessage("No beneficiary");
                responseData.setError(true);
                responseData.setCode("05");
                responseData.setData(mobileCorpLocalBeneficiaryDTOs);
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
        }catch (InternetBankingException e){
            logger.info("View all  Corporate Beneficiary Error",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            logger.info("View all  Corporate Beneficiary Error 2 {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }


    @ApiOperation(value = "Add Local Retail Beneficiary", tags = {"Beneficiary Management"})
    @PostMapping(value = "/retail/local/add", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> RetailAddLocalBeneficiary (@RequestBody LocalBeneficiaryDTO localBeneficiaryDTO, Principal principal){

        String failure;

        try {
            String responseMessage = localBeneficiaryService.addLocalBeneficiaryMobileApi(localBeneficiaryDTO);
            responseData.setMessage(responseMessage);
            responseData.setError(false);
            responseData.setCode("00");
            return  new ResponseEntity<Object>(responseData,HttpStatus.OK);
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred",ibe);
            failure = ibe.getMessage();
            responseData.setMessage(failure);
            responseData.setError(true);
            responseData.setCode("99");
            return  new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);

        }

    }

    @ApiOperation(value = "Delete Local Retail Beneficiary", tags = {"Beneficiary Management"})
    @PostMapping(value = "/retail/local/delete")
    public ResponseEntity<?> RetailDeleteLocalBeneficiary (@RequestBody LocalBeneficiaryDTO localBeneficiaryDTO){

        String failure;
            try {

                String message = localBeneficiaryService.deleteLocalBeneficiary(localBeneficiaryDTO.getId());
                responseData.setMessage(message);
                responseData.setData(null);
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            } catch (InternetBankingException e) {
                logger.error("Beneficiary Error", e);
                failure = e.getMessage();
                responseData.setMessage(failure);
                responseData.setData(null);
                responseData.setError(true);
                responseData.setCode("99");
                return new ResponseEntity<Object>(responseData, HttpStatus.BAD_REQUEST);
            }
        }




    @ApiOperation(value = "View specific Local Retail Beneficiary", tags = {"Beneficiary Management"})
    @GetMapping(value = "/retail/local/view/{beneficiaryId}")
    public ResponseEntity<?> viewRetailLocalBeneficiary (@ApiParam("Beneficiary Id") @PathVariable Long beneficiaryId){


        try{
            LocalBeneficiary viewLocalBeneficiary =localBeneficiaryService.getLocalBeneficiary(beneficiaryId);
            if (viewLocalBeneficiary !=null) {
                responseData.setMessage(viewLocalBeneficiary.toString());
                responseData.setError(false);
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else {
                responseData.setMessage(viewLocalBeneficiary.toString());
                responseData.setError(true);
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
        }catch (InternetBankingException e){
            logger.info("view  Retail Beneficiary Error",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "View All Local Retail Beneficiary", tags = {"Beneficiary Management"})
    @GetMapping(value = "/retail/local/view/all")
    public ResponseEntity<?> viewAllRetailLocalBeneficiary (){


        try{
            List<MobileRetailBeneficiaryDTO> mobileRetailBeneficiaryDTOS = new ArrayList<>();

            Iterable<LocalBeneficiary> viewLocalBeneficiary =localBeneficiaryService.getLocalBeneficiaries();
            viewLocalBeneficiary.forEach(i->{mobileRetailBeneficiaryDTOS.add(modelMapper.map(i, MobileRetailBeneficiaryDTO.class));});
            logger.info("Mobile retail beneficiary {} ",mobileRetailBeneficiaryDTOS.size());
            if (!mobileRetailBeneficiaryDTOS.isEmpty()){
                responseData.setMessage("Successful");
                responseData.setError(false);
                responseData.setCode("00");
                responseData.setData(mobileRetailBeneficiaryDTOS);
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else{
                logger.info("am here");
                responseData.setMessage(messageSource.getMessage("05", null, locale));
                responseData.setError(true);
                responseData.setCode("05");
                responseData.setData(mobileRetailBeneficiaryDTOS);
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
        }catch (InternetBankingException e){
            logger.info("View all  Retail Beneficiary Error 1 {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            logger.info("View all  Retail Beneficiary Error 2 {} ",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }

    private CorporateUser getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal userPrincipal =(CustomUserPrincipal) authentication.getPrincipal();
            return (CorporateUser)userPrincipal.getUser();
        }

        return (null) ;


    }


}
