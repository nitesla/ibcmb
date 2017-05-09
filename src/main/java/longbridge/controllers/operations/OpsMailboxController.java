package longbridge.controllers.operations;

import longbridge.dtos.MessageDTO;
import longbridge.models.*;
import longbridge.services.CorporateUserService;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.security.Principal;
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
    @Autowired
    CorporateUserService corporateUserService;
    @Autowired
    RetailUserService retailUserService;


    Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/inbox")
    public String getInbox(Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);

        if (!receivedMessages.isEmpty()) {
            MessageDTO message = receivedMessages.get(0);
            model.addAttribute("messageDTO", message);

        }
        model.addAttribute("receivedMessages", receivedMessages);


        return "ops/mailbox/inbox";
    }

    @GetMapping("/newinbox")
    public String getNewInbox(Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);

        if (!receivedMessages.isEmpty()) {
            MessageDTO message = receivedMessages.get(0);
            model.addAttribute("messageDTO", message);

        }
        model.addAttribute("receivedMessages", receivedMessages);


        return "ops/mailbox/newinbox";
    }

    @GetMapping("/newcompose")
    public String getNewCompose(Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);

        if (!receivedMessages.isEmpty()) {
            MessageDTO message = receivedMessages.get(0);
            model.addAttribute("messageDTO", message);

        }
        model.addAttribute("receivedMessages", receivedMessages);


        return "ops/mailbox/newcompose";
    }

    @GetMapping("/viewmail")
    public String getViewMail(Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);

        if (!receivedMessages.isEmpty()) {
            MessageDTO message = receivedMessages.get(0);
            model.addAttribute("messageDTO", message);

        }
        model.addAttribute("receivedMessages", receivedMessages);


        return "ops/mailbox/viewmail";
    }



    @GetMapping("/outbox")
    public String getOutbox(Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getSentMessages(opsUser);

        if (!sentMessages.isEmpty()) {
            MessageDTO message  = sentMessages.get(0);
            model.addAttribute("messageDTO", message);
        }
        model.addAttribute("sentMessages", sentMessages);
        return "ops/mailbox/outbox";
    }

    @GetMapping("/{id}/reply")
    public String replyMessage(@PathVariable Long id,  Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setRecipient(message.getSender());
        message.setSender(opsUser.getUserName());
        message.setSubject("Re: "+message.getSubject());
        message.setBody("");
        model.addAttribute("messageDTO", message);
        return "ops/mailbox/compose";
    }

    @GetMapping("/{id}/forward")
    public String forwardMessage(@PathVariable Long id,  Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setSender(opsUser.getUserName());
        message.setRecipient("");
        message.setSubject("Fwd: "+message.getSubject());
        message.setBody("\n---\n"+message.getDateCreated()+"\n\""+message.getBody()+"\"");
        model.addAttribute("messageDTO", message);
        return "ops/mailbox/compose";
    }

    @GetMapping("/new")
    public String addMessage(Model model,Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        MessageDTO message = new MessageDTO();
        message.setSender(opsUser.getUserName());
        model.addAttribute("messageDTO", message);
        return "ops/mailbox/compose";
    }


    @PostMapping
    public String createMessage(@ModelAttribute("messageDTO") @Valid MessageDTO messageDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal){
        if(bindingResult.hasErrors()){
            bindingResult.addError(new ObjectError("Invalid", "Please fill in the required fields"));
            return "ops/mailbox/compose";
        }
        User recipient = null;
        switch (messageDTO.getRecipientType()){
            case OPERATIONS:
                recipient = operationsUserService.getUserByName(messageDTO.getRecipient());
                break;
            case CORPORATE:
                recipient = corporateUserService.getUserByName(messageDTO.getRecipient());
                break;
            case RETAIL:
                recipient = retailUserService.getUserByName(messageDTO.getRecipient());
                break;
        }

        if(recipient==null){
            bindingResult.addError(new ObjectError("Invalid", "Invalid recipient username"));
            return "ops/mailbox/compose";
        }

        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        messageService.addMessage(opsUser,recipient,messageDTO);
        redirectAttributes.addFlashAttribute("message","Message sent successfully");
        return "redirect:/ops/mailbox/outbox";
    }

    @GetMapping("/inbox/{id}/details")
    public String viewReceivedMessage(@PathVariable Long id, Model model, Principal principal) {
        MessageDTO message = messageService.getMessage(id);
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);
        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("messageDTO", message);
        return "ops/mailbox/inbox";
    }


    @GetMapping("/sent/{id}/details")
    public String viewSentMessage(@PathVariable Long id, Model model, Principal principal) {
        MessageDTO message = messageService.getMessage(id);
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getSentMessages(opsUser);
        model.addAttribute("messageDTO", message);
        model.addAttribute("sentMessages", sentMessages);
        return "ops/mailbox/outbox";
    }

    @GetMapping("/outbox/{id}/delete")
    public String deleteSentMessage(@PathVariable Long id, Principal principal,RedirectAttributes redirectAttributes) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        messageService.deleteSentMessage(opsUser,id);
        redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        return "redirect:/ops/mailbox/outbox";
    }

    @GetMapping("/inbox/{id}/delete")
    public String deleteReceivedMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        messageService.deleteReceivedMessage(id);
        redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        return "redirect:/ops/mailbox/inbox";
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<MessageDTO> getAllMessages(Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> messages = messageService.getMessages(opsUser);
        return messages;

    }

    @GetMapping("/inbox/all")
    @ResponseBody
    public Iterable<MessageDTO> getReceivedMessages(Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);
        return receivedMessages;
    }

    @GetMapping("/sent/all")
    @ResponseBody
    public Iterable<MessageDTO> getSentMessages(Principal principal) {
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> sentMessages = messageService.getSentMessages(opsUser);
        return sentMessages;

    }
}


