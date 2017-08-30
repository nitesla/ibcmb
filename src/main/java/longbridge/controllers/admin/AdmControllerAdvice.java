package longbridge.controllers.admin;

import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.PostConstruct;
import java.security.Principal;

/**
 * Created by Fortune on 5/17/2017.
 */
@ControllerAdvice(basePackages = {"longbridge.controllers.admin"})
public class AdmControllerAdvice {


    @Autowired
    VerificationService verificationService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

//    @PostConstruct


    @ModelAttribute
    public String globalAttributes(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login/admin";
        }

//        if ( getCurrentUser() != null && !getCurrentUser().getUserType().equals(UserType.ADMIN)) return "redirect:/login/admin";
        int verificationNumber = verificationService.getTotalNumberForVerification();
        long totalPending = verificationService.getTotalNumberPending();
        if (totalPending > 0) {
            model.addAttribute("totalPending", totalPending);
        }
        if (verificationNumber > 0) {
            model.addAttribute("verificationNumber", verificationNumber);
        }

        model.addAttribute("pendingApprovals", verificationNumber);

        return "";
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }

        return null;
    }
}
