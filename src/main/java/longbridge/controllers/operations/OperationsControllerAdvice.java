package longbridge.controllers.operations;

import longbridge.models.OperationsUser;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import longbridge.services.RequestService;
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
    MessageService  messageService;

    Logger logger = LoggerFactory.getLogger(this.getClass());



    @ModelAttribute
    public String globallAttributes(Model model, Principal principal){

        if(principal.getName()==null){
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

        return "";
    }

}
