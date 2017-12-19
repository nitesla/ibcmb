package longbridge.api.omnichannel.controller;

import longbridge.api.omnichannel.dto.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by Fortune on 12/19/2017.
 */

@ControllerAdvice(basePackages = "longbridge.api.omnichannel.controller")
public class RestControllerAdvice {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, WebRequest webRequest){
        return new ResponseEntity<Object>(new ApiError(ex.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
