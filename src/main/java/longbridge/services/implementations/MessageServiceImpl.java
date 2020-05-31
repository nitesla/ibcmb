package longbridge.services.implementations;

import longbridge.dtos.CorporateDTO;
import longbridge.dtos.MessageDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.MailBoxRepo;
import longbridge.repositories.MessageRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.MessageCategory;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private MailService mailService;
    @Autowired
    private EntityManager entityManager;

    private Locale locale = LocaleContextHolder.getLocale();

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
        return mailBoxRepo.findOneById(id);
    }

    @Override
    public MessageDTO getMessage(Long id) {
        return convertEntityToDTO(this.messageRepo.findOneById(id));
    }

    @Override
    @Transactional
    public String addMessage(User sender, MessageDTO messageDTO) {

        try {
            if (sender instanceof RetailUser||sender instanceof CorporateUser) {
                SettingDTO setting = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
                if (setting != null && setting.isEnabled()) {
                    messageDTO.setRecipient(setting.getValue());
                    Message message = convertDTOToEntity(messageDTO);
                    message.setDateCreated(new Date());
                    sendEmail(messageDTO);
                }

            }

            Message message = convertDTOToEntity(messageDTO);
            message.setSender(sender.getUserName());
            message.setDateCreated(new Date());
            message.setStatus("Unread");
            message.setTag(MessageCategory.SENT.toString());
            MailBox senderMailBox = mailBoxRepo.findByUserIdAndUserType(sender.getId(), sender.getUserType());
            if (senderMailBox == null) {
                senderMailBox = new MailBox(sender.getId(), sender.getUserType());
            }
            message.setMailBox(senderMailBox);
            senderMailBox.getMessages().add(message);
            mailBoxRepo.save(senderMailBox);
            return messageSource.getMessage("message.add.success", null, locale);

        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("message.add.failure",null,locale),e);
        }
    }
    @Override
    @Transactional
    public String addMessage(User sender, MessageDTO messageDTO, String category) {
        String recipient=null;
        try {
            if (sender instanceof RetailUser||sender instanceof CorporateUser) {
                SettingDTO setting = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
                if (setting != null && setting.isEnabled()) {
                    logger.info("the recipient {} ",setting.getValue());
                    messageDTO.setRecipient(setting.getValue());
                    recipient=setting.getValue().split("@")[0];
                    Message message = convertDTOToEntity(messageDTO);
                    message.setDateCreated(new Date());
                    sendEmail(messageDTO);
                }

            }
            String initialMessageTag=messageDTO.getTag();
            Message message = convertDTOToEntity(messageDTO);
            message.setSender(sender.getUserName());
            message.setRecipient(recipient);
            message.setRecipientType(UserType.OPERATIONS);
            message.setDateCreated(new Date());

            if(category.equalsIgnoreCase("S")) {
                message.setTag(MessageCategory.SENT.toString());
            }else {
                message.setTag(MessageCategory.DRAFT.toString());
            }
            MailBox senderMailBox = mailBoxRepo.findByUserIdAndUserType(sender.getId(), sender.getUserType());
            if (senderMailBox == null) {
                senderMailBox = new MailBox(sender.getId(), sender.getUserType());
            }
            message.setMailBox(senderMailBox);
            List<Message> messages=senderMailBox.getMessages();
            if(initialMessageTag.equalsIgnoreCase("DRAFT")){
                for(Message message1:messages){
                    if(messageDTO.getId().equals(message1.getId())){
                        message1.setTag(MessageCategory.DRAFT_SENT.toString());
                    }
                }
            }

            message.setId(null);
                senderMailBox.setMessages(messages);
            senderMailBox.getMessages().add(message);
            mailBoxRepo.save(senderMailBox);
            return messageSource.getMessage("message.add.success", null, locale);

        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("message.add.failure",null,locale),e);
        }
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
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public Iterable<Message> getMessages(User user, Date fromDate, Date toDate) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Iterable<Message> getMessage(Date fromDate, Date toDate) {
        return this.messageRepo.findByDateCreatedBetween(fromDate, toDate);
    }

    @Override
    @Transactional
    public void setStatus(Long id, String status) {
        Message message = this.messageRepo.getOne(id);
        message.setStatus(status);
        this.messageRepo.save(message);
    }

    @Override
    public String deleteSentMessage(User user,Long id)throws InternetBankingException{
        try {
            Message message = new Message();
            message.setId(id);
            MailBox mailBox = mailBoxRepo.findByUserIdAndUserType(user.getId(), user.getUserType());
            mailBox.getMessages().remove(message);
            mailBoxRepo.save(mailBox);
            return messageSource.getMessage("message.delete.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("message.delete.failure",null,locale),e);
        }

    }

    @Override
    public String deleteReceivedMessage(Long id)throws InternetBankingException {
        try {
            this.messageRepo.delete(id);
            return messageSource.getMessage("message.delete.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("message.delete.failure",null,locale),e);
        }
    }

    @Override
    public String purge(int daysOld) throws InternetBankingException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String sendRetailContact(String msg, RetailUser user) {
        try {
            SettingDTO settingDTO = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
            String email = settingDTO.getValue();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Customer Name:  " + user.getFirstName() + " " + user.getLastName() + "\n\n");
            stringBuilder.append("Customer Email:  " + user.getEmail() + "\n\n");
            stringBuilder.append("Request Time:  " + new Date() + "\n\n\n\n");
            stringBuilder.append(" " + msg + "\n\n");
            String message = stringBuilder.toString();

            mailService.send(email, "CUSTOMER SUPPORT REQUEST", message,false);
            return messageSource.getMessage("contactus.send.success", null, locale);
        }catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("contactus.send.failure", null, locale),e);
        }
    }

    @Override
    public String sendCorporateContact(String msg, CorporateDTO corporate) {
        try {
            SettingDTO settingDTO = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
            String email = settingDTO.getValue();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Customer Name:  " + corporate.getName() + "\n\n");
            stringBuilder.append("Customer Email:  " + corporate.getEmail() + "\n\n");
            stringBuilder.append("Request Time:  " + new Date() + "\n\n\n\n");
            stringBuilder.append(" " + msg + "\n\n");
            String message = stringBuilder.toString();

            mailService.send(email, "CUSTOMER SUPPORT REQUEST", message,false);
            return messageSource.getMessage("contactus.send.success", null, locale);
        }catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("contactus.send.failure", null, locale),e);
        }
    }


    @Override
    @Transactional
    public List<MessageDTO> getSentMessages(User user) {
        List<Message>  messages = messageRepo.findMessageByUserAndTag(user.getId(), user.getUserType(),MessageCategory.SENT.toString());
        logger.info("the message {}",messages);
        List<MessageDTO> sentMessages = new ArrayList<MessageDTO>();
        if (messages == null) {
            return sentMessages;
        }
        sentMessages = convertEntitiesToDTOs(messages);
//        return reverse(sentMessages);
        return sentMessages.stream().sorted(Comparator.comparing(MessageDTO::getCreatedOn).reversed()).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public List<MessageDTO> getMessagesByTag(User user, MessageCategory messageCategory) {
        List<Message>  messages = messageRepo.findMessageByUserAndTag(user.getId(), user.getUserType(),messageCategory.toString());
        logger.info("the {} messages are {}",messageCategory,messages.size());
        List<MessageDTO> sentMessages = new ArrayList<MessageDTO>();
        if (messages == null) {
            return sentMessages;
        }
        sentMessages = convertEntitiesToDTOs(messages);
        return reverse(sentMessages);
    }
    @Override
    public Page<MessageDTO> getPagedMessagesByTag(User user,Pageable pageable, MessageCategory messageCategory) {
        Page<Message>  messages = messageRepo.findPagedMessageByUserAndTag(user.getId(), user.getUserType(),messageCategory.toString(),pageable);

        List<MessageDTO> dtOs = convertEntitiesToDTOs(messages.getContent());
        long t = messages.getTotalElements();
        Page<MessageDTO> pageImpl = new PageImpl<MessageDTO>(dtOs, pageable, t);
        return pageImpl;
    }
    @Override
    @Transactional
    public Long countMessagesByTag(User user, MessageCategory messageCategory) {
        Long  messagesCount = messageRepo.countMessageByUserAndTag(user.getId(), user.getUserType(),messageCategory.toString());
        return messagesCount;
    }



    @Override
    public Page<MessageDTO> getReceivedMessages(String recipient, UserType recipientTye, Pageable pageable) {
        Page<Message> page = messageRepo.findByRecipientIgnoreCaseAndRecipientTypeOrderByIdDesc(recipient, recipientTye,pageable);
        List<MessageDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<MessageDTO> pageImpl = new PageImpl<MessageDTO>(dtOs, pageable, t);
        return pageImpl;

    }

    @Override
    public Page<MessageDTO> getReceivedMessages(String recipient, UserType recipientTye, java.awt.print.Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public List<MessageDTO> getReceivedMessages(User user) {
        List<Message> receivedMessages = messageRepo.findByRecipientIgnoreCaseAndRecipientTypeAndTagOrderByIdDesc(user.getUserName(),user.getUserType(),MessageCategory.SENT.toString());
        return convertEntitiesToDTOs(receivedMessages);
    }

    public int getNumOfUnreadMessages(User user){
        int count = 0;
        List<Message> receivedMessages = messageRepo.findByRecipientAndRecipientTypeOrderByIdDesc(user.getUserName(),user.getUserType());
        for(Message message: receivedMessages){
            if("Unread".equals(message.getStatus())){
                count+=1;
            }
        }
        return count;
    }

    @Override
    public String purge(Date fromDate, Date toDate) {
        Iterable<Message> messages = getMessage(fromDate, toDate);
        this.messageRepo.deleteInBatch(messages);
        return messageSource.getMessage("message.purge.success",null,locale);
    }

    @Override
    public String sendMessage(User sender, User recipient, MessageDTO message) throws InternetBankingException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void sendEmail(MessageDTO messageDTO) {
        Email email = new Email.Builder()
                .setRecipient(messageDTO.getRecipient())
                .setSubject(messageDTO.getSubject())
                .setBody(messageDTO.getBody())
                .build();
        mailService.send(email);
    }

    @Override
    public Page<Message> getMessages(User user, java.awt.print.Pageable pageDetails) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Page<MessageDTO> getMessages(User user, Pageable pageDetails) {
        Page<Message> messages = messageRepo.findByRecipientIgnoreCaseAndRecipientTypeOrderByIdDesc(user.getUserName(),user.getUserType(),pageDetails);

        List<MessageDTO> dtOs = convertEntitiesToDTOs(messages.getContent());
        long t = messages.getTotalElements();
        Page<MessageDTO> pageImpl = new PageImpl<MessageDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Page<Message> getMessages(User user, Date date, java.awt.print.Pageable pageDetails) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Page<Message> getMessages(User user, Date fromDate, Date toDate, java.awt.print.Pageable pageDetails) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public Page<Message> getMessages(User user, Date fromDate, Date toDate, Pageable pageDetails) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private List<MessageDTO> reverse(List<MessageDTO> list) {
        for(int i = 0, j = list.size() - 1; i < j; i++) {
            list.add(i, list.remove(j));
        }
        return list;
    }

    private MessageDTO convertEntityToDTO(Message message) {
        MessageDTO messageDTO = this.modelMapper.map(message, MessageDTO.class);
        if(message.getDateCreated()!=null){
            messageDTO.setCreatedOn(DateFormatter.format(message.getDateCreated()));
        }
        return messageDTO;
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
        return messageDTOList.stream().sorted(Comparator.comparing(MessageDTO::getDateCreated).reversed()).collect(Collectors.toList());
    }
}
