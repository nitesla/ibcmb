package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorporateUser;
import longbridge.servicerequests.client.RequestService;
import longbridge.servicerequests.client.ServiceRequestDTO;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.JasperReport.ReportHelper;
import longbridge.utils.Response;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.*;

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
    private RequestService requestService;

    @Autowired
    private RequestConfigService requestConfigService;
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
    private SettingsService configService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    CorporateUserService corporateUserService;


    @Value("${report.logo.url}")
    private String imagePath;

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
        logger.info("the cifid been used : {}",corporateUser.getCorporate().getCustomerId());
        fixedDepositDTOS=fixedDepositService.getFixedDepositsForView(corporateUser.getCorporate().getCustomerId(),pageable);
        DataTablesOutput<FixedDepositDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(fixedDepositDTOS.getContent());
        out.setRecordsFiltered(fixedDepositDTOS.getTotalElements());
        out.setRecordsTotal(fixedDepositDTOS.getTotalElements());
        logger.info("Fixed Deposit Element {}",fixedDepositDTOS.getTotalElements());
        return out;
    }
    @GetMapping("/new")
    public String newFixedDeposits(Model model,Locale locale) {
        FixedDepositDTO fixedDepositDTO = new FixedDepositDTO();
        Iterable<CodeDTO> tenors = codeService.getCodesByType("TENOR");
        Iterable<CodeDTO> depositType = codeService.getCodesByType("FIXED_DEPOSIT_TYPE");
        model.addAttribute("fixedDepositDTO",fixedDepositDTO);
        model.addAttribute("tenors",tenors);
        model.addAttribute("depositTypes",depositType);
        model.addAttribute("requestConfig", requestConfigService.getRequestConfigByName("FIXED-DEPOSIT"));
        model.addAttribute("requestDTO", new ServiceRequestDTO());
        model.addAttribute("notice", messageSource.getMessage("deposit.notice",null,locale));

        return "corp/fixedDeposit/new1";
    }



    @GetMapping("/liquidate/{acctNum}/{refNo}/{amount}")
    public String newFixedDeposits(Model model,@PathVariable String acctNum,@PathVariable String refNo,@PathVariable String amount) {


        model.addAttribute("refNo",refNo);
        model.addAttribute("depositNo",acctNum);
        model.addAttribute("initialAmount",amount);

        model.addAttribute("requestConfig", requestConfigService.getRequestConfigByName("FIXED-DEPOSIT"));
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

    @PostMapping("/email")
    public String sendFixedDepositDetailsInMail(FixedDepositDTO fixedDepositDTO, RedirectAttributes redirectAttributes,Locale locale) {
        String recipientEmail = fixedDepositDTO.getRecipientEmail();
        String recipientName = fixedDepositDTO.getRecipientName();

        try {
            fixedDepositService.sendFixedDepositDetails(recipientEmail, recipientName, fixedDepositDTO.getAccountNumber());
            logger.info("Email successfully sent to {} with subject {}", recipientEmail,messageSource.getMessage("fixed.deposit.subject", null, locale));
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("mail.send.success", null, locale));
        }
        catch (MailException exception){
            logger.info("Trying to send mail to {}", exception);
            redirectAttributes.addFlashAttribute("message",  messageSource.getMessage("mail.send.failure", null, locale));
        }

        return "redirect:/corporate/dashboard";

    }



    @GetMapping("/download/{format}/{accountNumber}")
    public String downloadFixedDeposit(@PathVariable("format") String format, @PathVariable("accountNumber") String accountNumber, HttpServletResponse response, RedirectAttributes redirectAttributes,Locale locale) throws Exception {
        FixedDepositDTO fixedDeposit=fixedDepositService.getFixedDepositDetails(accountNumber);
        String success =null;
        if(fixedDeposit!=null){
            Map<String, Object> modelMap = new HashMap<>();

            modelMap.put("accountId",fixedDeposit.getAccountId());
            modelMap.put("accountNumber",fixedDeposit.getAccountNumber());
            modelMap.put("bookRefNo",fixedDeposit.getBookRefNo());
            modelMap.put("depositType",fixedDeposit.getDepositType());
            modelMap.put("depositStatus",fixedDeposit.getDepositStatus());
            modelMap.put("bookingDate",fixedDeposit.getBookingDate());
            modelMap.put("valueDate",fixedDeposit.getValueDate());
            modelMap.put("initialDepositAmount",fixedDeposit.getInitialDepositAmount());
            modelMap.put("maturityDate",fixedDeposit.getMaturityDate());
            modelMap.put("rate",fixedDeposit.getRate());
            modelMap.put("tenor",fixedDeposit.getTenor());
            modelMap.put("maturityAmount",fixedDeposit.getMaturityAmount());
            modelMap.put("logotwo", imagePath);
            List<FixedDepositDTO> fixedDepositList = new ArrayList<>();
             fixedDepositList.add(fixedDeposit);


            JasperReport jasperReport = ReportHelper.getJasperReport("details_report");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(fixedDepositList));


            if ("PDF".equalsIgnoreCase(format)) {
                response.setContentType("application/x-download");
                response.setHeader("Content-disposition", "attachment; filename=\"details_report.pdf\"");
//                OutputStream outputStream = response.getOutputStream();
                if (jasperPrint != null) {
                    logger.info("generating pdf");
                    JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
                    success ="success";
                }

            }

            else if ("EXCEL".equalsIgnoreCase(format))  {
                JRXlsxExporter exporter = new JRXlsxExporter();
                if (jasperPrint != null) {
                    logger.info("generating xls");
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
                    exporter.exportReport();
                    response.setHeader("Content-Length", String.valueOf(baos.size()));
                    response.setContentType("application/vnd.ms-excel");
                    response.addHeader("Content-disposition", "attachment; filename=\"details_report.xlsx\"");
                    OutputStream outputStream = response.getOutputStream();
                    outputStream.write(baos.toByteArray());
                    outputStream.close();
                    baos.close();
                    success ="success";

                }
            }else{
                logger.warn("unsupported report format {}", format);
                throw new InternetBankingException("unsupported report format " + format);
            }

        }

        else if(fixedDeposit==null){
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("Fixed Deposit Detail not available , Please contact the bank ", null, locale));
            success = "redirect:/corporate/dashboard";
        }
        System.out.println(success);
        return success;
    }



    @GetMapping("/view/details/{accountNumber}")
    public String viewFixedDepositDetails(@PathVariable String accountNumber,Model model) {
        FixedDepositDTO fixedDepositDTO=new FixedDepositDTO();
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("fixedDepositDTO",fixedDepositDTO);
        return "corp/fixedDeposit/view1";
    }

    @GetMapping("/getViewData/{accountNumber}")
    @ResponseBody
    public DataTablesOutput<FixedDepositDTO> getViewData(@PathVariable String accountNumber,DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FixedDepositDTO> fixedDepositDTOS = null;
         fixedDepositDTOS=fixedDepositService.getFixedDepositForView(accountNumber,pageable);
         logger.info("VIEW INFO {}",fixedDepositDTOS );
        DataTablesOutput<FixedDepositDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(fixedDepositDTOS.getContent());
        out.setRecordsFiltered(fixedDepositDTOS.getTotalElements());
        out.setRecordsTotal(fixedDepositDTOS.getTotalElements());
        logger.info("Fixed Deposit Element {}",fixedDepositDTOS.getTotalElements());
        return out;
    }




}
