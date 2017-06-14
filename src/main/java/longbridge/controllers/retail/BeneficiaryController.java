package longbridge.controllers.retail;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 4/3/2017.
 * Modified by Wunmi
 */

@Controller
@RequestMapping("/retail/beneficiary")
public class BeneficiaryController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LocalBeneficiaryService localBeneficiaryService;
    private MessageSource messages;

    private InternationalBeneficiaryService internationalBeneficiaryService;

    private FinancialInstitutionService financialInstitutionService;

    private RetailUserService retailUserService;
    @Value("${bank.code}")
    private String bankCode;

    private CodeService codeService;


    @Autowired
    public BeneficiaryController(LocalBeneficiaryService localBeneficiaryService, MessageSource messages, InternationalBeneficiaryService internationalBeneficiaryService, FinancialInstitutionService financialInstitutionService, RetailUserService retailUserService, CodeService codeService) {
        this.localBeneficiaryService = localBeneficiaryService;
        this.messages = messages;
        this.internationalBeneficiaryService = internationalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.retailUserService = retailUserService;
        this.codeService = codeService;
    }

    @GetMapping
    public String getBeneficiaries(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());


        Iterable<LocalBeneficiary> localBeneficiary = localBeneficiaryService.getLocalBeneficiaries(retailUser);
        List<LocalBeneficiary> localBeneficiaries = StreamSupport.stream(localBeneficiary.spliterator(), false)
                .filter(Objects::nonNull)
                .filter(i -> financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank()) != null)
                 .collect(Collectors.toList());

        localBeneficiaries.forEach(i-> i.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank()).getInstitutionName()));


        model.addAttribute("localBen",
                localBeneficiaries);

        Iterable<InternationalBeneficiary> intBeneficiary = internationalBeneficiaryService.getInternationalBeneficiaries(retailUser);
        for (InternationalBeneficiary intBenef : intBeneficiary) {
            intBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(intBenef.getBeneficiaryBank()).getInstitutionName());
        }
        model.addAttribute("intBen", intBeneficiary);
        return "cust/beneficiary/view";
    }

    @GetMapping("/new")
    public String addBeneficiary(Model model) {
        model.addAttribute("localBeneficiaryDTO", new LocalBeneficiaryDTO());
        model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
        model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
        model.addAttribute("foreignCurrencyCodes", codeService.getCodesByType("CURRENCY"));

        return "cust/beneficiary/add";
    }

    @PostMapping("/local")
    public String createLocalBeneficiary(@Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, Locale locale) {
        if (result.hasErrors()) {
            model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
            model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));

            return "cust/beneficiary/add";
        }

        try {
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message = localBeneficiaryService.addLocalBeneficiary(user, localBeneficiaryDTO);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {

            try{
                redirectAttributes.addFlashAttribute("failure", messages.getMessage(e.getMessage(), null, locale));
            }catch (Exception ex){
                redirectAttributes.addFlashAttribute("failure", messages.getMessage("beneficiary.add.failure", null, locale));

            }






        }
        return "redirect:/retail/beneficiary";
    }

    @PostMapping("/foreign")
    public String createForeignBeneficiary(@ModelAttribute("internationalBeneficiaryDTO") @Valid InternationalBeneficiaryDTO internationalBeneficiaryDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        if (result.hasErrors()) {

            model.addAttribute("localBeneficiaryDTO", new LocalBeneficiaryDTO());
            model.addAttribute("internationalBeneficiaryDTO", internationalBeneficiaryDTO);
            model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
            return "cust/beneficiary/add";
        }

        try {
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message = internationalBeneficiaryService.addInternationalBeneficiary(user, internationalBeneficiaryDTO);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }


    @GetMapping("/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId, Model model) {
        Beneficiary localBeneficiary = localBeneficiaryService.getLocalBeneficiary(beneficiaryId);
        model.addAttribute("beneficiary", localBeneficiary);
        return localBeneficiary;
    }


//    @GetMapping
//    public Iterable<Beneficiary> getLocalBeneficiaries(Model model){
//        Iterable<Beneficiary> localBeneficiaries = beneficiaryService.getLocalBeneficiaries(user);
//        model.addAttribute("beneficiaries",localBeneficiaries);
//        return localBeneficiaries;
//    }

    @GetMapping("/{beneficiaryId}/loc/delete")
    public String deleteLocBeneficiary(@PathVariable Long beneficiaryId, Model model, RedirectAttributes redirectAttributes) {

        try {
            String message = localBeneficiaryService.deleteLocalBeneficiary(beneficiaryId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }

    @GetMapping("/{beneficiaryId}/int/delete")
    public String deleteIncBeneficiary(@PathVariable Long beneficiaryId, Model model, RedirectAttributes redirectAttributes) {

        try {
            String message = internationalBeneficiaryService.deleteInternationalBeneficiary(beneficiaryId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }

    @ModelAttribute
    public void getBankCode(Model model) {
        model.addAttribute("bankCode", bankCode);
    }

   @ModelAttribute
    public void getLocalBanks(Model model){
        List<FinancialInstitutionDTO> dtos = financialInstitutionService.getOtherLocalBanks(bankCode);
        dtos.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));
       model.addAttribute("localBanks",dtos );
   }

}
