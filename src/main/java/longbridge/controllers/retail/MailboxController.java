package longbridge.controllers.retail;

import longbridge.dtos.MessageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.services.CorporateUserService;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kwere on 5/14/2017.
 */
@Controller
@RequestMapping("/retail/mailbox")
public class MailboxController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private OperationsUserService operationsUserService;
    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private MessageSource messageSource;


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/inbox")
    public String getInbox(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);

        //  if (!receivedMessages.isEmpty()) {
        //      MessageDTO message = receivedMessages.get(0);
        //      model.addAttribute("messageDTO", message);
        //  }
        model.addAttribute("receivedMessages", receivedMessages);


        return "cust/mailbox/inbox";
    }

    @GetMapping("/sentmail")
    public String getOutbox(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getSentMessages(retailUser);

        //  if (!sentMessages.isEmpty()) {
        //      MessageDTO message  = sentMessages.get(0);
        //      model.addAttribute("messageDTO", message);
        //  }
        model.addAttribute("sentMessages", sentMessages);

        return "cust/mailbox/sentmail";
    }

    @GetMapping("/{id}/reply")
    public String replyMessage(@PathVariable Long id, Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setRecipient(message.getSender());
        message.setSender(retailUser.getUserName());
        message.setSubject("Re: " + message.getSubject());
        message.setBody("");
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/compose";
    }

    @GetMapping("/{id}/forward")
    public String forwardMessage(@PathVariable Long id, Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setSender(retailUser.getUserName());
        message.setRecipient("");
        message.setSubject("Fwd: " + message.getSubject());
        message.setBody("\n---\n" + message.getDateCreated() + "\n\"" + message.getBody() + "\"");
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/compose";
    }

    @GetMapping("/compose")
    public String addMessage(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        MessageDTO message = new MessageDTO();
        message.setSender(retailUser.getUserName());
        //if(!message.getStatus()==not sent){
        //
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/compose";
    }

    @PostMapping
    public String createMessage(@ModelAttribute("messageDTO") MessageDTO messageDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, Principal principal, Locale locale) {
        if (messageDTO.getSubject() == null || messageDTO.getBody() == null) {
            model.addAttribute("failre", messageSource.getMessage("form.fields.required",null,locale));
            return "cust/mailbox/compose";
        }


        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        try {
            String message = messageService.addMessage(retailUser, messageDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/retail/mailbox/sentmail";
        } catch (InternetBankingException ibe) {
            logger.error("Error sending message", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            return "cust/mailbox/compose";

        }
    }

    @GetMapping("/inbox/{id}/message")
    public String viewReceivedMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/message";
    }

    @GetMapping("/sent/{id}/message")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/message";
    }

    @GetMapping("/inbox/message/{id}/delete")
    public String deleteReceivedMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        messageService.deleteReceivedMessage(id);
        redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        return "redirect:/cust/mailbox/inbox";
    }

    @GetMapping("/sentmail/message/{id}/delete")
    public String deleteSentMessage(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        messageService.deleteSentMessage(retailUser, id);
        redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        return "redirect:/cust/mailbox/sentmail";
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable<MessageDTO> getAllMessages(Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> messages = messageService.getMessages(retailUser);
        return messages;

    }

    @GetMapping("/inbox/all")
    @ResponseBody
    public Iterable<MessageDTO> getReceivedMessages(Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);
        return receivedMessages;
    }

    @GetMapping("/sent/all")
    @ResponseBody
    public Iterable<MessageDTO> getSentMessages(Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> sentMessages = messageService.getSentMessages(retailUser);
        return sentMessages;

    }

    @GetMapping("/message")
    public String getMessage(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);
        if (!receivedMessages.isEmpty()) {
            MessageDTO message = receivedMessages.get(0);
            model.addAttribute("messageDTO", message);
        }
        model.addAttribute("receivedMessages", receivedMessages);


        return "cust/mailbox/message";
    }


}
