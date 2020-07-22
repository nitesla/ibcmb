package longbridge.controllers.retail;


import longbridge.dtos.LoanDTO;
import longbridge.dtos.LoanDetailsDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/retail/loan")
public class RetailLoanController {
    @Autowired
    private LoanDetailsService loanDetailsService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Value("${jrxmlImage.path}")
    private String imagePath;

    @PostMapping("/email")
    @ResponseBody
    public String sendLoanDetailsinMail(@RequestBody List<LoanDTO> loans, RedirectAttributes redirectAttributes) throws IOException {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String recipient = principal.getUser().getEmail();
        String name = principal.getUser().getFirstName();
        LoanDetailsDTO detailsDTO = new LoanDetailsDTO();
        detailsDTO.setLoanList(loans);
        String message = loanDetailsService.sendLoanDetails(recipient,name,detailsDTO);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/cust/dashboard";

    }

    @PostMapping("/download/pdf")
    public ResponseEntity<HttpStatus> downloadLoanPdf(@ModelAttribute("loanObject") LoanDetailsDTO loans, HttpServletResponse response) throws Exception {

        Map<String, Object> modelMap = new HashMap<>();
        for (LoanDTO loan:loans.getLoanList()) {
            modelMap.put("accountId",loan.getAccountId());
            modelMap.put("accountNumber",loan.getAccountNumber());
            modelMap.put("startDate",loan.getStartDate());
            modelMap.put("tenor",loan.getTenor());
            modelMap.put( "amount",loan.getAmount());
            modelMap.put("interestRate",loan.getInterestRate());
            modelMap.put("maturityDate",loan.getMaturityDate());
            modelMap.put("logotwo", imagePath);
        }

        response.setContentType("application/x-download");
        response.setHeader("Content-disposition", String.format("attachment; filename=\"loan_report.pdf\""));
        OutputStream outputStream = response.getOutputStream();
        JasperReport jasperReport = ReportHelper.getJasperReport("loan_pdf");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(loans.getLoanList()));
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PostMapping("/download/excel")
    public ResponseEntity<HttpStatus> downloadLoanExcel(@ModelAttribute("loanObject") LoanDetailsDTO loans,HttpServletResponse response) throws Exception {

        Map<String, Object> modelMap = new HashMap<>();
        for (LoanDTO loan:loans.getLoanList()) {
            modelMap.put("accountId",loan.getAccountId());
            modelMap.put("accountNumber",loan.getAccountNumber());
            modelMap.put("startDate",loan.getStartDate());
            modelMap.put("tenor",loan.getTenor());
            modelMap.put( "amount",loan.getAmount());
            modelMap.put("interestRate",loan.getInterestRate());
            modelMap.put("maturityDate",loan.getMaturityDate());
            modelMap.put("logotwo", imagePath);
        }

        JasperReport jasperReport = ReportHelper.getJasperReport("loan_pdf");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(loans.getLoanList()));
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
