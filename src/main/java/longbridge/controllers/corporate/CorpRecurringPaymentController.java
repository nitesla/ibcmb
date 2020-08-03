package longbridge.controllers.corporate;

import longbridge.dtos.BillPaymentDTO;
import longbridge.dtos.RecurringPaymentDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
@RequestMapping("/corporate/recurringpayment")
public class CorpRecurringPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(CorpRecurringPaymentController.class);

    @Autowired
    private BillerRepo billerRepo;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RecurringPaymentService recurringPaymentService;

    @Autowired
    private BillerCategoryRepo billerCategoryRepo;

    @Autowired
    private PaymentItemRepo paymentItemRepo;

    @Autowired
    private CodeService codeService;

    @Autowired
    ConfigurationService configurationService;

    private final AccountService accountService;

    private final BillerService billerService;

    private final CorporateUserService corporateUserService;

    @Autowired
    public CorpRecurringPaymentController(BillerService billerService, CorporateUserService corporateUserService, AccountService accountService) {
        this.billerService = billerService;
        this.corporateUserService = corporateUserService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public String getRecurringBillPaymentPage(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        List<BillerCategory> billerCategories = billerService.getCategory();
        model.addAttribute("billerCategories",billerCategories);
        RecurringPaymentDTO recurringPaymentDTO = new RecurringPaymentDTO();
        recurringPaymentDTO.setPhoneNumber(corporateUser.getPhoneNumber());
        recurringPaymentDTO.setEmailAddress(corporateUser.getEmail());
        model.addAttribute("recurringpayment", recurringPaymentDTO);
        model.addAttribute("durations",codeService.getCodesByType("STANDING_ORDER_FREQ"));
        return "corp/recurringpayment/pagei";
    }

    @PostMapping("/summary")
    public String getRecurringPaymentSummary(@ModelAttribute("recurringpayment") @Valid RecurringPaymentDTO recurringPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest, PaymentItem paymentItemCode, Biller billerName, PaymentItem paymentItemName){
        if(result.hasErrors()){
            return "corp/recurringpayment/pagei";
        }
        model.addAttribute("recurringPaymentDTO",recurringPaymentDTO);
        paymentItemCode = billerService.getPaymentItem(Long.parseLong(recurringPaymentDTO.getPaymentItemId()));
        recurringPaymentDTO.setPaymentCode(paymentItemCode.getPaymentCode());
        billerName = billerService.getBillerName(Long.parseLong(recurringPaymentDTO.getBillerId()));
        recurringPaymentDTO.setBillerName(billerName.getBillerName());
        paymentItemName = billerService.getPaymentItem(Long.parseLong(recurringPaymentDTO.getPaymentItemId()));
        recurringPaymentDTO.setPaymentItemName(paymentItemName.getPaymentItemName());
        model.addAttribute("billPaymentDTO", recurringPaymentDTO);
        servletRequest.getSession().setAttribute("recurringPaymentDTO", recurringPaymentDTO);
        return "corp/recurringpayment/summary";
    }

    @PostMapping
    public String createRecurringPayment(@ModelAttribute("recurringPaymentDTO") @Valid RecurringPaymentDTO recurringPaymentDTO, Principal principal, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "corp/recurringpayment/pagei";
        }

        logger.info("direct debit request  {}", recurringPaymentDTO);
        logger.info("auth {}", session.getAttribute("authenticated"));
        SettingDTO setting = configurationService.getSettingByName("ENABLE_CORPORATE_2FA");
        logger.info("setg {}",setting);
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", recurringPaymentDTO);
            session.setAttribute("redirectURL", "/corporate/recurringpayment/process");
            return "redirect:/corporate/token/authenticate";
        }

        return "redirect:/recurringpayment/summary";
    }

    @GetMapping("/process")
    public String processRequest(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {


        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        if (session.getAttribute("requestDTO") != null) {
            RecurringPaymentDTO recurringPaymentDTO = (RecurringPaymentDTO) session.getAttribute("requestDTO");

            logger.info("Recurring Payment is {} ",recurringPaymentDTO.toString());

            if (session.getAttribute("authenticated") != null) {

                try {
                    String message = recurringPaymentService.addCorpRecurringPayment(corporateUser, recurringPaymentDTO);
                    model.addAttribute("success", "Direct Debit added successfully");
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                } catch (InternetBankingException e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/recurringpayment/pagei";
                } catch (Exception e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/recurringpayment/pagei";
                }
            }

        }
        return "redirect:/recurringpayment/pagei";
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
