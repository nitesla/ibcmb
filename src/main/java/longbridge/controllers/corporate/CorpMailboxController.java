package longbridge.controllers.corporate;

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

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kwere on 5/25/2017.
 */
@Controller
@RequestMapping("/corporate/mailbox")
public class CorpMailboxController {

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
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/inbox")
    public String getInbox(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(corporateUser);
        model.addAttribute("receivedMessages", receivedMessages);
        return "corp/mailbox/inbox";
    }

    @GetMapping("/sentmail")
    public String getOutbox(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getSentMessages(corporateUser);
        model.addAttribute("sentMessages", sentMessages);
        return "corp/mailbox/sentmail";
    }

    @GetMapping("/{id}/reply")
    public String replyMessage(@PathVariable Long id, Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setRecipient(message.getSender());
        message.setSender(corporateUser.getUserName());
        message.setSubject("Re: " + message.getSubject());
        message.setBody("");
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/compose";
    }

    @GetMapping("/{id}/forward")
    public String forwardMessage(@PathVariable Long id, Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setSender(corporateUser.getUserName());
        message.setRecipient("");
        message.setSubject("Fwd: " + message.getSubject());
        message.setBody("\n---\n" + message.getDateCreated() + "\n\"" + message.getBody() + "\"");
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/compose";
    }

    @GetMapping("/compose")
    public String addMessage(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        MessageDTO message = new MessageDTO();
        message.setSender(corporateUser.getUserName());
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/compose";
    }

    @PostMapping
    public String createMessage(@ModelAttribute("messageDTO") @Valid MessageDTO messageDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal, Model model, Locale locale) {
        if (messageDTO.getSubject() == null || messageDTO.getBody() == null) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "corp/mailbox/compose";
        }

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        try {
            messageService.addMessage(corporateUser, messageDTO);
            redirectAttributes.addFlashAttribute("message", "Message sent successfully");
            return "redirect:/corporate/mailbox/sentmail";
        } catch (InternetBankingException ibe) {
            logger.error("Error sending message", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            return "corp/mailbox/compose";

        }
    }

    @GetMapping("/inbox/{id}/message")
    public String viewReceivedMessage(@PathVariable Long id, Model model) {
        messageService.setStatus(id, "Read");
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/recievedmessage";
    }

    @GetMapping("/sent/{id}/message")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "corp/mailbox/sentmessage";
    }

    @GetMapping("/inbox/message/{id}/delete")
    public String deleteReceivedMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            messageService.deleteReceivedMessage(id);
            redirectAttributes.addFlashAttribute("message", "Message deleted successfully");
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/corporate/mailbox/inbox";

    }

    @GetMapping("/sent/message/{id}/delete")
    public String deleteSentMessage(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
            String message = messageService.deleteSentMessage(corporateUser, id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/corporate/mailbox/sentmail";
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
    public String getMessage(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(corporateUser);

        if (!receivedMessages.isEmpty()) {
            MessageDTO message = receivedMessages.get(0);
            model.addAttribute("messageDTO", message);
        }
        model.addAttribute("receivedMessages", receivedMessages);
        return "corp/mailbox/message";
    }

}
