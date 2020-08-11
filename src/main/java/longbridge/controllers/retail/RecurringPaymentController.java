package longbridge.controllers.retail;

import longbridge.dtos.PaymentStatDTO;
import longbridge.dtos.RecurringPaymentDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.PaymentItemRepo;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/retail/recurringpayment")
public class RecurringPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(RecurringPaymentController.class);

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
    private SecurityService securityService;
    private final BillerService billerService;
    private final RetailUserService retailUserService;

    @Autowired
    public RecurringPaymentController(BillerService billerService, RetailUserService retailUserService, SecurityService securityService, AccountService accountService) {
        this.billerService = billerService;
        this.retailUserService = retailUserService;
        this.securityService = securityService;
        this.accountService = accountService;
    }

    @GetMapping()
    public String getRecurringPaymentForUser(){
        return "cust/recurringpayment/view";
    }

    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public String getRecurringBillPaymentPage(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        List<BillerCategory> billerCategories = billerService.getCategory();
        model.addAttribute("billerCategories",billerCategories);
        RecurringPaymentDTO recurringPaymentDTO = new RecurringPaymentDTO();
        recurringPaymentDTO.setPhoneNumber(retailUser.getPhoneNumber());
        recurringPaymentDTO.setEmailAddress(retailUser.getEmail());
        model.addAttribute("recurringpayment", recurringPaymentDTO);
        model.addAttribute("durations",codeService.getCodesByType("STANDING_ORDER_FREQ"));
        return "cust/recurringpayment/pagei";
    }

    @PostMapping("/summary")
    public String getRecurringPaymentSummary(@ModelAttribute("recurringpayment") @Valid RecurringPaymentDTO recurringPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest, PaymentItem paymentItemCode, Biller billerName, PaymentItem paymentItemName){
        if(result.hasErrors()){
            return "cust/recurringpayment/pagei";
        }
        model.addAttribute("recurringPaymentDTO",recurringPaymentDTO);
//        paymentItemCode = billerService.getPaymentItem(Long.parseLong(recurringPaymentDTO.getPaymentItemId()));
//        recurringPaymentDTO.setPaymentCode(paymentItemCode.getPaymentCode());
        billerName = billerService.getBillerName(Long.parseLong(recurringPaymentDTO.getBillerId()));
        recurringPaymentDTO.setBillerName(billerName.getBillerName());
        paymentItemName = billerService.getPaymentItem(Long.parseLong(recurringPaymentDTO.getPaymentItemId()));
        recurringPaymentDTO.setPaymentItemName(paymentItemName.getPaymentItemName());
        model.addAttribute("billPaymentDTO", recurringPaymentDTO);
        servletRequest.getSession().setAttribute("recurringPaymentDTO", recurringPaymentDTO);
        return "cust/recurringpayment/summary";
    }

    @PostMapping
    public String createRecurringPayment(@ModelAttribute("recurringPaymentDTO") @Valid RecurringPaymentDTO recurringPaymentDTO, Principal principal, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "cust/recurringpayment/pagei";
        }

        logger.info("recurring payment request  {}", recurringPaymentDTO);
        logger.info("auth {}", session.getAttribute("authenticated"));
        SettingDTO setting = configurationService.getSettingByName("ENABLE_RETAIL_2FA");
        logger.info("setg {}",setting);
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", recurringPaymentDTO);
            session.setAttribute("redirectURL", "/retail/recurringpayment/process");
            return "redirect:/retail/token/authenticate";
        }

        return "redirect:/recurringpayment/summary";
    }

    @GetMapping("/process")
    public String processRequest(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {


        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        if (session.getAttribute("requestDTO") != null) {
            RecurringPaymentDTO recurringPaymentDTO = (RecurringPaymentDTO) session.getAttribute("requestDTO");

            logger.info("Recurring Payment is {} ",recurringPaymentDTO.toString());

            if (session.getAttribute("authenticated") != null) {

                try {
                    String message = recurringPaymentService.addRecurringPayment(retailUser, recurringPaymentDTO);
                    model.addAttribute("success", "Recurring Payment added successfully");
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                } catch (InternetBankingException e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/retail/recurringpayment";
                } catch (Exception e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/retail/recurringpayment";
                }
            }

        }
        return "redirect:/retail/recurringpayment";
    }

    @PostMapping("/delete")
    public String deleteRecurringPaymentWithToken(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the token {}", token);
        Long recurringPaymentId = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the id {}", recurringPaymentId);
        if (StringUtils.isNotBlank(token) && recurringPaymentId != 0) {
            try {
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = recurringPaymentService.deleteRecurringPayment(recurringPaymentId);
                        recurringPaymentService.recurringPayments(recurringPaymentService.getRecurringPayment(recurringPaymentId)).
                                stream().filter(Objects::nonNull).forEach(i->recurringPaymentService.deletePayment(i.getId()));

                        redirectAttributes.addFlashAttribute("message", message);
                    } catch (InternetBankingException e) {
                        logger.error("Recurring Payment Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                    return "redirect:/retail/recurringpayment";
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                    return "redirect:/retail/recurringpayment";
                }
            } catch (InternetBankingException e) {
                logger.error("Recurring Payment Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/retail/recurringpayment";
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token to delete ! ");
            return "redirect:/retail/recurringpayment";
        }
    }

    @PostMapping("/payment")
    public String deletePayment(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the token {}", token);
        Long paymentId = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the id {}", paymentId);
        if (StringUtils.isNotBlank(token) && paymentId != 0) {
            RecurringPayment recurringPayment = recurringPaymentService.getPaymentsRecurringPayment(paymentId);
            try {
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = recurringPaymentService.deletePayment(paymentId);
                        redirectAttributes.addFlashAttribute("message", message);
                    } catch (InternetBankingException e) {
                        logger.error("Recurring Payment Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                    return "redirect:/retail/recurringpayment/payments/" + recurringPayment.getId();
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                    return "redirect:/retail/recurringpayment/payments/" + recurringPayment.getId();
                }
            } catch (InternetBankingException e) {
                logger.error("Recurring Payment Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/retail/recurringpayment/payments/" + recurringPayment.getId();
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token to delete ! ");
            return "redirect:/retail/recurringpayment";
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<RecurringPaymentDTO> getRecurringPayments(DataTablesInput input, Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Page<RecurringPaymentDTO> page = recurringPaymentService.getUserRecurringPaymentDTOs(retailUser,pageable);

        DataTablesOutput<RecurringPaymentDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
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

    @GetMapping("/payments/{id}")
    public String getPayments(Model model, @PathVariable Long id) {
        RecurringPayment recurringPayment = recurringPaymentService.getRecurringPayment(id);
        Collection<PaymentStatDTO> payments = recurringPaymentService.getPayments(recurringPayment);
        model.addAttribute("payments", payments);
        return "cust/recurringpayment/payments";
    }

}
