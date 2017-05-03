package longbridge.controllers.retail;


import longbridge.api.AccountInfo;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.RetailUser;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.ws.http.HTTPException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {


private RetailUserService retailUserService;
private IntegrationService integrationService;
private TransferService transferService;

@Autowired
    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
    }

    @GetMapping
    public String getInternationalTransfer( Principal principal) throws Exception{
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
    @GetMapping("/ownaccount")
    public ModelAndView getOwnAccount(Principal principal) throws Exception{

        RetailUser  user = retailUserService.getUserByName(principal.getName());
        if(user!=null){
            List<String>  accountList = new ArrayList<>();

                integrationService.fetchAccounts(user.getCustomerId())

                    .stream()
                    .forEach(i-> accountList.add(i.getAccountNumber()));


            ModelAndView view  = new ModelAndView();
            view.addObject("accounts",accountList);
            view.setViewName( "cust/transfer/ownaccounttransfers/view");
            return view;
        }


        throw new IllegalAccessException("");



    }
    @GetMapping("/settransferlimit")
    public String getTransferLimit(Model model) throws Exception{
        return "cust/transfer/settransferlimit/view";
    }
}
