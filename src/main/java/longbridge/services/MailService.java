package longbridge.services;

import longbridge.models.Email;
import org.springframework.mail.MailException;

/**
 * Created by Fortune on 5/12/2017.
 */
public interface MailService {

    void send(String recipient, String subject, String message) throws MailException;

    void send(Email email) throws MailException;
}
