package longbridge.controllers.admin;

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
@ControllerAdvice(basePackages = {"longbridge.controllers.admin"})
public class AdmControllerAdvice {

    @Autowired
    VerificationService verificationService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @ModelAttribute
    public String globallAttributes(Model model, Principal principal){

        if(principal.getName()==null){
            return "redirect://login/admin";
        }


        int verificationNumber = verificationService.getTotalNumberForVerification();
        long totalPending = verificationService.getTotalNumberPending();
        if(totalPending>0) {
            model.addAttribute("totalPending", totalPending);
        }
        if(verificationNumber>0) {
            model.addAttribute("verificationNumber", verificationNumber);
        }

        return "";
    }

}