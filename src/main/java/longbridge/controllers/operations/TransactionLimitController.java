package longbridge.controllers.operations;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by SYLVESTER on 4/26/2017.
 */
@Controller
@RequestMapping("/ops/fees")
public class TransactionLimitController {

    @GetMapping
    public String getTransFees(Model model){
        return "ops/fees/view";
    }

    @GetMapping("/new")
    public String addTransactionType(Model model){
        return "ops/fees/add";

          }
}
