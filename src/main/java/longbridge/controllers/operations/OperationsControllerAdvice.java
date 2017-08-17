package longbridge.controllers.operations;

import longbridge.models.OperationsUser;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

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
        int numOfSubmittedRequests = requestService.getNumOfUnattendedRequests(operationsUser);
        if(numOfSubmittedRequests>0) {
            model.addAttribute("numOfSubmittedRequests",numOfSubmittedRequests);
        }

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
        model.addAttribute("pendingRequests", numOfSubmittedRequests);

        return "";
    }

}
