package longbridge.services.implementations;

import longbridge.models.Email;
import longbridge.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    private JavaMailSender mailSender;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mail.from}")
    private String sender;

    @Value("${logo.url}")
    private String logoUrl;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void send(String recipient, String subject, String message) throws MailException {
        MimeMessagePreparator messagePreparator = (MimeMessage mimeMessage) -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);

        };

        logger.info("Trying to send mail to {}", recipient);

        mailSender.send(messagePreparator);
        logger.info("Email successfully sent to {} with subject '{}'", recipient, subject);
    }



    @Override
    @Async
    public void send(Email email) throws MailException {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);
            if (email.getReceiverEmail() != null) {
                messageHelper.setTo(email.getReceiverEmail());
            }
            if (email.getReceiverEmails() != null) {
                messageHelper.setTo(email.getReceiverEmails());
            }
            if (email.getCcList() != null) {
                messageHelper.setCc(email.getCcList());
            }
            messageHelper.setSubject(email.getMessageSubject());
            messageHelper.setText(email.getMessageBody());

        };
        logger.info("Trying to send mail to {}", email.getReceiverEmail()!=null?email.getReceiverEmail():email.getReceiverEmails());
        mailSender.send(messagePreparator);
        logger.info("Email successfully sent to {} with subject '{}'", email.getReceiverEmail()!=null?email.getReceiverEmail():email.getReceiverEmails(), email.getMessageSubject());
    }




    @Async
    @Override
    public void sendMail(Email email, Context context){
        context.setVariable("logoUrl",logoUrl);
        String messageBody = templateEngine.process(email.getTemplate(),context);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);

            if (email.getReceiverEmail() != null) {
                messageHelper.setTo(email.getReceiverEmail());
            }
            if (email.getReceiverEmails() != null) {
                messageHelper.setTo(email.getReceiverEmails());
            }
            if (email.getCcList() != null) {
                messageHelper.setCc(email.getCcList());
            }
            messageHelper.setSubject(email.getMessageSubject());
            messageHelper.setText(messageBody,true);

        };
        logger.info("Trying to send mails to {}", email.getReceiverEmail()!=null?email.getReceiverEmail():email.getReceiverEmails());
        mailSender.send(messagePreparator);
        logger.info("Email successfully sent to {} with subject '{}'", email.getReceiverEmail()!=null?email.getReceiverEmail():email.getReceiverEmails(), email.getMessageSubject());

    }

}