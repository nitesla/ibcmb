package longbridge.services.implementations;

import longbridge.dtos.ContactDTO;
import longbridge.exception.MessageException;
import longbridge.models.Email;
import longbridge.models.Message;
import longbridge.repositories.MessageRepo;
import longbridge.repositories.OperationsUserRepo;
import longbridge.services.MailService;
import longbridge.services.UserGroupMessageService;
import longbridge.services.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 5/12/2017.
 */

@Service
public class UserGroupMessageServiceImpl implements UserGroupMessageService {

    private final MailService mailService;
    private final MessageRepo messageRepo;
    private final UserGroupService userGroupService;
    private final OperationsUserRepo opsUserRepo;
    private final MessageSource messageSource;
    private final Locale locale = LocaleContextHolder.getLocale();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserGroupMessageServiceImpl(MailService mailService, MessageRepo messageRepo, UserGroupService userGroupService, OperationsUserRepo opsUserRepo, MessageSource messageSource) {
        this.mailService = mailService;
        this.messageRepo = messageRepo;
        this.userGroupService = userGroupService;
        this.opsUserRepo = opsUserRepo;
        this.messageSource = messageSource;
    }

    @Override
    public String send(Long groupId, String sender, String subject, String message) throws MessageException {
        List<ContactDTO> contacts = userGroupService.getContacts(groupId);
        for (ContactDTO contact : contacts) {
            try {
                mailService.send(contact.getEmail(), subject, message);
            } catch (MailAuthenticationException mae) {
                throw new MessageException(messageSource.getMessage("mail.connect.failure", null, locale), mae);
            } catch (MailSendException mse) {
                logger.error("Failed to send mail to {}", contact.getEmail(), mse);
            } catch (MailException me) {
                throw new MessageException(messageSource.getMessage("mail.send.failure", null, locale), me);
            }
        }
        return messageSource.getMessage("mail.send.success", null, locale);
    }




    @Override
    public String send(Long groupId, Email email) throws MessageException{

        List<ContactDTO> contacts = userGroupService.getContacts(groupId);
        for (ContactDTO contact : contacts) {
            if (!contact.isExternal()) {
                Message msg = new Message();
                try {
                    msg.setSender(email.getSenderEmail());
                    msg.setRecipient(opsUserRepo.findById(contact.getDt_RowId()).get().getUserName());
                    msg.setSubject(email.getMessageSubject());
                    msg.setBody(email.getMessageBody());
                    msg.setRecipientType(opsUserRepo.findById(contact.getDt_RowId()).get().getUserType());
                    msg.setDateCreated(new Date());
                    messageRepo.save(msg);
                } catch (Exception me) {
                    throw new MessageException(String.format(messageSource.getMessage("message.send.failure", null, locale), msg.getRecipient()), me);
                }
            }

            try {
                email.setReceiverEmail(contact.getEmail());
                mailService.send(email);
            } catch (MailAuthenticationException mae) {
                throw new MessageException(messageSource.getMessage("mail.connect.failure", null, locale), mae);
            } catch (MailSendException mse) {
                logger.error("Failed to send mail to {}", contact.getEmail(), mse);
            } catch (MailException me) {
                throw new MessageException(messageSource.getMessage("mail.send.failure", null, locale), me);
            }
        }
        return messageSource.getMessage("mail.send.success", null, locale);
    }


}
