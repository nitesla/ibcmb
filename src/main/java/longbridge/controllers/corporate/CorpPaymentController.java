package longbridge.controllers.corporate;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.Biller;
import longbridge.models.CorporateUser;
import longbridge.models.PaymentItem;
import longbridge.services.AccountService;
import longbridge.services.BillerService;
import longbridge.services.CorporateUserService;
import longbridge.services.PaymentService;
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
@RequestMapping("/corporate/payment")
public class CorpPaymentController {

    private final BillerService billerService;
    private final CorporateUserService corporateUserService;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final static Logger logger = LoggerFactory.getLogger(CorpPaymentController.class);

    @Autowired
    public CorpPaymentController(BillerService billerService, CorporateUserService corporateUserService, AccountService accountService, PaymentService paymentService) {
        this.billerService = billerService;
        this.corporateUserService = corporateUserService;
        this.accountService = accountService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/new", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPaymentPage(Model model, Principal principal){

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<Biller> billerCategories = billerService.getBillersCategories();
        model.addAttribute("billerCategories",billerCategories);
        BillPaymentDTO paymentDTO = new BillPaymentDTO();
        paymentDTO.setPhoneNumber(corporateUser.getPhoneNumber());
        paymentDTO.setEmailAddress(corporateUser.getEmail());
        model.addAttribute("paymentDTO", paymentDTO);
        return "corp/payment/new";
    }

    @PostMapping("/new")
    public String addBillPayment(@ModelAttribute("paymentDTO") @Valid BillPaymentDTO paymentDTO, BindingResult result, RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            return "corp/payment/new";
        }

        try {
            String message = paymentService.addBillPayment(paymentDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corporate/payment/completed";
        }
        catch (InternetBankingException e){
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/corporate/payment/new";
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

        logger.info("Payment Item Id is {}", paymentItem.getPaymentItemId());
        logger.info("Debugging");

        List<PaymentItem> paymentItems = billerService.getPaymentItem(paymentItem.getPaymentItemName());
        return paymentItems;
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

        return "corp/payment/completed";
    }

    @GetMapping("/completed/all")
    public @ResponseBody
    DataTablesOutput<BillPaymentDTO> getTransfersCompleted(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<BillPaymentDTO> transferRequests = paymentService.getCorpPayments(pageable);

        DataTablesOutput<BillPaymentDTO> out = new DataTablesOutput<BillPaymentDTO>();
        out.setDraw(input.getDraw());
        out.setData(transferRequests.getContent());
        out.setRecordsFiltered(transferRequests.getTotalElements());
        out.setRecordsTotal(transferRequests.getTotalElements());

        return out;
    }




    @ModelAttribute
    public void getNairaSourceAccount(Model model, Principal principal) {

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        if (user != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCorporate().getAccounts());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                    .forEach(i -> accountList.add(i));
            model.addAttribute("accountList", accountList);


        }

    }
}
