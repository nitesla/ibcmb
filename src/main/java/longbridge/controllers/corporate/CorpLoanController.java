package longbridge.controllers.corporate;


import longbridge.dtos.LoanDTO;
import longbridge.security.userdetails.CustomUserPrincipal;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private Locale locale = LocaleContextHolder.getLocale();


    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Value("${jrxmlImage.path}")
    private String imagePath;

    @GetMapping("/email/{accountNumber}")
    public String sendLoanDetailsinMail(@PathVariable String accountNumber, RedirectAttributes redirectAttributes) {
       CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String recipient = principal.getUser().getEmail();
        String name = principal.getUser().getFirstName();

        try {
            loanDetailsService.sendLoanDetails(recipient,name, accountNumber);
            logger.info("Email successfully sent to {} with subject {}", recipient,messageSource.getMessage("loan.subject", null, locale));
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("mail.send.success", null, locale));
        }
        catch (MailException exception){
            logger.info("Trying to send mail to {}", exception);
            redirectAttributes.addFlashAttribute("message",  messageSource.getMessage("mail.send.failure", null, locale));
        }

        return "redirect:/corporate/dashboard";

    }

    @GetMapping("/pdf/{accountNumber}")
    public ResponseEntity<HttpStatus> downloadLoanPdf(@PathVariable String accountNumber,HttpServletResponse response) throws Exception {
        LoanDTO loan = loanDetailsService.getLoanDetails(accountNumber);


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
        response.setHeader("Content-disposition", String.format("attachment; filename=\"loan_report.pdf\""));
        OutputStream outputStream = response.getOutputStream();
        JasperReport jasperReport = ReportHelper.getJasperReport("loan_pdf");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(loanDTOList));
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/excel/{accountNumber}")
    public ResponseEntity<HttpStatus> downloadLoanExcel(@PathVariable String accountNumber,HttpServletResponse response) throws Exception {
        LoanDTO loan = loanDetailsService.getLoanDetails(accountNumber);

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
        response.addHeader("Content-disposition", String.format("attachment; filename=\"loan_report.xlsx\""));
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(baos.toByteArray());
        outputStream.close();
        baos.close();
        outputStream.flush();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


}
