package longbridge.controllers.admin;

import longbridge.dtos.FaqsDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.FaqsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Showboy on 24/06/2017.
 */
@Controller
@RequestMapping("/admin/faq")
public class AdmFaqController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private FaqsService faqsService;

    @GetMapping
    public String getSecurityQuestions() {
        return "/adm/faq/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<FaqsDTO> getAllsecQuestions(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FaqsDTO> sq = faqsService.getFaqs(pageable);
        DataTablesOutput<FaqsDTO> out = new DataTablesOutput<FaqsDTO>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/new")
    public String addFaq(FaqsDTO faqsDTO) {
        return "adm/faq/add";
    }

    @PostMapping
    public String createFaq(@ModelAttribute("faqsDTO") @Valid FaqsDTO faqsDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "adm/faq/add";
        }

        try {
            String message = faqsService.addFaq(faqsDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/faq";
        }
        catch (InternetBankingException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            logger.error("Error creating code",ibe);
            return "adm/faq/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editCode(@PathVariable Long id, Model model) {
        FaqsDTO faq = faqsService.getFaq(id);
        model.addAttribute("faq", faq);
        return "adm/faq/edit";
    }

    @PostMapping("/update")
    public String updateCode(@ModelAttribute("faq") @Valid FaqsDTO faqsDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        if(result.hasErrors()){
            logger.error("Error occurred creating faq{}", result.toString());
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "adm/faq/edit";

        }
        try {
            String message = faqsService.updateFaq(faqsDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/faq";
        }
        catch (InternetBankingException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            logger.error("Error updating Code",ibe);
            return "adm/faq/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteCode(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String message = faqsService.deleteFaq(id);
            redirectAttributes.addFlashAttribute("message", message);
        }
        catch (InternetBankingException ibe){
            logger.error("Error deleting Code",ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
        }
        return "redirect:/admin/faq";
    }
}
