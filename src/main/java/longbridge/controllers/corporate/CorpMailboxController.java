package longbridge.controllers.corporate;

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
 * Created by Kwere on 5/25/2017.
 */
@Controller
@RequestMapping("/corporate/mailbox")
public class CorpMailboxController {

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
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(corporateUser);

        //  if (!receivedMessages.isEmpty()) {
        //      MessageDTO message = receivedMessages.get(0);
        //      model.addAttribute("messageDTO", message);
        //  }
        model.addAttribute("receivedMessages", receivedMessages);


        return "corp/mailbox/inbox";
    }

    @GetMapping("/sentmail")
    public String getOutbox(Model model,Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getSentMessages(corporateUser);

        //  if (!sentMessages.isEmpty()) {
        //      MessageDTO message  = sentMessages.get(0);
        //      model.addAttribute("messageDTO", message);
        //  }
        model.addAttribute("sentMessages", sentMessages);

        return "corp/mailbox/sentmail";
    }

    @GetMapping("/{id}/reply")
    public String replyMessage(@PathVariable Long id,  Model model,Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setRecipient(message.getSender());
        message.setSender(corporateUser.getUserName());
        message.setSubject("Re: "+message.getSubject());
        message.setBody("");
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/compose";
    }

    @GetMapping("/{id}/forward")
    public String forwardMessage(@PathVariable Long id,  Model model,Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setSender(corporateUser.getUserName());
        message.setRecipient("");
        message.setSubject("Fwd: "+message.getSubject());
        message.setBody("\n---\n"+message.getDateCreated()+"\n\""+message.getBody()+"\"");
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/compose";
    }

    @GetMapping("/compose")
    public String addMessage(Model model,Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        MessageDTO message = new MessageDTO();
        message.setSender(corporateUser.getUserName());
        message.setRecipient("joykwere@yahoo.com");
        //if(!message.getStatus()==not sent){
        //
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/compose";
    }

    @PostMapping
    public String createMessage(@ModelAttribute("messageDTO") @Valid MessageDTO messageDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal){
        if(bindingResult.hasErrors()){
            bindingResult.addError(new ObjectError("Invalid", "Please fill in the required fields"));
            return "corp/mailbox/compose";
        }
        User recipient = null;
        switch (messageDTO.getRecipientType()){
            case OPERATIONS:
                recipient = operationsUserService.getUserByName(messageDTO.getRecipient());
                break;
            case RETAIL:
                recipient = retailUserService.getUserByName(messageDTO.getRecipient());
                break;
        }

        if(recipient==null){
            bindingResult.addError(new ObjectError("Invalid", "Invalid recipient username"));
            return "corp/mailbox/compose";
        }

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        messageService.addMessage(corporateUser,recipient,messageDTO);
        redirectAttributes.addFlashAttribute("message","Message sent successfully");
        return "redirect:/corp/mailbox/outbox";
    }

    @GetMapping("/inbox/{id}/message")
    public String viewReceivedMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/message";
    }

    @GetMapping("/sent/{id}/message")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/message";
    }

    @GetMapping("/inbox/message/{id}/delete")
    public String deleteReceivedMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        messageService.deleteReceivedMessage(id);
        redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        return "redirect:/corp/mailbox/inbox";
    }

    @GetMapping("/sentmail/message/{id}/delete")
    public String deleteSentMessage(@PathVariable Long id, Principal principal,RedirectAttributes redirectAttributes) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        messageService.deleteSentMessage(corporateUser,id);
        redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        return "redirect:/corp/mailbox/sentmail";
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<MessageDTO> getAllMessages(Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> messages = messageService.getMessages(corporateUser);
        return messages;

    }

    @GetMapping("/inbox/all")
    @ResponseBody
    public Iterable<MessageDTO> getReceivedMessages(Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(corporateUser);
        return receivedMessages;
    }

    @GetMapping("/sent/all")
    @ResponseBody
    public Iterable<MessageDTO> getSentMessages(Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> sentMessages = messageService.getSentMessages(corporateUser);
        return sentMessages;

    }
    @GetMapping("/message")
    public String getMessage(Model model,Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(corporateUser);
        MessageDTO message = receivedMessages.get(0);
        //  if (!receivedMessages.isEmpty()) {
        //      MessageDTO message = receivedMessages.get(0);
        //      model.addAttribute("messageDTO", message);
        //  }
        model.addAttribute("messageDTO", message);


        return "corp/mailbox/message";
    }


}
