package longbridge.controllers.retail;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;



@Controller
@RequestMapping("/retail/payment")
public class PaymentController {

    private final BillerService billerService;
    private final RetailUserService retailUserService;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final static Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    public PaymentController(BillerService billerService, RetailUserService retailUserService, AccountService accountService, PaymentService paymentService) {
        this.billerService = billerService;
        this.retailUserService = retailUserService;
        this.accountService = accountService;
        this.paymentService = paymentService;
    }

    @GetMapping("/new")
    public String getPaymentPage(Model model, Principal principal){

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<String> billerCategories = billerService.getBillerCategories();
        model.addAttribute("billerCategories",billerCategories);
        BillPaymentDTO paymentDTO = new BillPaymentDTO();
        paymentDTO.setPhoneNumber(retailUser.getPhoneNumber());
        paymentDTO.setEmailAddress(retailUser.getEmail());
        model.addAttribute("paymentDTO", paymentDTO);
        return "cust/payment/new";
    }

    @PostMapping("/new")
    public String addBillPayment(@ModelAttribute("paymentDTO") @Valid BillPaymentDTO paymentDTO, BindingResult result, RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            return "cust/payment/new";
        }

        try {
            String message = paymentService.addBillPayment(paymentDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/retail/payment/completed";
        }
        catch (InternetBankingException e){
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/payment/new";
    }

    @ResponseBody
    @GetMapping("/{category}/merchants")
    public List<Biller> getBillers(@PathVariable String category){

        List<Biller> billersByCategory = billerService.getBillersByCategory(category);
        return billersByCategory;
    }


    @ResponseBody
    @GetMapping("/product/{productId}")
    public PaymentItem getPaymentItem(@PathVariable Long paymentItemId){

        PaymentItem paymentItem = billerService.getPaymentItem(paymentItemId);
        return paymentItem;
    }


    @ResponseBody
    @GetMapping("/{merchantId}/products")
    public List<PaymentItem> getBillerPaymentItems(@PathVariable String billerId){

        Biller biller = billerService.getBiller(Long.parseLong(billerId));
        List<PaymentItem> paymentItems = biller.getPaymentItems();
        return paymentItems;
    }

//    @ResponseBody
//    @GetMapping("/{merchantId}/referencename")
//    public String getOwnerReferenceName(@PathVariable String billerId){
//
//        Biller biller = billerService.getBiller(Long.parseLong(billerId));
//        return biller.getOwnerReferenceName();
//    }

    @GetMapping("/completed")
    public String getCompletedPayments(){

        return "cust/payment/completed";
    }

    @GetMapping("/completed/all")
    public @ResponseBody
    DataTablesOutput<BillPaymentDTO> getTransfersCompleted(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<BillPaymentDTO> transferRequests = paymentService.getBillPayments(pageable);

        DataTablesOutput<BillPaymentDTO> out = new DataTablesOutput<BillPaymentDTO>();
        out.setDraw(input.getDraw());
        out.setData(transferRequests.getContent());
        out.setRecordsFiltered(transferRequests.getTotalElements());
        out.setRecordsTotal(transferRequests.getTotalElements());

        return out;
    }




    @ModelAttribute
    public void getNairaSourceAccount(Model model, Principal principal) {

        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                    .forEach(i -> accountList.add(i));
            model.addAttribute("accountList", accountList);


        }

    }
}
