package longbridge.controllers.operations;

import longbridge.InternetbankingApplication;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.models.Verification;
import longbridge.repositories.VerificationRepo;
import longbridge.services.AdminUserService;
import longbridge.services.OperationsUserService;
import longbridge.services.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

//import longbridge.dtos.PendingDTO;

@Controller
@RequestMapping("/ops/verifications")
public class OpsVerificationController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private VerificationRepo verificationRepo;

    @Autowired
    private OperationsUserService operationsUserService;

    @GetMapping("/")
    public String getVerifications(Model model) {

        return "ops/verification/view";
    }


    @GetMapping("/{id}/verify")
    public String verifyOp(@PathVariable Long id, RedirectAttributes redirectAttributes) {

       try {
           verificationService.verify(id);
           redirectAttributes.addFlashAttribute("message","Operation approved successfully");

       }

       catch (VerificationException ve){
           logger.error("Error verifying the operation",ve);
           redirectAttributes.addFlashAttribute("failure", ve.getMessage());
       }

       catch (InternetBankingException ibe){
           logger.error("Error verifying operation",ibe);
           redirectAttributes.addFlashAttribute("failure",ibe.getMessage());

       }
        return "redirect:/ops/verifications/operations";
    }


    @PostMapping("/verify")
    public String verify(@ModelAttribute("verification") VerificationDTO verification, WebRequest request, RedirectAttributes redirectAttributes) {

        String approval = request.getParameter("approve");

        try {
            if ("true".equals(approval)) {
                verificationService.verify(verification);
                redirectAttributes.addFlashAttribute("message", "Operation approved successfully");

            } else if ("false".equals(approval)) {
                if("".equals(verification.getComment()) ||verification.getComment()==null){
                    redirectAttributes.addFlashAttribute("failure", "Enter a reason for declining the operation");
                    return "redirect:/ops/verifications/"+verification.getId()+"/view";
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
            logger.error("Error verifying operation",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/ops/verifications/operations";
    }


    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<VerificationDTO> getAllPending(DataTablesInput input, Principal principal) {
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
    DataTablesOutput<Verification> getAllVerification(DataTablesInput input, Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Verification> verifications = verificationService.getVerificationsForUser(pageable);
        DataTablesOutput<Verification> out = new DataTablesOutput<Verification>();
        out.setDraw(input.getDraw());
        out.setData(verifications.getContent());
        out.setRecordsFiltered(verifications.getTotalElements());
        out.setRecordsTotal(verifications.getTotalElements());
        return out;
    }

    @GetMapping("/{opId}/pending")
    public String getPendingOperation(@PathVariable Long opId, Model model, Principal principal) {

        VerificationDTO verificationDTO = verificationService.getVerification(opId);
        model.addAttribute("operation", verificationDTO.getOperation());
        return "/ops/makerchecker/operation";
    }

    @GetMapping(path = "/{operation}/all")
    public
    @ResponseBody
    DataTablesOutput<VerificationDTO> getPendingOperation(@PathVariable String operation, DataTablesInput input, Principal principal) {
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
        return "/ops/makerchecker/verified";
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
    public String getPendingVerification(Model model, Principal principal) {
              return "ops/makerchecker/pending";
    }


    @GetMapping("/operations")
    public String getVerification(Model model, Principal principal) {
          return "ops/makerchecker/checker";
    }


    @GetMapping("/{id}/view")
    public String getObjectsForVerification(@PathVariable Long id, Model model, Principal principal) {

        VerificationDTO verification = verificationService.getVerification(id);
        model.addAttribute("verification", verification);
               return "ops/makerchecker/details";
    }

    @GetMapping("/{id}/pendingviews")
    public String getObjectsForPending(@PathVariable Long id, Model model, Principal principal) {

        VerificationDTO verification = verificationService.getVerification(id);
        model.addAttribute("beforeObject", verification.getBeforeObject());
        model.addAttribute("afterObject", verification.getAfterObject());
        return "ops/makerchecker/pendingdetails";
    }


}
