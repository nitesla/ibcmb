package longbridge.controllers.retail;

import longbridge.dtos.MessageDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.RetailUser;
import longbridge.services.CorporateUserService;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import longbridge.services.RetailUserService;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.MessageCategory;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
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
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);
        Long noOfDraft = messageService.countMessagesByTag(retailUser,MessageCategory.DRAFT);
        Long noOfSent = messageService.countMessagesByTag(retailUser,MessageCategory.SENT);
        model.addAttribute("noOfSent","Sent ("+noOfSent+")");
        model.addAttribute("noOfInbox","Inbox ("+((List<MessageDTO>) receivedMessages).size()+")");
        model.addAttribute("noOfDraft", "Drafts ("+noOfDraft+")");
        model.addAttribute("receivedMessages", receivedMessages);

        return "cust/mailbox/inbox";
    }

    @GetMapping("/sentmail")
    public String getOutbox(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getMessagesByTag(retailUser, MessageCategory.SENT);
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);

        Long noOfDraft = messageService.countMessagesByTag(retailUser,MessageCategory.DRAFT);
        model.addAttribute("noOfSent","Sent ("+sentMessages.size()+")");
        model.addAttribute("noOfInbox","Inbox ("+((List<MessageDTO>) receivedMessages).size()+")");
        model.addAttribute("noOfDraft", "Drafts ("+noOfDraft+")");
        model.addAttribute("sentMessages", sentMessages);
        return "cust/mailbox/sentmail";
    }
    @GetMapping("/sentmails/list")
    @ResponseBody
    public DataTablesOutput<MessageDTO> getSentMails(DataTablesInput input, Principal principal) {
        logger.info("the username  {}",principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<MessageDTO> messages = null;
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
//        List<MessageDTO> sentMessages = messageService.getMessagesByTag(retailUser, MessageCategory.SENT);

        messages = messageService.getPagedMessagesByTag(retailUser,pageable, MessageCategory.SENT);
        DataTablesOutput<MessageDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(messages.getContent());
        out.setRecordsFiltered(messages.getTotalElements());
        out.setRecordsTotal(messages.getTotalElements());
        return out;
    }

    @GetMapping("/receivedmails/list")
    @ResponseBody
    public DataTablesOutput<MessageDTO> getReceivedMails(DataTablesInput input, Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        Page<MessageDTO> messages=messageService.getMessages(retailUser,pageable);
        DataTablesOutput<MessageDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(messages.getContent());
        out.setRecordsFiltered(messages.getTotalElements());
        out.setRecordsTotal(messages.getTotalElements());
        return out;
    }

    @GetMapping("/draft")
    public String getDraft(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<MessageDTO> draftMessages = messageService.getMessagesByTag(retailUser,MessageCategory.DRAFT);
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);

        Long noOfSent = messageService.countMessagesByTag(retailUser,MessageCategory.SENT);
        model.addAttribute("noOfInbox","("+((List<MessageDTO>) receivedMessages).size()+")");
        model.addAttribute("noOfSent","("+noOfSent+")");
        model.addAttribute("noOfDraft","("+draftMessages.size()+")");
        model.addAttribute("draftMessages", draftMessages);
        return "cust/mailbox/draft";
    }
    @GetMapping("/draftmails/list")
    @ResponseBody
    public DataTablesOutput<MessageDTO> getDraftMails(DataTablesInput input, Principal principal) {
        logger.info("the username  {}",principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<MessageDTO> messages = null;
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
//        List<MessageDTO> sentMessages = messageService.getMessagesByTag(retailUser, MessageCategory.SENT);

        messages = messageService.getPagedMessagesByTag(retailUser,pageable, MessageCategory.DRAFT);
        DataTablesOutput<MessageDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(messages.getContent());
        out.setRecordsFiltered(messages.getTotalElements());
        out.setRecordsTotal(messages.getTotalElements());
        return out;
    }
    @GetMapping("/{id}/reply")
    public String replyMessage(@PathVariable Long id, Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        MessageDTO message = messageService.getMessage(id);
        message.setRecipient(message.getSender());
        message.setSender(retailUser.getUserName());
        message.setSubject("Re: " + message.getSubject());
        message.setBody(message.getBody());
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
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/compose";
    }

    @PostMapping
    public String createMessage(@ModelAttribute("messageDTO") MessageDTO messageDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, Principal principal, Locale locale, WebRequest webRequest) {
        if (messageDTO.getSubject() == null || messageDTO.getBody() == null) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "cust/mailbox/compose";
        }
        String category = webRequest.getParameter("category");
        logger.debug("the category {}",category);
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        try {
            String message = messageService.addMessage(retailUser, messageDTO,category);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/retail/mailbox/sentmail";
        } catch (InternetBankingException ibe) {
            logger.error("Error sending message", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            return "cust/mailbox/compose";

        }
    }

    @GetMapping("/inbox/{id}/message")
    public String viewReceivedMessage(@PathVariable Long id, Model model,Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<MessageDTO> receivedMessages = messageService.getReceivedMessages(retailUser);
        Long noOfDraft = messageService.countMessagesByTag(retailUser,MessageCategory.DRAFT);
        Long noOfSent = messageService.countMessagesByTag(retailUser,MessageCategory.SENT);
        model.addAttribute("noOfSent","Sent ("+noOfSent+")");
        model.addAttribute("noOfInbox","Inbox ("+((List<MessageDTO>) receivedMessages).size()+")");
        model.addAttribute("noOfDraft", "Drafts ("+noOfDraft+")");
        model.addAttribute("receivedMessages", receivedMessages);


        messageService.setStatus(id, "Read");
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/receivedmessage";
    }

    @GetMapping("/sent/{id}/message")
    public String viewSentMessage(@PathVariable Long id, Model model) {
        MessageDTO message = messageService.getMessage(id);
        model.addAttribute("messageDTO", message);
        return "cust/mailbox/compose";
    }

    @GetMapping("/inbox/{id}/delete")
    public String deleteReceivedMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = messageService.deleteReceivedMessage(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/retail/mailbox/inbox";
    }

    @GetMapping("/sent/message/{id}/delete")
    public String deleteSentMessage(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            String message = messageService.deleteSentMessage(retailUser, id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/retail/mailbox/sentmail";
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
@GetMapping("/message/count")
@ResponseBody
    public JSONObject getOfMessages(Principal principal){
    JSONObject jsonObject =  new JSONObject();
    RetailUser retailUser = retailUserService.getUserByName(principal.getName());
    List<MessageDTO> sentMessages = messageService.getMessagesByTag(retailUser, MessageCategory.SENT);
    Long noOfInbox = messageService.countMessagesByTag(retailUser,MessageCategory.INBOX);
    Long noOfDraft = messageService.countMessagesByTag(retailUser,MessageCategory.DRAFT);
       jsonObject.put("noOfInbox","("+noOfInbox+")");
       jsonObject.put("noOfDraft","("+noOfDraft+")");
       jsonObject.put("noOfSent","("+sentMessages.size()+")");
    return jsonObject;
}

}
