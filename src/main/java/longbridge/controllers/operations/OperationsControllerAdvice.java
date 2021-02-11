package longbridge.controllers.operations;

import longbridge.exception.AccountFetchException;
import longbridge.exception.IdentificationException;
import longbridge.models.OperationsUser;
import longbridge.servicerequests.client.RequestService;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import longbridge.services.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;

/**
 * Created by Fortune on 5/17/2017.
 */
@ControllerAdvice(basePackages = {"longbridge.controllers.operations"})
public class OperationsControllerAdvice {

    @Autowired
    private OperationsUserService operationsUserService;
    @Autowired
    private RequestService requestService;

    @Autowired
    private MessageService  messageService;

    @Autowired
    private VerificationService verificationService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @ModelAttribute
    public String globalAttributes(Model model, Principal principal){

        if(principal==null){
            return "redirect://login/ops";
        }
        OperationsUser operationsUser = operationsUserService.getUserByName(principal.getName());
        //Todo : Add fuctionality back
//        int numOfSubmittedRequests = requestService.getNumOfUnattendedRequests(operationsUser);
//        if(numOfSubmittedRequests>0) {
//            model.addAttribute("numOfSubmittedRequests",numOfSubmittedRequests);
//        }

        int numOfUnreadMessages = messageService.getNumOfUnreadMessages(operationsUser);
        if(numOfUnreadMessages>0){
            model.addAttribute("numOfUnreadMessages",numOfUnreadMessages);
        }

        int verificationNumber = verificationService.getTotalNumberForVerification();
        long totalPending = verificationService.getTotalNumberPending();
        if(totalPending>0) {
            model.addAttribute("totalPending", totalPending);
        }
        if(verificationNumber>0) {
            model.addAttribute("verificationNumber", verificationNumber);
        }

        model.addAttribute("pendingMessages",numOfUnreadMessages);
        model.addAttribute("pendingApprovals", verificationNumber);
        //Todo : Add fuctionality back
//        model.addAttribute("pendingRequests", numOfSubmittedRequests);

        return "";
    }



    @ExceptionHandler(IdentificationException.class)
    public ResponseEntity<?> handleIdentificationException(Exception ex, WebRequest webRequest){
        return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccountFetchException.class)
    public ResponseEntity<?> handleAccountFetchException(Exception ex, WebRequest webRequest){
        return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
