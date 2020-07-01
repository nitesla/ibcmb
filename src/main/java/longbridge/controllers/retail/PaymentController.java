package longbridge.controllers.retail;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.Biller;
import longbridge.models.PaymentItem;
import longbridge.models.RetailUser;
import longbridge.repositories.BillerRepo;
import longbridge.services.AccountService;
import longbridge.services.BillerService;
import longbridge.services.PaymentService;
import longbridge.services.RetailUserService;
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
    @Autowired
    private BillerRepo billerRepo;
    private final static Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    public PaymentController(BillerService billerService, RetailUserService retailUserService, AccountService accountService, PaymentService paymentService) {
        this.billerService = billerService;
        this.retailUserService = retailUserService;
        this.accountService = accountService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/new", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPaymentPage(Model model, Principal principal){

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<Biller> billerCategories = billerService.getBillersCategories();
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
    @RequestMapping(value = "/biller", method = {RequestMethod.GET, RequestMethod.POST})

    public List<Biller> getBillers(Biller biller){

        logger.info("{}", biller.getCategoryName());
        logger.info("Debugging");

        List<Biller> billerByCategory = billerService.getBillersByCategory(biller.getCategoryName());
        logger.info("biller category =========== {}", billerByCategory);
        return billerByCategory;
    }


    @ResponseBody
    @RequestMapping(value = "/paymentItem", method = {RequestMethod.GET, RequestMethod.POST})
    public List<PaymentItem> getPaymentItem(PaymentItem paymentItem){

////        Biller biller = billerRepo.findOneById(paymentItem.getBillerId());
//        Long billerId = biller.getBillerId();
        logger.info("{}", paymentItem.getBillerId());
        logger.info("Debugging");

        List<PaymentItem> paymentItems = billerService.getPaymentItems(paymentItem.getBillerId());
        logger.info("payment item =========== {}", paymentItems);
        return paymentItems;
    }


    @ResponseBody
    @GetMapping("/paymentItem/{paymentItemId}")
    public PaymentItem getPaymentItem(@PathVariable Long paymentItemId){

        logger.info("{}", paymentItemId);
        logger.info("Debugging");

        PaymentItem paymentItem = billerService.getPaymentItem(paymentItemId);

        logger.info("paymentItem details are {}", paymentItem);
        return paymentItem;
    }


//    @ResponseBody
//    @GetMapping("/{billerId}/paymentItems")
//    public List<PaymentItem> getBillerPaymentItems(@PathVariable String billerId){
//
//        Biller biller = billerService.getBiller(Long.parseLong(billerId));
//        List<PaymentItem> paymentItems = biller.getPaymentItem();
//        return paymentItems;
//    }

//    @ResponseBody
//    @GetMapping("/{billerId}/referencename")
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
