package longbridge.services;

import longbridge.exception.InternetBankingException;
import longbridge.models.Email;
import org.springframework.mail.MailException;

/**
 * Created by Fortune on 5/12/2017.
 */
public interface UserGroupMessageService {


    String send(Long groupId, String sender,String subject, String message) throws MailException, InternetBankingException;


    String send(Long groupId, Email email) throws MailException, InternetBankingException;



}
