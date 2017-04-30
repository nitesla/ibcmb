package longbridge.controllers.retail;


import longbridge.dtos.TransferRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {

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
    public String getOwnAccount(Model model) throws Exception{
        return "cust/transfer/ownaccounttransfers/view";
    }
    @GetMapping("/settransferlimit")
    public String getTransferLimit(Model model) throws Exception{
        return "cust/transfer/settransferlimit/view";
    }
}
