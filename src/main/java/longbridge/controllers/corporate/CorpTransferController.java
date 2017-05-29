package longbridge.controllers.corporate;


import longbridge.exception.InternetBankingException;
import longbridge.exception.InvalidAuthorizationException;
import longbridge.exception.TransferRuleException;
import longbridge.models.CorpTransRequest;


import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.PendAuth;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.CorpTransferService;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 5/22/2017.
 */

@Controller
@RequestMapping("/corporate/")
public class CorpTransferController {

    @Autowired
    CorporateService corporateService;

    @Autowired
    CorporateRepo corporateRepo;

    @Autowired
    private CorporateUserService corporateUserService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    CorpTransferRequestRepo transferRequestRepo;
    @Autowired private CorpTransferService corpTransferService;

    @GetMapping("/{id}/{amount}")
    public void getQualifiedAuthorizers(@PathVariable Long id, @PathVariable String amount) {

        CorpTransRequest transferRequest = new CorpTransRequest();
        transferRequest.setAmount(new BigDecimal(amount));
        Corporate corporate = corporateRepo.findOne(id);
        transferRequest.setCorporate(corporate);
        List<CorporateUser> authorizers = corporateService.getQualifiedAuthorizers(transferRequest);
        PendAuth pendAuth = new PendAuth();
        List<PendAuth> pendAuths = new ArrayList<>();
        for (CorporateUser authorizer : authorizers) {
            pendAuth.setAuthorizer(authorizer);
            pendAuths.add(pendAuth);
        }
        transferRequest.setPendAuths(pendAuths);
        transferRequestRepo.save(transferRequest);

    }

    @GetMapping("/pending")
    public String getPendingTransfer(Principal principal,Model model){

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<PendAuth> pendAuths =corporateUser.getPendAuths();
        model.addAttribute("pendAuths", pendAuths);
        return "corp/transfer/pendingtransfer/view";
    }
    @GetMapping("/{id}/authorize")
    public String authorizeTransfer(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes){
try {
    CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
    String message = corpTransferService.authorizeTransfer(corporateUser, id);
    redirectAttributes.addFlashAttribute("message", message);
}
catch (InvalidAuthorizationException iae){
    logger.error("Failed to authorize transfer",iae);
    redirectAttributes.addFlashAttribute("failure", iae.getMessage());

}
catch (TransferRuleException tre){
    logger.error("Failed to authorize transfer",tre);
    redirectAttributes.addFlashAttribute("failure", tre.getMessage());

}
catch (InternetBankingException e){
    logger.error("Failed to authorize transfer",e);
    redirectAttributes.addFlashAttribute("failure", e.getMessage());

}
        return "redirect:/corporate/pending";
    }
}
