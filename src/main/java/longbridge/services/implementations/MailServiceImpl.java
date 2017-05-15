package longbridge.services.implementations;

import longbridge.models.Email;
import longbridge.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private JavaMailSender mailSender;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override public void send(String recipient, String subject, String message) throws MailException {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("admin@ibanking.coronationmb.com");
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);
        };
            mailSender.send(messagePreparator);
            logger.info("Email successfully sent to {}",recipient);
    }

    @Override public void send(Email email) throws MailException {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(email.getSenderEmail());
            messageHelper.setTo(email.getReceiverEmail());
            messageHelper.setSubject(email.getMessageSubject());
            messageHelper.setText(email.getMessageBody());

        };
        mailSender.send(messagePreparator);
        logger.info("Email successfully sent to {}", email.getReceiverEmail());
    }
}