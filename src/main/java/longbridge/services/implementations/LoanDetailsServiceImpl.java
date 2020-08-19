package longbridge.services.implementations;

import com.sun.mail.util.MailConnectException;
import longbridge.dtos.LoanDTO;
import longbridge.exception.InternetBankingException;
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
import java.net.UnknownHostException;
import java.util.*;

@Service
public class LoanDetailsServiceImpl implements LoanDetailsService {
    private JavaMailSender mailSender;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private IntegrationService integrationService;

    @Value("${mail.from}")
    private String sender;

    @Value("${jrxmlImage.path}")
    private String imagePath;

    @Value("${jrxmlFile.path.loan.pdf}")
    private String jrxmlPathPdf;

//    @Value("${jrxmlFile.path.loan.excel}")
//    private String jrxmlPathExcel;

//    @Value("${jrxmlFile.path.loan.csv}")
//    private String jrxmlPathCsv;



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
//        messageSource.getMessage("mail.send.wait", null, locale);
//        logger.info("Trying to send mail to {}", recipient);
//        try {
//        mailSender.send(messagePreparator);
//        logger.info("Email successfully sent to {} with subject {}", recipient,messageSource.getMessage("loan.subject", null, locale));
//         return messageSource.getMessage("mail.send.success", null, locale);
//        }
//
//        catch (MailAuthenticationException mae) {
//            return messageSource.getMessage("mail.connect.failure", null, locale);
//        }
//
//        catch (MailSendException mse) {
//            logger.error("Failed to send mail to {}", recipient, mse);
//            Exception[] exceptions=mse.getMessageExceptions();
//            mse.getFailedMessages();
//            for (Exception e:exceptions) {
//                if (e instanceof UnknownHostException){
//                System.out.println(e+"mooooos");
//                    logger.error("Mail Server Down Contac It department", e);
//             }
//            }
//            return messageSource.getMessage("mail.send.failure", null, locale);
//
//
//        }
//
//        catch (MailException me) {
//           return messageSource.getMessage("mail.send.failure", null, locale);
//        }

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
        DataSource attachment =  new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");
        return attachment;
            }



}
