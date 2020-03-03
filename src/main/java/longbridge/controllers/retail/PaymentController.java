package longbridge.controllers.retail;

import longbridge.dtos.BillPaymentDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Account;
import longbridge.models.Merchant;
import longbridge.models.Product;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.MerchantService;
import longbridge.services.PaymentService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import longbridge.utils.DataTablesUtils;
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

/**
 * Created by Fortune on 7/6/2018.
 */

@Controller
@RequestMapping("/retail/payment")
public class PaymentController {

    private final MerchantService merchantService;
    private final RetailUserService retailUserService;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final static Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    public PaymentController(MerchantService merchantService, RetailUserService retailUserService, AccountService accountService, PaymentService paymentService) {
        this.merchantService = merchantService;
        this.retailUserService = retailUserService;
        this.accountService = accountService;
        this.paymentService = paymentService;
    }

    @GetMapping("/new")
    public String getPaymentPage(Model model, Principal principal){

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<String> merchantCategories = merchantService.getMerchantCategories();
        model.addAttribute("merchantCategories",merchantCategories);
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
    public List<Merchant> getMerchants(@PathVariable String category){

        List<Merchant> merchantsByCategory = merchantService.getMerchantsByCategory(category);
        return merchantsByCategory;
    }


    @ResponseBody
    @GetMapping("/product/{productId}")
    public Product getProduct(@PathVariable Long productId){

        Product product = merchantService.getProduct(productId);
        return product;
    }


    @ResponseBody
    @GetMapping("/{merchantId}/products")
    public List<Product> getMerchantProducts(@PathVariable String merchantId){

        Merchant merchant = merchantService.getMerchant(Long.parseLong(merchantId));
        List<Product> products = merchant.getProducts();
        return products;
    }

    @ResponseBody
    @GetMapping("/{merchantId}/referencename")
    public String getOwnerReferenceName(@PathVariable String merchantId){

        Merchant merchant = merchantService.getMerchant(Long.parseLong(merchantId));
        return merchant.getOwnerReferenceName();
    }

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
