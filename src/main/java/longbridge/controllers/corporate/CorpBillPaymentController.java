package longbridge.controllers.corporate;

import longbridge.dtos.BillPaymentDTO;
import longbridge.dtos.BillerDTO;
import longbridge.models.Biller;
import longbridge.models.BillerCategory;
import longbridge.models.PaymentItem;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    @Autowired
    private PaymentItemRepo paymentItemRepo;

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

    @ResponseBody
    @PostMapping("/getBiller")
    public List<Biller> beneficiaryBiller(String category, HttpServletRequest request, Model model){
        boolean enabled = true;
        logger.info("BILLER DTO {} " , category);
        List<Biller> biller = billerRepo.findAllByEnabledAndCategoryName(enabled,category);
        return biller;
    }

    @ResponseBody
    @PostMapping("/getpaymentitems")
    public List<PaymentItem> getPaymentItems(Long billerId){
        logger.info("billerId {} " , billerId);
        boolean enabled = true;
        List<PaymentItem> items =  paymentItemRepo.findAllByEnabledAndBillerId(enabled, billerId);
        logger.info("items {} " , items);
        return items;

    }

    @PostMapping("/addnewbeneficiary")
    public String addNewBeneficiary(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO){
        logger.info("DETAILS {} " , billPaymentDTO);
        return "SUCCESSFULL";
    }

}
