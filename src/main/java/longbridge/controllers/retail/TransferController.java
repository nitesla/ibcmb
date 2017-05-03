package longbridge.controllers.retail;


import longbridge.dtos.AccountDTO;
import longbridge.dtos.RetailUserDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.models.TransferRequest;
import longbridge.models.User;
import longbridge.services.AccountService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {
    @Autowired
    AccountService accountService;
    @Autowired
    RetailUserService retailUserService;
    @Autowired
    TransferService transferService;

    @GetMapping
    public String getInternationalTransfer(Model model) throws Exception{
        return "cust/transfer/internationaltransfer/view";
    }

    @GetMapping("/coronationbanktransfer")
    public String getCoronationBankTransfer(Model model) throws Exception{
        return "cust/transfer/coronationbanktransfer/add";
    }
    @GetMapping("/interbanktransfer")
    public String getInterBank(Model model) throws Exception{
        return "cust/transfer/interbanktransfer/add";
    }
    @GetMapping("/ownaccounttransfer")
    public String getOwnAccount(Model model, TransferRequestDTO transferRequestDTO) throws Exception{
        RetailUserDTO user=retailUserService.getUser(2l);
        if(user!=null) {
            Iterable <AccountDTO> source = accountService.getAccounts(user.getCustomerId());
           Iterable <AccountDTO> destination = accountService.getAccounts(user.getCustomerId());
            /*System.out.println("the source: " + source);
            System.out.println("the destination: " + destination);*/
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
        }
            return "cust/transfer/ownaccounttransfers/view";

    }
    @PostMapping("/ownaccounttransfer")
    public String addOwnAccount(@ModelAttribute("transferRequestDTO") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception{
        RetailUserDTO user=retailUserService.getUser(2l);
        if(user!=null) {
            Account source = accountService.getAccountByAccountNumber(transferRequestDTO.getSource());
            System.out.println("the sourcePost"+source);
            Account destination = accountService.getAccountByAccountNumber(transferRequestDTO.getBeneficiaryAccountNumber());
            System.out.println("the destPost"+destination);
//            source.getCurrencyCode();
 //           destination.getCurrencyCode();
            if(!(source.getCurrencyCode().equals(destination.getCurrencyCode()))){
                model.addAttribute("differ","the currency are different");
            }
            transferService.saveTransfer(transferRequestDTO);
           /* System.out.println("the source: " + source);
            System.out.println("the destination: " + source);*/

            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
        }
        return "cust/transfer/ownaccounttransfers/view";
    }

    @GetMapping("/settransferlimit")
    public String getTransferLimit(Model model) throws Exception{
        return "cust/transfer/settransferlimit/view";
    }
}
