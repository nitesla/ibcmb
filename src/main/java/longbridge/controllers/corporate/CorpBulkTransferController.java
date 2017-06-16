package longbridge.controllers.corporate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.BulkTransferDTO;
import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import longbridge.services.BulkTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Controller
@RequestMapping("/corporate/bulktransfer")
public class CorpBulkTransferController {
    @Autowired
    private MessageSource messageSource;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private BulkTransferService bulkTransferService;

    @Autowired
    public CorpBulkTransferController(BulkTransferService bulkTransferService) {
        this.bulkTransferService = bulkTransferService;
    }

    @PostMapping
    public String saveTransfer(WebRequest request, RedirectAttributes redirectAttributes, Model model, Locale locale, HttpServletRequest httpServletRequest){


        try {
            String requests = request.getParameter("requests");
            String debitAccount = request.getParameter("debitAccount");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            List<CreditRequest> requestList = mapper.readValue(requests, new TypeReference<List<CreditRequest>>() {
            });

            BulkTransfer bulkTransfer = new BulkTransfer();
            bulkTransfer.setCreditRequestList(requestList);
            //bulkTransfer.setDebitAccount();
            //bulkTransfer.setDate();
            //bulkTransfer.setStatus();

            String message = bulkTransferService.makeBulkTransferRequest(bulkTransfer);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/groups";
        } catch (Exception ibe) {
            logger.error("Error creating transfer", ibe);
            model.addAttribute("failure", messageSource.getMessage("bulk.transfer.failure", null, locale));
            return "/corp/transfer/bulktransfer/add";
        }
    }


}
