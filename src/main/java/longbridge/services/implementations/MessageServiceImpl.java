package longbridge.services.implementations;

import longbridge.dtos.MessageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.MailBoxRepo;
import longbridge.repositories.MessageRepo;
import longbridge.services.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MessageSource messageSource;
    Locale locale = LocaleContextHolder.getLocale();

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private MessageRepo messageRepo;
    private MailBoxRepo mailBoxRepo;
    private AdminUserService adminUserService;
    private OperationsUserService operationsUserService;
    private RetailUserService retailUserService;
    private CorporateUserService corporateUserService;

//    @Override
//    public MessageDTO getLastSentMessage(String sender) {
//        return convertEntityToDTO(messageRepo.findFirstBySenderOrderByIdDesc(sender));
//    }
//
//    @Override
//    public MessageDTO getLastReceivedMessage(String recipient) {
//        return convertEntityToDTO(messageRepo.findFirstBySenderOrderByIdDesc(recipient));
//    }

    @Autowired
    public MessageServiceImpl(MessageRepo messageRepo, MailBoxRepo mailBoxRepo, AdminUserService adminUserService, OperationsUserService operationsUserService, RetailUserService retailUserService, CorporateUserService corporateUserService) {
        this.messageRepo = messageRepo;
        this.mailBoxRepo = mailBoxRepo;
        this.adminUserService = adminUserService;
        this.operationsUserService = operationsUserService;
        this.retailUserService = retailUserService;
        this.corporateUserService = corporateUserService;
    }

    @Override
    @Transactional
    public MailBox getMailBox(User user) {
        return mailBoxRepo.findByUserIdAndUserType(user.getId(), user.getUserType());
    }

    @Override
    @Transactional
    public MailBox getMailBox(Long id) {
        return mailBoxRepo.findOne(id);
    }

    @Override
    public MessageDTO getMessage(Long id) {
        return convertEntityToDTO(this.messageRepo.findOne(id));
    }

    @Override
    @Transactional
    public String addMessage(User sender, User recipient, MessageDTO messageDTO) {

        Message message = convertDTOToEntity(messageDTO);
        message.setDateCreated(new Date());
        message.setStatus("Unread");
        MailBox senderMailBox = mailBoxRepo.findByUserIdAndUserType(sender.getId(), sender.getUserType());
        if (senderMailBox == null) {
            senderMailBox = new MailBox(sender.getId(), sender.getUserType());
        }
        message.setMailBox(senderMailBox);
        senderMailBox.getMessages().add(message);
        mailBoxRepo.save(senderMailBox);
        return messageSource.getMessage("message.add.success",null,locale);

    }

    public Iterable<MessageDTO> getMessages() {
        return convertEntitiesToDTOs(messageRepo.findAll());
    }

    @Override
    @Transactional
    public Iterable<MessageDTO> getMessages(User user) {
        MailBox mailBox = mailBoxRepo.findByUserIdAndUserType(user.getId(), user.getUserType());
        return convertEntitiesToDTOs(mailBox.getMessages());
    }


    @Override
    public Iterable<Message> getMessages(User user, Date date) {
//
        return null;
    }

    @Override
    public Iterable<Message> getMessages(User user, Date fromDate, Date toDate) {
//        return this.messageRepo.findByMailBoxAndDateCreatedBetween(mailBox, fromDate, toDate);
        return null; //TODO
    }

    public Iterable<Message> getMessage(Date fromDate, Date toDate) {
        return this.messageRepo.findByDateCreatedBetween(fromDate, toDate);
    }

    @Override
    public void setStatus(Long id, String status) {
        Message message = this.messageRepo.getOne(id);
        message.setStatus(status);
        this.messageRepo.save(message);
    }

    @Override
    public String deleteSentMessage(User user,Long id)throws InternetBankingException{
        Message message = new Message();
        message.setId(id);
        MailBox mailBox = mailBoxRepo.findByUserIdAndUserType(user.getId(),user.getUserType());
        mailBox.getMessages().remove(message);
        mailBoxRepo.save(mailBox);
        return messageSource.getMessage("message.delete.success",null,locale);
    }

    @Override
    public String deleteReceivedMessage(Long id)throws InternetBankingException {
        this.messageRepo.delete(id);
        return messageSource.getMessage("message.delete.success",null,locale);

    }

    @Override
    public String purge(int daysOld) throws InternetBankingException {
        return null;
    }

    @Override
    @Transactional
    public List<MessageDTO> getSentMessages(User user) {
        MailBox mailBox = mailBoxRepo.findByUserIdAndUserType(user.getId(), user.getUserType());
        List<MessageDTO> sentMessages = new ArrayList<MessageDTO>();
        if (mailBox == null) {
            return sentMessages;
        }
        sentMessages = convertEntitiesToDTOs(mailBox.getMessages());
        return reverse(sentMessages);
    }

    @Override
    @Transactional
    public List<MessageDTO> getReceivedMessages(User user) {
        List<Message> receivedMessages = messageRepo.findByRecipientAndRecipientTypeOrderByIdDesc(user.getUserName(),user.getUserType());
        return convertEntitiesToDTOs(receivedMessages);
    }

    @Override
    public String purge(Date fromDate, Date toDate) {
        Iterable<Message> messages = getMessage(fromDate, toDate);
        this.messageRepo.delete(messages);
        return messageSource.getMessage("message.purge.success",null,locale);
    }

    @Override
    public String sendMessage(User sender, User recipient, MessageDTO message) throws InternetBankingException {
        return null; //TODO
    }

    @Override
    public void sendEmail(Email email) {
        //todo  send email
    }

    @Override
    public Page<Message> getMessages(User user, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Message> getMessages(User user, Date date, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Message> getMessages(User user, Date fromDate, Date toDate, Pageable pageDetails) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<MessageDTO> reverse(List<MessageDTO> list) {
        for(int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }

    private MessageDTO convertEntityToDTO(Message message) {
        return this.modelMapper.map(message, MessageDTO.class);
    }


    private Message convertDTOToEntity(MessageDTO messageDTO) {
        return this.modelMapper.map(messageDTO, Message.class);
    }

    private List<MessageDTO> convertEntitiesToDTOs(Iterable<Message> messages) {
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messages) {
            MessageDTO messageDTO = convertEntityToDTO(message);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }
}
