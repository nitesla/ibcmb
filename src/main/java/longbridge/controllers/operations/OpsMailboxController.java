package longbridge.controllers.operations;

import longbridge.dtos.MessageDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.models.MailBox;
import longbridge.models.Message;
import longbridge.models.UserType;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by Fortune on 5/1/2017.
 */

@Controller
@RequestMapping("/ops/mailbox")
public class OpsMailboxController {

    @Autowired
    MessageService messageService;
    @Autowired
    OperationsUserService operationsUserService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void init(Model model) {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(1L, UserType.OPERATIONS);
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(mailBox);
        List<MessageDTO> sentMessages = messageService.getSentMessages(mailBox);

        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("sentMessages", sentMessages);

    }

    @GetMapping("/inbox")
    public String getInbox(Model model) {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(opsUser.getId(), UserType.OPERATIONS);
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(mailBox);
        MessageDTO message = new MessageDTO();
        if (!receivedMessages.isEmpty()) {
            message = receivedMessages.get(0);
        }
        model.addAttribute("message", message);

        return "ops/mailbox/inbox";
    }

    @GetMapping("/sent")
    public String getOutbox(Model model) {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(opsUser.getId(), UserType.OPERATIONS);
        List<MessageDTO> sentMessages = messageService.getSentMessages(mailBox);
        MessageDTO message = new MessageDTO();
        if (!sentMessages.isEmpty()) {
            message = sentMessages.get(0);
        }
        model.addAttribute("message", message);
        return "ops/mailbox/outbox";
    }

    @GetMapping("/new")
    public String addMessage(Model model) {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MessageDTO message = new MessageDTO();
        message.setSender(opsUser.getUserName());
        model.addAttribute("message", message);
        return "ops/mailbox/compose";
    }


    @PostMapping
    public String createMessage(@ModelAttribute("message") MessageDTO messageDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "ops/mailbox/compose";
        }
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(opsUser.getId(), opsUser.getUserType());

        messageService.addMessage(mailBox,messageDTO);
        redirectAttributes.addFlashAttribute("message","Message sent succcessfully");
        return "redirect:/ops/mailbox/sent";
    }

    @GetMapping("/inbox/{id}/details")
    public String viewReceivedMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("message", message);
        return "ops/mailbox/inbox";
    }


    @GetMapping("/sent/{id}/details")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("message", message);
        return "ops/mailbox/outbox";
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<MessageDTO> getAllMessages() {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(1L);
        Iterable<MessageDTO> messages = messageService.getMessages();
        return messages;

    }


    @GetMapping("/inbox/all")
    @ResponseBody
    public Iterable<MessageDTO> getReceivedMessages() {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(opsUser.getId(), UserType.OPERATIONS);
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(mailBox);
        return receivedMessages;
    }

    @GetMapping("/sent/all")
    @ResponseBody
    public Iterable<MessageDTO> getSentMessages() {
        OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
        MailBox mailBox = messageService.getMailBox(1L);
//        logger.info("Mailbox is {}",mailBox.toString());
        Iterable<MessageDTO> sentMessages = messageService.getSentMessages(mailBox);
        return sentMessages;

    }
}


