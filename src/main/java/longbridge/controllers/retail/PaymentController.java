package longbridge.controllers.retail;

import longbridge.dtos.BillPaymentDTO;
import longbridge.dtos.QuicktellerRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.Account;
import longbridge.models.Biller;
import longbridge.models.PaymentItem;
import longbridge.models.RetailUser;
import longbridge.repositories.BillerRepo;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.StreamSupport;



@Controller
@RequestMapping("/retail/payment")
public class PaymentController {

    private final BillerService billerService;
    private final RetailUserService retailUserService;
    private MessageSource messages;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final String page = "cust/payment/";
    private SecurityService securityService;
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
        BillPaymentDTO billPaymentDTO = new BillPaymentDTO();
        billPaymentDTO.setPhoneNumber(retailUser.getPhoneNumber());
        billPaymentDTO.setEmailAddress(retailUser.getEmail());
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        return "cust/payment/new";
    }




    @PostMapping("/summary")
    public String paymentSummary(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest, PaymentItem paymentItemCode, Biller billerName, PaymentItem paymentItemName) {
        model.addAttribute("billPaymentDTO", billPaymentDTO);
//        if (servletRequest.getSession().getAttribute("add") != null)
//            servletRequest.getSession().removeAttribute("add");
        logger.info("content-->>>>> {}", billPaymentDTO);
        logger.info("Print---->{}", billPaymentDTO.getCustomerAccountNumber());

        billPaymentDTO.setCustomerAccountNumber(billPaymentDTO.getCustomerAccountNumber());
        billPaymentDTO.setCategoryName(billPaymentDTO.getCategoryName());
        billPaymentDTO.setBillerId(billPaymentDTO.getBillerId());
        billPaymentDTO.setPaymentItemId(billPaymentDTO.getPaymentItemId());
        billPaymentDTO.setAmount(billPaymentDTO.getAmount());
        billPaymentDTO.setPhoneNumber(billPaymentDTO.getPhoneNumber());
        billPaymentDTO.setEmailAddress(billPaymentDTO.getEmailAddress());
        paymentItemCode = billerService.getPaymentItem(Long.parseLong(billPaymentDTO.getPaymentItemId()));
        billPaymentDTO.setPaymentCode(paymentItemCode.getPaymentCode());
        logger.info("Payment Code is ------>>>>{}", paymentItemCode.getPaymentCode());
        billerName = billerService.getBillerName(Long.parseLong(billPaymentDTO.getBillerId()));
        billPaymentDTO.setBillerName(billerName.getBillerName());
        logger.info("Billlllerrrrr Name---->{}", billerName.getBillerName());
        paymentItemName = billerService.getPaymentItem(Long.parseLong(billPaymentDTO.getPaymentItemId()));
        billPaymentDTO.setPaymentItemName(paymentItemName.getPaymentItemName());
        logger.info("Payment Nameeee is ------>>>>{}", paymentItemName.getPaymentItemName());
        model.addAttribute("billPaymentDTO", billPaymentDTO);

        servletRequest.getSession().setAttribute("billPaymentDTO", billPaymentDTO);

//        if (servletRequest.getParameter("add") != null)
//            servletRequest.getSession().setAttribute("add", "add");
        return page + "summary";
    }

    @PostMapping("/edit")
    public String editPayment(@ModelAttribute("billPaymentDTO") BillPaymentDTO billPaymentDTO, Model model, HttpServletRequest request) {


        model.addAttribute("billPaymentDTO", billPaymentDTO);
        if (request.getSession().getAttribute("billPaymentDTO") != null) {
            BillPaymentDTO dto = (BillPaymentDTO) request.getSession().getAttribute("billPaymentDTO");
            model.addAttribute("billPaymentDTO", dto);
        }


        return page + "new";
    }

    @PostMapping("/process")
    public String billPayment(Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {
        BillPaymentDTO billPaymentDTO = (BillPaymentDTO) request.getSession().getAttribute("billPaymentDTO");
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        logger.info("hereeeeeeeeeeee {}", billPaymentDTO);
        logger.info("Print   222---->{}", billPaymentDTO.getCustomerAccountNumber());
        try {

            if (request.getSession().getAttribute("auth-needed") != null) {

                String token = request.getParameter("token");
                logger.info("gbemiiiiiiiiiii {}", token);
                if (token == null || token.isEmpty()) {
                    model.addAttribute("failure", "Token is required");
                    return "/cust/payment/summary";
                }


                try {
                    RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                    securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);

                } catch (InternetBankingSecurityException ibse) {
                    ibse.printStackTrace();
                    model.addAttribute("failure", ibse.getMessage());
                    return "/cust/payment/summary";
                }

                request.getSession().removeAttribute("auth-needed");
            }


            String message = paymentService.addBillPayment(billPaymentDTO);
            model.addAttribute("billPaymentDTO", billPaymentDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/retail/payment/completed";


        }catch (InternetBankingException e){
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/retail/payment/new";
        }



    }


    @PostMapping("/billpayment")
    public String addBillPayment(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO, BindingResult result,  HttpSession session, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request){

        if (result.hasErrors()) {
            model.addAttribute("billPaymentDTO", billPaymentDTO);
            return page + "new";
        }

        if (request.getSession().getAttribute("add") != null)
            request.getSession().removeAttribute("add");

        QuicktellerRequestDTO quicktellerRequestDTO = new QuicktellerRequestDTO();
        quicktellerRequestDTO.setCustomerAccountNumber(billPaymentDTO.getCustomerAccountNumber());
        quicktellerRequestDTO.setCategoryName(billPaymentDTO.getCategoryName());
        quicktellerRequestDTO.setBillerName(billPaymentDTO.getBillerName());
        quicktellerRequestDTO.setPaymentItemName(billPaymentDTO.getPaymentItemName());
        quicktellerRequestDTO.setAmount(billPaymentDTO.getAmount());
        quicktellerRequestDTO.setPhoneNumber(billPaymentDTO.getPhoneNumber());
        quicktellerRequestDTO.setEmailAddress(billPaymentDTO.getEmailAddress());
        model.addAttribute("quicktellerRequest", quicktellerRequestDTO);
        request.getSession().setAttribute("Bpayment", billPaymentDTO);
        model.addAttribute("payment", billPaymentDTO);
        if (request.getParameter("add") != null)
            request.getSession().setAttribute("add", "add");

        return page + "pagei";

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
    DataTablesOutput<BillPaymentDTO> getTransfersCompleted(DataTablesInput input, @RequestParam("csearch") String search){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<BillPaymentDTO> paymentRequests;

        if (StringUtils.isNoneBlank(search)) {

            paymentRequests = paymentService.getBillPayments(search.toUpperCase(), pageable);
        } else paymentRequests =  paymentService.getBillPayments(pageable);
        DataTablesOutput<BillPaymentDTO> out = new DataTablesOutput<BillPaymentDTO>();
        out.setDraw(input.getDraw());
        out.setData(paymentRequests.getContent());
        out.setRecordsFiltered(paymentRequests.getTotalElements());
        out.setRecordsTotal(paymentRequests.getTotalElements());

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
