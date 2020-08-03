package longbridge.services;

import longbridge.models.Email;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.thymeleaf.context.Context;

/**
 * Created by Fortune on 5/12/2017.
 */
public interface MailService {

    @PreAuthorize("hasAuthority('SEND_EMAIL')")
    void send(String recipient, String subject, String message, boolean isHTML) throws MailException;

    @PreAuthorize("hasAuthority('SEND_EMAIL')")
    void send(String recipient, String subject, String message) throws MailException;


    @PreAuthorize("hasAuthority('SEND_EMAIL')")
    void send(Email email) throws MailException;


    void sendMail(Email email, Context context) throws MailException;



    }
