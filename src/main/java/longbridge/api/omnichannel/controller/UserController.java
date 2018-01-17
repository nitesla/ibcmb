package longbridge.api.omnichannel.controller;

import longbridge.api.omnichannel.dto.ApiError;
import longbridge.api.omnichannel.dto.CustomerInfo;
import longbridge.api.omnichannel.dto.RetailUserCredentials;
import longbridge.exception.InternetBankingException;
import longbridge.exception.UserNotFoundException;
import longbridge.exception.WrongPasswordException;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by Fortune on 12/18/2017.
 */

@RestController
public class UserController {

    @Autowired
    private RetailUserService retailUserService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(value = "/customer/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCustomerInfo(@RequestBody RetailUserCredentials retailUserCredentials) {

        try {
            logger.debug("Omni-channel: Request for customer info with Username {}", retailUserCredentials.getUsername());
            CustomerInfo customerInfo = retailUserService.getCustomerInfo(retailUserCredentials);
            logger.debug("Omni-channel: Response for customer info request: {}", customerInfo);
            return new ResponseEntity<>(customerInfo, new HttpHeaders(),HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            logger.error("Omni-channel: User could not be found", ex.getMessage());
            return new ResponseEntity<>(new ApiError(ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
        } catch (WrongPasswordException ex) {
            logger.error("Omni-channel: Incorrect password provided", ex.getMessage());
            return new ResponseEntity<>(new ApiError(ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);

        }
        catch (InternetBankingException ibe) {
            logger.error("Omni-channel: Error processing request", ibe.getMessage());
            return new ResponseEntity<>(new ApiError(ibe.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Omni-channel: Error processing request", e);
            return new ResponseEntity<>(new ApiError(e.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }
}
