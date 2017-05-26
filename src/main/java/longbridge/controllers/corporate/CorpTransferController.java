package longbridge.controllers.corporate;

import longbridge.dtos.CorporateDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.models.CorpTransferRequest;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.PendingAuthorization;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.CorporateService;
import longbridge.services.CorporateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    CorpTransferRequestRepo transferRequestRepo;

    @GetMapping("/{id}/{amount}")
    public void getQualifiedAuthorizers(@PathVariable Long id, @PathVariable String amount) {
        CorpTransferRequest transferRequest = new CorpTransferRequest();
        transferRequest.setAmount(new BigDecimal(amount));
        Corporate corporate = corporateRepo.findOne(id);
        transferRequest.setCorporate(corporate);
        List<CorporateUser> authorizers = corporateService.getQualifiedAuthorizers(transferRequest);
        PendingAuthorization pendingAuthorization = new PendingAuthorization();
        List<PendingAuthorization> pendingAuthorizations = new ArrayList<>();
        for (CorporateUser authorizer : authorizers) {
            pendingAuthorization.setAuthorizer(authorizer);
            pendingAuthorizations.add(pendingAuthorization);
        }
        transferRequest.setPendingAuthorizations(pendingAuthorizations);
        transferRequestRepo.save(transferRequest);
    }

    @GetMapping("/pending")
    public String getPendingTransfer(Principal principal,Model model){

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<PendingAuthorization> pendingAuthorizations=corporateUser.getPendingAuthorizations();
        model.addAttribute("pendingAuthorizations",pendingAuthorizations);
        return "corp/transfer/pendingtransfer/view";
    }
}
