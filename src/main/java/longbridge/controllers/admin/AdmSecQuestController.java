package longbridge.controllers.admin;

import longbridge.dtos.SecQuestionDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.SecurityQuestions;
import longbridge.services.SecurityQuestionService;
import longbridge.services.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by Showboy on 07/06/2017.
 */
@Controller
@RequestMapping("/admin/secquestions")
public class AdmSecQuestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private VerificationService verificationService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @GetMapping
    public String getSecurityQuestions() {
        return "/adm/secquestions/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<SecQuestionDTO> getAllsecQuestions(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<SecQuestionDTO> sq = securityQuestionService.getSecQuestions(pageable);
        DataTablesOutput<SecQuestionDTO> out = new DataTablesOutput<SecQuestionDTO>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/new")
    public String addSecurityQuestions() {
        return "/adm/secquestions/add";
    }


    @PostMapping
    public String createFinancialInstitution(WebRequest webRequest, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        String question = webRequest.getParameter("secQuestion");
        if (question == null || "".equals(question)) {
            logger.error("Error occurred creating code{}", "field is empty");
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/adm/secquestions/add";
        }


        try {
            String message = securityQuestionService.addSecQuestion(question);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/secquestions";
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred adding Security Question {}", ibe.toString());
            model.addAttribute("failure", messageSource.getMessage("secQues.add.failure", null, locale));
            return "/adm/secquestions/add";
        }

    }

    @GetMapping("/{id}/edit")
    public String editSecurityQuestion(@PathVariable Long id, Model model) {
        SecurityQuestions sec = securityQuestionService.getSecQuestion(id);
        model.addAttribute("secQuestion", sec);
        return "/adm/secquestions/edit";
    }

    @PostMapping("/update")
    public String updateFinancialInstitution(@ModelAttribute("secQuestion")  @Valid SecQuestionDTO secQuestionDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "/adm/secquestions/edit";
        }

        try {
            String message = securityQuestionService.updateSecQuestion(secQuestionDTO);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/secquestions";
        } catch (InternetBankingException ibe) {
            logger.error("Error occurred adding Security Question {}", ibe.toString());
            model.addAttribute("failure", messageSource.getMessage("secQues.add.failure", null, locale));
            return "/adm/secquestions/edit";
        }
    }


    @GetMapping("/{id}/delete")
    public String deleteSecurityQuestions(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = securityQuestionService.deleteSecQuestion(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Error occurred deleting security question ", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/admin/secquestions";

    }

}
