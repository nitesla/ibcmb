//package longbridge.apiLayer.apiV1;
//
//
//import longbridge.exception.InternetBankingException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping(value = "/api/v1/fin")
//public class MobileFinancialInstitutionController {
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    public ResponseEntity<?> localFinacialInstitution(){
//
//        try{
//
//
//            return  new ResponseEntity(HttpStatus.OK);
//        }catch (InternetBankingException e){
//            logger.info("Local FInacial Institution Error {} ", e);
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//    }
//}
