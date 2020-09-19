package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.models.CorporateUser;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.Response;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by mac on 06/03/2018.
 */
@Controller
@RequestMapping("/corporate/fixdeposit")
public class CorpFixedDepositController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FixedDepositService fixedDepositService;
    @Autowired
    private ServiceReqConfigService serviceReqConfigService;

    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private RetailUserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    CorporateUserService corporateUserService;

    @GetMapping("/view")
    public String viewFixedDeposits(Principal principal,Model model) {
        logger.info("the deposit view");
        return "corp/fixedDeposit/view";
    }
    @GetMapping("/details")
    @ResponseBody
    public DataTablesOutput<FixedDepositDTO> getStatementDataByState(DataTablesInput input, Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FixedDepositDTO> fixedDepositDTOS = null;
        CorporateUser corporateUser=corporateUserService.getUserByName(principal.getName());
        fixedDepositDTOS = fixedDepositService.getFixedDepositDetials(corporateUser.getCorporate().getCustomerId(),pageable);
        DataTablesOutput<FixedDepositDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(fixedDepositDTOS.getContent());
        out.setRecordsFiltered(fixedDepositDTOS.getTotalElements());
        out.setRecordsTotal(fixedDepositDTOS.getTotalElements());
        logger.info("elem {}",fixedDepositDTOS.getTotalElements());
        return out;
    }
    @GetMapping("/new")
    public String newFixedDeposits(Model model,Locale locale) {
        FixedDepositDTO fixedDepositDTO = new FixedDepositDTO();
        Iterable<CodeDTO> tenors = codeService.getCodesByType("TENOR");
        Iterable<CodeDTO> depositType = codeService.getCodesByType("FIXED_DEPOSIT_TYPE");
        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("FIXED-DEPOSIT");

        model.addAttribute("fixedDepositDTO",fixedDepositDTO);
        model.addAttribute("tenors",tenors);
        model.addAttribute("depositTypes",depositType);
        model.addAttribute("requestConfig", serviceReqConfig);
        model.addAttribute("requestDTO", new ServiceRequestDTO());
        model.addAttribute("notice", messageSource.getMessage("deposit.notice",null,locale));

        return "corp/fixedDeposit/new1";
    }

   /* @PostMapping("/new")
    public String newFixedDeposits(RedirectAttributes redirectAttributes,Model model,@ModelAttribute("fixedDepositDTO") @Valid FixedDepositDTO fixedDepositDTO, HttpSession session, Locale locale) {
        logger.info("the fixdeposit {}",fixedDepositDTO);
        if(!fixedDepositService.isBalanceEnoughForBooking(fixedDepositDTO)) {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.balance.insufficient", null, locale));
            return "redirect:/retail/fixdeposit/view";
        }
        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", fixedDepositDTO);
            session.setAttribute("redirectURL", "/retail/fixdeposit/book/process");
            return "redirect:/retail/token/authenticate";
        }
        ServiceRequestDTO serviceRequestDTO=new ServiceRequestDTO();

        Response response = fixedDepositService.bookFixDeposit(fixedDepositDTO);
        if(response != null){
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("deposit.book.success",null,locale));
        }else {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.book.failed", null, locale));

        }
        return "redirect:/retail/fixdeposit/view";
    }*/
  /*  @GetMapping("/book/process")
    public String bookFixedDeposits(HttpSession session,Locale locale, RedirectAttributes redirectAttributes) {
        logger.info("the liquidate process");
        FixedDepositDTO fixedDepositDTO = (FixedDepositDTO) session.getAttribute("requestDTO");
        session.removeAttribute("requestDTO");
        session.removeAttribute("redirectURL");
        Response response = fixedDepositService.bookFixDeposit(fixedDepositDTO);

        if(response != null){
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("deposit.book.success",null,locale));
        }else {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.book.failed", null, locale));

        }
        return "redirect:/retail/fixdeposit/view";
    }*/
//Gb
    /*@PostMapping("/rate")
    @ResponseBody
    public String getDepositRate(WebRequest webRequest){
        String acctNumber = webRequest.getParameter("acctNumber");
        String amount = webRequest.getParameter("amount");
        String tenor = webRequest.getParameter("tenor");
        logger.info("the account number {} the amount {} and tenor {}",acctNumber,amount,tenor);
        String rate =  integrationService.estinameDepositRate(amount,tenor,acctNumber);
        return rate;
    }*/

    @GetMapping("/liquidate/{acctNum}/{refNo}/{amount}")
    public String newFixedDeposits(Model model,@PathVariable String acctNum,@PathVariable String refNo,@PathVariable String amount) {

        ServiceReqConfigDTO serviceReqConfig = serviceReqConfigService.getServiceReqConfigRequestName("FIXED-DEPOSIT");

        model.addAttribute("refNo",refNo);
        model.addAttribute("depositNo",acctNum);
        model.addAttribute("initialAmount",amount);

        model.addAttribute("requestConfig", serviceReqConfig);
        model.addAttribute("requestDTO", new ServiceRequestDTO());
        return "corp/fixedDeposit/liquidate";
    }
    @PostMapping("/liquidate")
    public String newFixedDeposits(Principal principal, @ModelAttribute("fixedDepositDTO") @Valid FixedDepositDTO fixedDepositDTO, HttpSession session, Model model, Locale locale, RedirectAttributes redirectAttributes) {
        logger.info("the account to liquidate {}",fixedDepositDTO);
        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", fixedDepositDTO);
            session.setAttribute("redirectURL", "/retail/fixdeposit/liquidate/process");
            return "redirect:/retail/token/authenticate";
        }
        Response response = fixedDepositService.liquidateDeposit(fixedDepositDTO);
        if(response != null){
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("deposit.liquidate.success",null,locale));

        }else {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.liquidate.failed", null, locale));

        }
        return "redirect:/retail/fixdeposit/view";
    }
    @GetMapping("/liquidate/process")
    public String liquidateFixedDeposits(HttpSession session,Locale locale, RedirectAttributes redirectAttributes) {

        FixedDepositDTO fixedDepositDTO = (FixedDepositDTO) session.getAttribute("requestDTO");
        session.removeAttribute("requestDTO");
        session.removeAttribute("redirectURL");
        Response response = fixedDepositService.liquidateDeposit(fixedDepositDTO);
        if(response != null){
            logger.info("the liquidate process");
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("deposit.liquidate.success",null,locale));

        }else {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.liquidate.failed", null, locale));

        }
        return "redirect:/retail/fixdeposit/view";
    }

    @GetMapping("/addfund/{acctNum}/{refNo}")
    public String addFund(Model model,@PathVariable String acctNum,@PathVariable String refNo) {
        logger.info("the account no {} and ref no {}",acctNum,refNo);
        FixedDepositDTO fixedDepositDTO = new FixedDepositDTO();
        model.addAttribute("fixedDepositDTO",fixedDepositDTO);
        return "cust/fixedDeposit/addfund";
    }
    @PostMapping("/fund")
    public String addFund(RedirectAttributes redirectAttributes,Model model,@ModelAttribute("fixedDepositDTO") @Valid FixedDepositDTO fixedDepositDTO, HttpSession session, Locale locale) {
        logger.info("the fixdeposit {}",fixedDepositDTO);
        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", fixedDepositDTO);
            session.setAttribute("redirectURL", "/retail/fixdeposit/fund/process");
            return "redirect:/retail/token/authenticate";
        }
        Response response = fixedDepositService.addFund(fixedDepositDTO);
        if(response != null){
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("deposit.fund.success",null,locale));
        }else {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.fund.failed", null, locale));

        }
        return "redirect:/retail/fixdeposit/view";
    }
    @GetMapping("/fund/process")
    public String processAddFund(HttpSession session,Locale locale, RedirectAttributes redirectAttributes) {
        logger.info("the liquidate process");
        FixedDepositDTO fixedDepositDTO = (FixedDepositDTO) session.getAttribute("requestDTO");
        session.removeAttribute("requestDTO");
        session.removeAttribute("redirectURL");
        Response response = fixedDepositService.addFund(fixedDepositDTO);
        if(response != null){
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("deposit.fund.success",null,locale));
        }else {
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("deposit.fund.failed", null, locale));

        }
        return "redirect:/retail/fixdeposit/view";
    }

    @PostMapping("/rate")
    @ResponseBody
    public Optional<Integer> getTenorRate(WebRequest webRequest){
        int amount=Integer.parseInt(webRequest.getParameter("amount"));
        int tenor=Integer.parseInt(webRequest.getParameter("tenor"));
            return fixedDepositService.getRateBasedOnAmountAndTenor(amount, tenor);

    }
    @PostMapping("/balance/check")
    @ResponseBody
    public String checkBalance(WebRequest webRequest,Locale locale){
        String amount=webRequest.getParameter("amount");
        String accountNumber=webRequest.getParameter("accountNumber");
        FixedDepositDTO fixedDepositDTO=new FixedDepositDTO();
        fixedDepositDTO.setInitialDepositAmount(amount);
        fixedDepositDTO.setAccountNumber(accountNumber);
        if(!fixedDepositService.isBalanceEnoughForBooking(fixedDepositDTO)) {
            return  messageSource.getMessage("deposit.balance.insufficient", null, locale);

        }else{
            return "";
        }
    }


}
