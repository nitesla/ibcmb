package longbridge.services;

import longbridge.models.Email;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by Fortune on 5/12/2017.
 */
public interface MailService {

    @PreAuthorize("hasAuthority('SEND_EMAIL')")
    void send(String recipient, String subject, String message) throws MailException;

    void sendHtml(String recipient, String subject, String message) throws MailException;

    @PreAuthorize("hasAuthority('SEND_EMAIL')")
    void send(Email email) throws MailException;

    void sendHtml(Email email) throws MailException;
}
