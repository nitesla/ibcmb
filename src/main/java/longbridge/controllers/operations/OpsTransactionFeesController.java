package longbridge.controllers.operations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.TransactionFeeDTO;
import longbridge.services.CodeService;
import longbridge.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import longbridge.utils.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by SYLVESTER on 4/26/2017.
 * Modified by Fortune
 */
@Controller
@RequestMapping("/ops/fees")
public class OpsTransactionFeesController {


    @Autowired
    private CodeService codeService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private TransactionService transactionService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @ModelAttribute
    public void init(Model model) {
        Iterable<CodeDTO> transactionTypes = codeService.getCodesByType("TRANSACTION_TYPE");
        Iterable<CodeDTO> currencies = codeService.getCodesByType("CURRENCY");
        model.addAttribute("transactionTypes", transactionTypes);
        model.addAttribute("currencies", currencies);
    }

    @GetMapping("/new")
    public String addTransactionFee(Model model) {
        TransactionFeeDTO transactionFeeDTO = new TransactionFeeDTO();
        model.addAttribute("transactionFee", transactionFeeDTO);
        return "ops/fee/add";
    }

    @PostMapping
    public String createTransactionFee(@ModelAttribute("transactionFee") @Valid TransactionFeeDTO transactionFeeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "ops/fee/add";
        }

        try {
            transactionService.addTransactionFee(transactionFeeDTO);
        } catch (DataAccessException dae) {
            logger.error("Could not create transaction fee {}", dae.toString());
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("fee.exists", null, locale)));
            return "ops/fees/add";
        } catch (Exception exc) {
            logger.error("Could not create transaction fee {}", exc.toString());
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("fee.add.failure", null, locale)));
            return "ops/fee/add";
        }

        logger.info("Fee created for transaction {}", transactionFeeDTO.getTransactionType());
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("fee.add.success", null, locale));
        return "redirect:/ops/fees";
    }

    @GetMapping("/{id}/edit")
    public String editTransactionFee(Model model, @PathVariable Long id) {
        TransactionFeeDTO transactionFeeDTO = transactionService.getTransactionFee(id);
        model.addAttribute("transactionFee", transactionFeeDTO);
        return "ops/fee/edit";
    }

    @PostMapping("/update")
    public String updateTransactionFee(@ModelAttribute("transactionFee") @Valid TransactionFeeDTO transactionFeeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Locale locale) {
        if (bindingResult.hasErrors()) {
            bindingResult.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "ops/fee/edit";
        }

        try {
            transactionService.updateTransactionFee(transactionFeeDTO);
        } catch (Exception exc) {
            logger.error("Could not update transaction fee {}", exc.toString());
            bindingResult.addError(new ObjectError("exception", messageSource.getMessage("fee.update.failure", null, locale)));
            return "ops/fee/edit";
        }

        logger.info("Fee updated for transaction {}", transactionFeeDTO.getTransactionType());
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("fee.update.success", null, locale));
        return "redirect:/ops/fees";
    }

    @GetMapping
    public String getTransactionFees() {
        return "ops/fee/view";
    }

    @GetMapping("/all")
    public
    @ResponseBody
    DataTablesOutput<TransactionFeeDTO> getTransactionFees(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<TransactionFeeDTO> transactionFees = transactionService.getTransactionFees(pageable);
        DataTablesOutput<TransactionFeeDTO> out = new DataTablesOutput<TransactionFeeDTO>();
        out.setDraw(input.getDraw());
        out.setData(transactionFees.getContent());
        out.setRecordsFiltered(transactionFees.getTotalElements());
        out.setRecordsTotal(transactionFees.getTotalElements());
        return out;
    }


    @GetMapping("/{id}/delete")
    public String deleteTransactionFee(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        TransactionFeeDTO transactionFeeDTO = transactionService.getTransactionFee(id);
        transactionService.deleteTransactionFee(id);
        logger.warn("Fee deleted for transaction {}", transactionFeeDTO.getTransactionType());
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("fee.delete.success", null, locale));
        return "redirect:/ops/fees";
    }
}
