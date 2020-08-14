package longbridge.controllers.corporate;

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
@RequestMapping("/corporate/payment")
public class CorpPaymentController {

    private final BillerService billerService;
    private final CorporateUserService corporateUserService;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final String page = "corp/payment/";
    private SecurityService securityService;
    @Autowired
    ConfigurationService configurationService;
    @Autowired
    private MessageSource messageSource;
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
        List<BillerCategory> billerCategories = billerService.getCategory();
        model.addAttribute("billerCategories",billerCategories);
        BillPaymentDTO paymentDTO = new BillPaymentDTO();
        paymentDTO.setPhoneNumber(corporateUser.getPhoneNumber());
        paymentDTO.setEmailAddress(corporateUser.getEmail());
        model.addAttribute("billPaymentDTO", paymentDTO);
        return "corp/payment/new";
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String savePayment(@PathVariable Long id, Model model, HttpServletRequest request, Locale locale, RedirectAttributes attributes) {
        BillPayment billPayment = paymentService.getBillPayment(id);
        List<Biller> billerCategories = billerService.getBillersCategories();
        model.addAttribute("billerCategories", billPayment);
        BillPaymentDTO billPaymentDTO = new BillPaymentDTO();
        billPaymentDTO.setPhoneNumber(billPayment.getPhoneNumber());
        billPaymentDTO.setEmailAddress(billPayment.getEmailAddress());
        billPaymentDTO.setPaymentCode(billPayment.getPaymentCode());
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        request.getSession().setAttribute("billPaymentDTO", billPaymentDTO);
        return page + "pagei";
    }

    @PostMapping("/summary")
    public String paymentSummary(@ModelAttribute("billPaymentDTO") @Valid BillPaymentDTO billPaymentDTO, BindingResult result, Model model, HttpServletRequest servletRequest, PaymentItem paymentItemCode, Biller billerName, PaymentItem paymentItemName) {
        model.addAttribute("billPaymentDTO", billPaymentDTO);
        billerName = billerService.getBillerName(Long.parseLong(billPaymentDTO.getBillerId()));
        billPaymentDTO.setBillerName(billerName.getBillerName());
        paymentItemName = billerService.getPaymentItem(Long.parseLong(billPaymentDTO.getPaymentItemId()));
        billPaymentDTO.setPaymentItemName(paymentItemName.getPaymentItemName());
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
            return "corp/payment/new";
        }

        logger.info("Bill payment request  {}", billPaymentDTO);
        logger.info("auth {}", session.getAttribute("authenticated"));
        SettingDTO setting = configurationService.getSettingByName("ENABLE_CORPORATE_2FA");
        logger.info("setg {}",setting);
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", billPaymentDTO);
            session.setAttribute("redirectURL", "/corporate/payment/process");
            return "redirect:/corporate/token/authenticate";
        }

        return "redirect:/corporate/payment/summary";
    }

    @GetMapping("/process")
    public String billPayment(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        if (session.getAttribute("requestDTO") != null) {
            BillPaymentDTO billPaymentDTO = (BillPaymentDTO) session.getAttribute("requestDTO");

            logger.info("Bill Payment is {} ",billPaymentDTO.toString());

            if (session.getAttribute("authenticated") != null) {

                try {
                    String message = paymentService.addCorpBillPayment(billPaymentDTO);
                    model.addAttribute("success", "Bill Payment added successfully");
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/corporate/payment/completed";
                } catch (InternetBankingException e) {
                    logger.error("Bill Payment Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/corporate/payment/new";
                } catch (Exception e) {
                    logger.error("Bill Payment Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/corporate/payment/new";
                }
            }

        }
        return "redirect:/corporate/payment/completed";

    }

    @ResponseBody
    @RequestMapping(value = "/biller", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Biller> getBillers(Biller biller){
        List<Biller> billerByCategory = billerService.getBillersByCategory(biller.getCategoryName());
        return billerByCategory;
    }


    @ResponseBody
    @RequestMapping(value = "/paymentItem", method = {RequestMethod.GET, RequestMethod.POST})
    public List<PaymentItem> getPaymentItem(PaymentItem paymentItem){
        List<PaymentItem> paymentItems = billerService.getPaymentItems(paymentItem.getBillerId());
        return paymentItems;
    }

    @ResponseBody
    @GetMapping("/paymentItem/{paymentItemId}")
    public PaymentItem getPaymentItem(@PathVariable Long paymentItemId){
        PaymentItem paymentItem = billerService.getPaymentItem(paymentItemId);
        return paymentItem;
    }
    @GetMapping("/completed")
    public String getCompletedPayments(){

        return "corp/payment/completed";
    }

    @GetMapping("/completed/all")
    public @ResponseBody
    DataTablesOutput<BillPaymentDTO> getTransfersCompleted(DataTablesInput input, @RequestParam("csearch") String search){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<BillPaymentDTO> corpPaymentRequests;

        if (StringUtils.isNoneBlank(search)) {

            corpPaymentRequests = paymentService.getCorpPayments(search.toUpperCase(), pageable);
        } else corpPaymentRequests =  paymentService.getCorpPayments(pageable);

        DataTablesOutput<BillPaymentDTO> out = new DataTablesOutput<BillPaymentDTO>();
        out.setDraw(input.getDraw());
        out.setData(corpPaymentRequests.getContent());
        out.setRecordsFiltered(corpPaymentRequests.getTotalElements());
        out.setRecordsTotal(corpPaymentRequests.getTotalElements());

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
