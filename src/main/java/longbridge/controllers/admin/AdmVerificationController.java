package longbridge.controllers.admin;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import javax.validation.Valid;

//import longbridge.dtos.PendingDTO;
import longbridge.dtos.PendingVerification;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.utils.verificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.VerificationService;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/verifications")
public class AdmVerificationController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private MessageSource messageSource;



    @GetMapping("/")
    public String getVerifications(Model model) {

        return "adm/admin/verification/view";
    }


    @GetMapping("/{id}/verify")
    public String verifyOp(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            verificationService.verify(id);
            redirectAttributes.addFlashAttribute("message", "Operation approved successfully");
        }

        catch (VerificationException ve){
            logger.error("Error verifying the operation",ve);
            redirectAttributes.addFlashAttribute("failure", ve.getMessage());
        }
        catch (InternetBankingException ibe){
            logger.error("Error verifying the operation",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/admin/verifications/operations";
    }


    @PostMapping("/verify")
    public String verify(@ModelAttribute("verification") @Valid VerificationDTO verification,BindingResult result, WebRequest request, Model model, RedirectAttributes redirectAttributes,  Locale locale) {

        String approval = request.getParameter("approve");

        try {
            if ("true".equals(approval)) {
                verificationService.verify(verification);
                redirectAttributes.addFlashAttribute("message", "Operation approved successfully");

            } else if ("false".equals(approval)) {
                if (result.hasErrors())
                {
                     VerificationDTO verification2=verificationService.getVerification(verification.getId());
                    model.addAttribute("verify",verification2);
                    result.addError(new ObjectError("invalid", messageSource.getMessage("reason.required", null, locale)));
                    return "adm/makerchecker/details";
                }
                verificationService.decline(verification);
                redirectAttributes.addFlashAttribute("message", "Operation declined successfully");

            }
        }
        catch (VerificationException ve){
            logger.error("Error verifying the operation",ve);
            redirectAttributes.addFlashAttribute("failure", ve.getMessage());
        }
        catch (InternetBankingException ibe){
            logger.error("Error verifying the operation",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/verifications/operations";
    }


    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<VerificationDTO> getAllPending(DataTablesInput input)
    {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<VerificationDTO> page = verificationService.getPendingForUser(pageable);
        DataTablesOutput<VerificationDTO> out = new DataTablesOutput<VerificationDTO>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
        return out;
    }

    @GetMapping(path = "/allverification")
    public
    @ResponseBody
    DataTablesOutput<Verification> getAllVerification(DataTablesInput input)
    {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Verification> page = verificationService.getVerificationsForUser(pageable);
        DataTablesOutput<Verification> out = new DataTablesOutput<Verification>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
        return out;
    }


    @GetMapping("/{opId}/pending")
    public String getPendingOperation(@PathVariable Long opId, Model model) {

        VerificationDTO verificationDTO = verificationService.getVerification(opId);
        model.addAttribute("operation", verificationDTO.getOperation());
        return "/adm/makerchecker/operation";
    }

    @GetMapping(path = "/{operation}/all")
    public
    @ResponseBody
    DataTablesOutput<VerificationDTO> getPendingOperation(@PathVariable String operation, DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<VerificationDTO> page = verificationService.getPendingOperations(operation, pageable);
        DataTablesOutput<VerificationDTO> out = new DataTablesOutput<VerificationDTO>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
        return out;
    }


    @GetMapping("/verified")
    public String getVerifiedOperations() {
        return "adm/makerchecker/verified";
    }

    @GetMapping(path = "/verified/all")
    public
    @ResponseBody
    DataTablesOutput<VerificationDTO> getVerifiedOperations(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<VerificationDTO> page = verificationService.getVerifiedOPerations(pageable);
        DataTablesOutput<VerificationDTO> out = new DataTablesOutput<VerificationDTO>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
        return out;
    }


    @GetMapping("/pendingops")
    public String getPendingVerification(Model model)
    {

        return "adm/makerchecker/pending";
    }


    @GetMapping("/operations")
    public String getVerification(Model model)
    {

        return "adm/makerchecker/checker";
    }


    @GetMapping("/{id}/view")
    public String getObjectsForVerification(@PathVariable Long id, Model model) {

        VerificationDTO verification = verificationService.getVerification(id);
        model.addAttribute("verification",new VerificationDTO());
        model.addAttribute("verify", verification);
        return "adm/makerchecker/details";
    }

    @GetMapping("/{id}/pendingviews")
    public String getObjectsForPending(@PathVariable Long id, Model model) {

        VerificationDTO verification = verificationService.getVerification(id);
        model.addAttribute("verify", verification);
        return "adm/makerchecker/pendingdetails";
    }


}
