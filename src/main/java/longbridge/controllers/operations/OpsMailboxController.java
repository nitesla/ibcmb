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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
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


//    @GetMapping("/inbox")
//    public String getInbox(Model model,Principal principal) {
//        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
//        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);
//
//        if (!receivedMessages.isEmpty()) {
//            MessageDTO message = receivedMessages.get(0);
//            model.addAttribute("messageDTO", message);
//
//        }
//        model.addAttribute("receivedMessages", receivedMessages);
//
//
//        return "ops/mailbox/inbox";
//    }

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


            MessageDTO message = new MessageDTO();
             message.setSender(principal.getName());
            model.addAttribute("messageDTO", message);


        model.addAttribute("receivedMessages", receivedMessages);


        return "ops/mailbox/newcompose";
    }

    @PostMapping("/newcompose")
    public String createMessages(@ModelAttribute("messageDTO") @Valid MessageDTO messageDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal){
        if(bindingResult.hasErrors()){
            bindingResult.addError(new ObjectError("Invalid", "Please fill in the required fields"));
            return "ops/mailbox/newcompose";
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
            return "ops/mailbox/newcompose";
        }

        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        messageService.addMessage(opsUser,recipient,messageDTO);
        redirectAttributes.addFlashAttribute("message","Message sent successfully");
        return "redirect:/ops/mailbox/outbox";
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

    @GetMapping("/viewmail/{id}/details")
    public String showMail(@PathVariable Long id, Model model, Principal principal,MessageDTO messageDTO) {
        MessageDTO message = messageService.getMessage(id);
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);
        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("messageDTO", message);
        return "ops/mailbox/newinbox";

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
        return "ops/mailbox/newsent";
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
        return "ops/mailbox/newcompose";
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
        return "ops/mailbox/newcompose";
    }

//    @GetMapping("/new")
//    public String addMessage(Model model,Principal principal) {
//        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
//        MessageDTO message = new MessageDTO();
//        message.setSender(opsUser.getUserName());
//        model.addAttribute("messageDTO", message);
//        return "ops/mailbox/compose";
//    }


//    @PostMapping
//    public String createMessage(@ModelAttribute("messageDTO") @Valid MessageDTO messageDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal){
//        if(bindingResult.hasErrors()){
//            bindingResult.addError(new ObjectError("Invalid", "Please fill in the required fields"));
//            return "ops/mailbox/newcompose";
//        }
//        User recipient = null;
//        switch (messageDTO.getRecipientType()){
//            case OPERATIONS:
//                recipient = operationsUserService.getUserByName(messageDTO.getRecipient());
//                break;
//            case CORPORATE:
//                recipient = corporateUserService.getUserByName(messageDTO.getRecipient());
//                break;
//            case RETAIL:
//                recipient = retailUserService.getUserByName(messageDTO.getRecipient());
//                break;
//        }
//
//        if(recipient==null){
//            bindingResult.addError(new ObjectError("Invalid", "Invalid recipient username"));
//            return "ops/mailbox/compose";
//        }
//
//        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
//        messageService.addMessage(opsUser,recipient,messageDTO);
//        redirectAttributes.addFlashAttribute("message","Message sent successfully");
//        return "redirect:/ops/mailbox/outbox";
//    }

//    @GetMapping("/inbox/{id}/details")
//    public String viewReceivedMessage(@PathVariable Long id, Model model, Principal principal) {
//        MessageDTO message = messageService.getMessage(id);
//        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
//        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(opsUser);
//        model.addAttribute("receivedMessages", receivedMessages);
//        model.addAttribute("messageDTO", message);
//        return "ops/mailbox/inbox";
//    }


    @GetMapping("/sent/{id}/details")
    public String viewSentMessage(@PathVariable Long id, Model model, Principal principal) {
        MessageDTO message = messageService.getMessage(id);
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        List<MessageDTO> sentMessages = messageService.getSentMessages(opsUser);
        model.addAttribute("messageDTO", message);
        model.addAttribute("sentMessages", sentMessages);
        return "ops/mailbox/newsent";
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

    @GetMapping("/sentmessages")
    public
    @ResponseBody
    DataTablesOutput<MessageDTO> getSentMessages(DataTablesInput input,Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        OperationsUser opsUser = operationsUserService.getUserByName(principal.getName());
        Page<MessageDTO> sentMessages = messageService.getSentMessages(opsUser.getUserName(),opsUser.getUserType(),pageable);
        DataTablesOutput<MessageDTO> out = new DataTablesOutput<MessageDTO>();
        out.setDraw(input.getDraw());
        out.setData(sentMessages.getContent());
        out.setRecordsFiltered(sentMessages.getTotalElements());
        out.setRecordsTotal(sentMessages.getTotalElements());
        return out;
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


