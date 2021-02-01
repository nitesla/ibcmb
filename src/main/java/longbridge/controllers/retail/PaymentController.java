package longbridge.controllers.retail;

import longbridge.dtos.BillPaymentDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
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
    SettingsService configurationService;
    @Autowired
    private MessageSource messageSource;
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
        List<BillerCategory> billerCategories = billerService.getCategory();
        model.addAttribute("billerCategories",billerCategories);
        BillPaymentDTO billPaymentDTO = new BillPaymentDTO();
        billPaymentDTO.setPhoneNumber(retailUser.getPhoneNumber());
        billPaymentDTO.setEmailAddress(retailUser.getEmail());
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        return "cust/payment/new";
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String savePayment(@PathVariable Long id, Model model, HttpServletRequest request, Locale locale, RedirectAttributes attributes) {
        BillPayment billPayment = paymentService.getBillPayment(id);
//        PaymentItem paymentItem = billerService.getPaymentItemAmount(billPayment.getPaymentItemName());
        List<Biller> billerCategories = billerService.getBillersCategories();
        model.addAttribute("billerCategories", billPayment);
        BillPaymentDTO billPaymentDTO = new BillPaymentDTO();
        billPaymentDTO.setPhoneNumber(billPayment.getPhoneNumber());
        billPaymentDTO.setEmailAddress(billPayment.getEmailAddress());
        billPaymentDTO.setPaymentCode(billPayment.getPaymentCode());
        billPaymentDTO.setCategoryName(billPayment.getCategoryName());
        billPaymentDTO.setBillerName(billPayment.getBillerName());
        billPaymentDTO.setPaymentItemName(billPayment.getPaymentItemName());
        billPaymentDTO.setAmount(billPayment.getAmount().toString());
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        request.getSession().setAttribute("billPaymentDTO", billPaymentDTO);
        return page + "pagei";
    }

    @PostMapping("/summary")
    public String paymentSummary(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest, PaymentItem paymentItemCode) {
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        Biller billerName = billerService.getBillerName(Long.parseLong(billPaymentDTO.getBillerId()));
        billPaymentDTO.setBillerName(billerName.getBillerName());
        PaymentItem paymentItemName = billerService.getPaymentItem(Long.parseLong(billPaymentDTO.getPaymentItemId()));
        billPaymentDTO.setPaymentItemName(paymentItemName.getPaymentItemName());
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        servletRequest.getSession().setAttribute("billPaymentDTO", billPaymentDTO);
        return page + "summary";
    }

    @PostMapping("/highlight")
    public String savedPaymentSummary(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest, PaymentItem paymentItemCode, Biller billerName, PaymentItem paymentItemName) {
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        servletRequest.getSession().setAttribute("billPaymentDTO", billPaymentDTO);
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

    @PostMapping
    public String createBillPayment(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO, Principal principal, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "cust/payment/new";
        }

        logger.info("Bill payment request  {}", billPaymentDTO);
        logger.info("auth {}", session.getAttribute("authenticated"));
        SettingDTO setting = configurationService.getSettingByName("ENABLE_RETAIL_2FA");
        logger.info("setg {}",setting);
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", billPaymentDTO);
            session.setAttribute("redirectURL", "/retail/payment/process");
            return "redirect:/retail/token/authenticate";
        }

        return "redirect:/retail/payment/summary";
    }

    @GetMapping("/process")
    public String billPayment(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        if (session.getAttribute("requestDTO") != null) {
            BillPaymentDTO billPaymentDTO = (BillPaymentDTO) session.getAttribute("requestDTO");

            logger.info("Bill Payment is {} ",billPaymentDTO.toString());

            if (session.getAttribute("authenticated") != null) {

                try {
                    String message = paymentService.addBillPayment(billPaymentDTO);
                    model.addAttribute("success", "Bill Payment added successfully");
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/retail/payment/completed";
                } catch (InternetBankingException e) {
                    logger.error("Bill Payment Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/retail/payment/new";
                } catch (Exception e) {
                    logger.error("Bill Payment Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/retail/payment/new";
                }
            }

        }
        return "redirect:/retail/payment/completed";

    }

    @ResponseBody
    @RequestMapping(value = "/biller", method = {RequestMethod.GET, RequestMethod.POST})

    public List<Biller> getBillers(Biller biller){
        return billerService.getBillersByCategory(biller.getCategoryName());
    }


    @ResponseBody
    @RequestMapping(value = "/paymentItem", method = {RequestMethod.GET, RequestMethod.POST})
    public List<PaymentItem> getPaymentItem(PaymentItem paymentItem){
        return billerService.getPaymentItems(paymentItem.getBillerId());
    }


    @ResponseBody
    @GetMapping("/paymentItem/{paymentItemId}")
    public PaymentItem getPaymentItem(@PathVariable Long paymentItemId){
        PaymentItem paymentItem = billerService.getPaymentItem(paymentItemId);
        logger.info(" The payment items {}", paymentItem);
        return paymentItem;
    }


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
        DataTablesOutput<BillPaymentDTO> out = new DataTablesOutput<>();
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
                    .forEach(accountList::add);
            model.addAttribute("accountList", accountList);


        }

    }
}
