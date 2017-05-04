package longbridge.services.implementations;

import longbridge.dtos.MessageDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.models.*;
import longbridge.repositories.MailBoxRepo;
import longbridge.repositories.MessageRepo;
import longbridge.services.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    ModelMapper modelMapper;
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
    public MailBox getMailBox(Long userId, UserType userType) {
        return mailBoxRepo.findByUserIdAndUserType(userId,userType);
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
    public void addMessage(MailBox mailBox,MessageDTO messageDTO) {
        Message message = convertDTOToEntity(messageDTO);
        message.setDateCreated(new Date());
        message.setStatus("SENT");
        message.setMailBox(mailBox);



        mailBox.getMessages().add(message);
        mailBoxRepo.save(mailBox);
    }

    public Iterable<MessageDTO> getMessages(){
        return convertEntitiesToDTOs(messageRepo.findAll());
    }

    @Override
    @Transactional
    public Iterable<MessageDTO> getMessages(MailBox mailBox) {
        return convertEntitiesToDTOs(messageRepo.findByMailBox(mailBox));
    }


    @Override
    public Iterable<Message> getMessages(MailBox mailBox, Date date) {
        return this.messageRepo.findByMailBoxAndDateCreated(mailBox, date);
    }


    @Override
    public Iterable<Message> getMessage(MailBox mailBox, Date fromDate, Date toDate) {
        return this.messageRepo.findByMailBoxAndDateCreatedBetween(mailBox, fromDate, toDate);
    }

    public Iterable<Message> getMessage(Date fromDate, Date toDate) {
        return this.messageRepo.findByDateCreatedBetween(fromDate, toDate);
    }

    @Override
    public void setStatus(Long id, String status) {
        Message mes = this.messageRepo.getOne(id);
        mes.setStatus(status);
        this.messageRepo.save(mes);
    }

    @Override
    public void deleteMessage(Long id) {
        this.messageRepo.delete(id);
    }

    @Override
    public void purge(int daysOld) {

    }

    @Override
    @Transactional
    public List<MessageDTO> getSentMessages(MailBox mailBox) {
        Iterable<Message> messages =  messageRepo.findByMailBoxOrderByIdDesc(mailBox);
        UserType userType = mailBox.getUserType();
        String username = null;
        switch (userType) {
            case ADMIN:
                username = adminUserService.getAdminUser(mailBox.getUserId()).getUserName();
                break;
            case OPERATIONS:
                username = operationsUserService.getUser(mailBox.getId()).getUserName();
                break;
            case RETAIL:
                username = retailUserService.getUser(mailBox.getId()).getUserName();
                break;
            case CORPORATE:
                username = corporateUserService.getUser(mailBox.getId()).getUserName();
                break;
            default:
                break;
        }
        List<MessageDTO> sentMessages = new ArrayList<MessageDTO>();

        for(Message message: messages){
            if(message.getSender().equals(username)){
                sentMessages.add(convertEntityToDTO(message));
            }
        }
        return sentMessages;
    }

    @Override
    @Transactional
    public List<MessageDTO> getReceivedMessages(MailBox mailBox) {
        Iterable<Message> messages =  messageRepo.findByMailBoxOrderByIdDesc(mailBox);
        UserType userType = mailBox.getUserType();
        String username = null;
        switch (userType) {
            case ADMIN:
                username = adminUserService.getAdminUser(mailBox.getUserId()).getUserName();
                break;
            case OPERATIONS:
                username = operationsUserService.getUser(mailBox.getId()).getUserName();
                break;
            case RETAIL:
                username = retailUserService.getUser(mailBox.getId()).getUserName();
                break;
            case CORPORATE:
                username = corporateUserService.getUser(mailBox.getId()).getUserName();
                break;
            default:
                break;
        }
        List<MessageDTO> receivedMessages = new ArrayList<MessageDTO>();

        for(Message message: messages){
            if(message.getRecipient().equals(username)){
                receivedMessages.add(convertEntityToDTO(message));
            }
        }
        return receivedMessages;
    }

    @Override
    public void purge(Date fromDate, Date toDate) {
        Iterable<Message> messages = getMessage(fromDate, toDate);
        this.messageRepo.delete(messages);


    }

    @Override
    public void sendMessage(User sender, User recipient, Message message) {

    }

    @Override
    public void sendEmail(EmailDetail email){
        //todo  send email
    }

	@Override
	public Page<Message> getMessages(MailBox mailbox, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Message> getMessages(MailBox mailbox, Date date, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Message> getMessages(MailBox mailbox, Date fromDate, Date toDate, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

    private MessageDTO convertEntityToDTO(Message message){
        return  this.modelMapper.map(message,MessageDTO.class);
    }


    private Message convertDTOToEntity(MessageDTO messageDTO){
        return this.modelMapper.map(messageDTO,Message.class);
    }

    private List<MessageDTO> convertEntitiesToDTOs(Iterable<Message> messages){
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for(Message message: messages){
            MessageDTO messageDTO = convertEntityToDTO(message);
            messageDTOList.add(messageDTO);
        }
        return messageDTOList;
    }
}
