package longbridge.services.implementations;


import longbridge.dtos.LoanDTO;
import longbridge.services.IntegrationService;
import longbridge.services.LoanDetailsService;
import longbridge.utils.JasperReport.ReportHelper;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class LoanDetailsServiceImpl implements LoanDetailsService {
    private final JavaMailSender mailSender;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private IntegrationService integrationService;

    @Value("${mail.from}")
    private String sender;

    @Value("${report.logo.url}")
    private String imagePath;



    @Autowired
    public LoanDetailsServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public LoanDTO getLoanDetails(String accountNumber) {
        return integrationService.getLoanDetails(accountNumber);
    }

    @Override
    public void sendLoanDetails(String recipient, String name, String accountNumber) throws MailException {
        LoanDTO loan = integrationService.getLoanDetails(accountNumber);
           MimeMessagePreparator messagePreparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(sender);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(messageSource.getMessage("loan.subject", null, locale));
            messageHelper.setText(String.format(messageSource.getMessage("loan.message", null, locale),name));
            messageHelper.addAttachment("loan_report.pdf", mailAttachmentPdf(loan));
        };
        mailSender.send(messagePreparator);

    }



    private DataSource mailAttachmentPdf(LoanDTO loan) throws Exception {

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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperReport jasperReport = ReportHelper.getJasperReport("loan_pdf");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(loanDTOList));
        JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
        return new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");
            }



}
