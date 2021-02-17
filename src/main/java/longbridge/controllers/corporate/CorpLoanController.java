package longbridge.controllers.corporate;


import longbridge.dtos.LoanDTO;
import longbridge.dtos.MailLoanDTO;
import longbridge.services.LoanDetailsService;
import longbridge.utils.JasperReport.ReportHelper;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequestMapping("/corporate/loan")
public class CorpLoanController {

    @Autowired
    private LoanDetailsService loanDetailsService;
    @Autowired
    private MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();


    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Value("${report.logo.url}")
    private String imagePath;

    @PostMapping("/email")
    public String sendLoanDetailsinMail(MailLoanDTO mailLoanDTO, RedirectAttributes redirectAttributes) {
        String recipientEmail = mailLoanDTO.getRecipientEmail();
        String recipientName = mailLoanDTO.getRecipientName();

        try {
            loanDetailsService.sendLoanDetails(recipientEmail, recipientName, mailLoanDTO.getAccountNumber());
            logger.info("Email successfully sent to {} with subject {}", recipientEmail,messageSource.getMessage("loan.subject", null, locale));
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("mail.send.success", null, locale));
        }
        catch (MailException exception){
            logger.info("Trying to send mail to {}", exception);
            redirectAttributes.addFlashAttribute("message",  messageSource.getMessage("mail.send.failure", null, locale));
        }

        return "redirect:/corporate/dashboard";

    }

    @GetMapping("/pdf/{accountNumber}")
    public String downloadLoanPdf(@PathVariable String accountNumber,HttpServletResponse response,RedirectAttributes redirectAttributes) throws Exception {
        LoanDTO loan = loanDetailsService.getLoanDetails(accountNumber);
        String success =null;
        if(loan!=null){
        Map<String, Object> modelMap = new HashMap<>();

            modelMap.put("accountId",loan.getAccountId());
            modelMap.put("accountNumber",loan.getAccountNumber());
            modelMap.put("startDate",loan.getStartDate());
            modelMap.put("tenor",loan.getTenor());
            modelMap.put( "amount",loan.getAmount());
            modelMap.put("interestRate",loan.getInterestRate());
            modelMap.put("maturityDate",loan.getMaturityDate());
            modelMap.put("logotwo", imagePath);
        List<LoanDTO> loanDTOList = new ArrayList<>();
        loanDTOList.add(loan);
        response.setContentType("application/x-download");
        response.setHeader("Content-disposition", "attachment; filename=\"loan_report.pdf\"");
        OutputStream outputStream = response.getOutputStream();
        JasperReport jasperReport = ReportHelper.getJasperReport("loan_pdf");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(loanDTOList));
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
        success ="success";
    }
        else if(loan==null){
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("Loan Detail not available , Please contact the bank ", null, locale));
        success = "redirect:/corporate/dashboard";
    }
        System.out.println(success);
     return success;
}



    @GetMapping("/excel/{accountNumber}")
    public String downloadLoanExcel(@PathVariable String accountNumber,HttpServletResponse response,RedirectAttributes redirectAttributes ) throws Exception {
        LoanDTO loan = loanDetailsService.getLoanDetails(accountNumber);
        String success =null;
        if(loan!=null){
        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("accountId",loan.getAccountId());
        modelMap.put("accountNumber",loan.getAccountNumber());
        modelMap.put("startDate",loan.getStartDate());
        modelMap.put("tenor",loan.getTenor());
        modelMap.put( "amount",loan.getAmount());
        modelMap.put("interestRate",loan.getInterestRate());
        modelMap.put("maturityDate",loan.getMaturityDate());
        modelMap.put("logotwo", imagePath);
        List<LoanDTO> loanDTOList = new ArrayList<>();
        loanDTOList.add(loan);
        JasperReport jasperReport = ReportHelper.getJasperReport("loan_excel");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(loanDTOList));
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();
        response.setHeader("Content-Length", String.valueOf(baos.size()));
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-disposition", "attachment; filename=\"loan_report.xlsx\"");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(baos.toByteArray());
        outputStream.close();
        baos.close();
        success ="success";
    }
        else if(loan==null){
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("Loan Detail not available , Please contact the bank ", null, locale));
        success = "redirect:/corporate/dashboard";
    }
        System.out.println(success);
        return success;
}


}
