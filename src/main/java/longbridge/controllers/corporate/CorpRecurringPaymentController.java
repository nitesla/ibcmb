package longbridge.controllers.corporate;

import longbridge.dtos.PaymentStatDTO;
import longbridge.dtos.RecurringPaymentDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import longbridge.repositories.CorpRecurringPaymentRepo;
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
    CorpRecurringPaymentRepo recurringPaymentRepo ;

    @Autowired
    private PaymentItemRepo paymentItemRepo;

    @Autowired
    private CodeService codeService;

    @Autowired
    ConfigurationService configurationService;

    private final AccountService accountService;
    private final SecurityService securityService;
    private final BillerService billerService;
    private final CorporateUserService corporateUserService;

    @Autowired
    public CorpRecurringPaymentController(BillerService billerService, CorporateUserService corporateUserService, SecurityService securityService, AccountService accountService) {
        this.billerService = billerService;
        this.corporateUserService = corporateUserService;
        this.securityService = securityService;
        this.accountService = accountService;
    }

    @GetMapping()
    public String getRecurringPaymentForUser(){
        return "corp/recurringpayment/view";
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
    public String getRecurringPaymentSummary(@ModelAttribute("recurringpayment") @Valid RecurringPaymentDTO recurringPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest){
        if(result.hasErrors()){
            return "corp/recurringpayment/pagei";
        }
        model.addAttribute("recurringPaymentDTO",recurringPaymentDTO);
        Biller billerName = billerService.getBillerName(Long.parseLong(recurringPaymentDTO.getBillerId()));
        recurringPaymentDTO.setBillerName(billerName.getBillerName());
        PaymentItem paymentItemName = billerService.getPaymentItem(Long.parseLong(recurringPaymentDTO.getPaymentItemId()));
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
                    model.addAttribute("success", "Recurring Payment added successfully");
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                } catch (InternetBankingException e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/corporate/recurringpayment";
                } catch (Exception e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/corporate/recurringpayment";
                }
            }

        }
        return "redirect:/corporate/recurringpayment";
    }

    @PostMapping("/delete")
    public String deleteRecurringPaymentWithToken(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the token {}", token);
        Long recurringPaymentId = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the id {}", recurringPaymentId);
        if (StringUtils.isNotBlank(token) && recurringPaymentId != 0) {
            try {
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
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
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                }
                return "redirect:/corporate/recurringpayment";
            } catch (InternetBankingException e) {
                logger.error("Recurring Payment Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/corporate/recurringpayment";
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token to delete ! ");
            return "redirect:/corporate/recurringpayment";
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
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = recurringPaymentService.deletePayment(paymentId);
                        redirectAttributes.addFlashAttribute("message", message);
                    } catch (InternetBankingException e) {
                        logger.error("Recurring Payment Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                }
                return "redirect:/corporate/recurringpayment/payments/" + recurringPayment.getId();
            } catch (InternetBankingException e) {
                logger.error("Recurring Payment Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/corporate/recurringpayment/payments/" + recurringPayment.getId();
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token to delete ! ");
            return "redirect:/corporate/recurringpayment";
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<RecurringPaymentDTO> getRecurringPayments(DataTablesInput input, Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Page<RecurringPaymentDTO> page = recurringPaymentService.getCorpUserRecurringPaymentDTOS(corporateUser,pageable);

        DataTablesOutput<RecurringPaymentDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
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
                    .forEach(accountList::add);
            model.addAttribute("accountList", accountList);


        }

    }

    @GetMapping("/payments/{id}")
    public String getPayments(Model model, @PathVariable Long id) {
        CorpRecurringPayment recurringPayment = recurringPaymentRepo.findOneById(id);
        Collection<PaymentStatDTO> payments = recurringPaymentService.getPayments(recurringPayment);
        model.addAttribute("payments", payments);
        return "corp/recurringpayment/payments";
    }

}
