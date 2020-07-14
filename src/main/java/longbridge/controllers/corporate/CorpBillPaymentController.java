package longbridge.controllers.corporate;

import longbridge.dtos.BillerDTO;
import longbridge.models.Biller;
import longbridge.models.BillerCategory;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/corporate/billpayment")
public class CorpBillPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(CorpBillPaymentController.class);

    @Autowired
    private BillerRepo billerRepo;

    @Autowired
    private BillerCategoryRepo billerCategoryRepo;

    @GetMapping
    public String getRecurringBillPaymentPage() {
        return "corp/billpayment/pagei";
    }

    @GetMapping("/addbeneficiary")
    public String addBeneficiary(Model model, HttpServletRequest request){
        boolean enabled = true;
        List<BillerCategory> categorys = billerCategoryRepo.findAllByEnabled(enabled);
        model.addAttribute("categorys",categorys);
        return "corp/billpayment/addbeneficiary";
    }

    @PostMapping("/getBiller")
    public String beneficiaryBiller(String category, HttpServletRequest request, Model model){
        boolean enabled = true;
        logger.info("BILLER DTO {} " , category);
        List<Biller> biller = billerRepo.findAllByEnabledAndCategoryName(enabled,category);
        model.addAttribute("biller", biller);
        return "corp/billpayment/addbeneficiary";
    }


}
