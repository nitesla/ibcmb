package longbridge.controllers.retail;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Showboy on 01/05/2017.
 */
@Controller
@RequestMapping("/retail/cheque")
public class ChequeController {

    @GetMapping("/confirm")
    public String getConfirmPage(){
        return "cust/cheque/confirm";
    }

    @GetMapping("/stop")
    public String getStopPage(){
        return "cust/cheque/stop";
    }
}
