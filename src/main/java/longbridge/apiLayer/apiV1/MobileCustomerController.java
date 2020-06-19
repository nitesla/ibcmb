package longbridge.apiLayer.apiV1;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.apidtos.MobileCorporateUserDTO;
import longbridge.dtos.apidtos.MobileRetailUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.CorporateUserService;
import longbridge.services.RetailUserService;
import longbridge.services.SecurityService;
import longbridge.services.UserRetrievalService;
import longbridge.utils.Converters;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Api(value = "Customer Info", description = "Customer Information", tags = {"Customer Info"})
@RequestMapping(value = "/api/v1/customer")
public class MobileCustomerController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    ResponseData responseData = new ResponseData();
    String message ="Successful";

    @Autowired
    RetailUserService retailUserService;
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    UserRetrievalService userRetrievalService;
    @Autowired
    SecurityService securityService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
            Converters converters;

    ModelMapper mm = new ModelMapper();

    @ApiOperation(value = "Retail Information")
    @GetMapping(value = "/retail/info")
    public ResponseEntity<?> RetailInfo (Principal principal){

        try{
            RetailUser user = retailUserService.getUserByName(principal.getName());

            if (user !=null) {

                MobileRetailUserDTO mobileRetailUserDTO = modelMapper.map(user, MobileRetailUserDTO.class);
                responseData.setMessage(message);
                responseData.setData(mobileRetailUserDTO);
                responseData.setCode("00");
                responseData.setError(false);
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            }
            else
                responseData.setMessage("Invalid User");
            responseData.setError(true);
            responseData.setCode("99");
            responseData.setData(null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }catch (InternetBankingException e){
            logger.info("Retail user info Error",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            responseData.setData(null);
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "Corporate Information")
    @GetMapping(value = "/corp/info")
    public ResponseEntity<?> CorporateInfo (Principal principal){

        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
            if (corporateUser != null) {
                MobileCorporateUserDTO mobileCorporateUserDTO = modelMapper.map(corporateUser, MobileCorporateUserDTO.class);
                responseData.setMessage(message);
                responseData.setData(mobileCorporateUserDTO);
                responseData.setError(false);
                responseData.setCode("00");
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            } else
                responseData.setMessage("Invalid User");
            responseData.setData(null);
                 responseData.setError(true);
            responseData.setCode("99");
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

        }catch (InternetBankingException e){
            logger.info("Corporate User Error",e);
            responseData.setMessage(e.getMessage());
            responseData.setError(true);
            responseData.setCode("99");
            responseData.setData(null);
            return new ResponseEntity<>(responseData,HttpStatus.BAD_REQUEST);
        }

    }









}
