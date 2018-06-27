package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Controller
@RequestMapping("/corporate/beneficiary")
public class CorpBeneficiaryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SecurityService securityService;
    @Autowired
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    @Autowired
    private CorpInternationalBeneficiaryService corpInternationalBeneficiaryService;
    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private MessageSource messages;

    @Value("${bank.code}")
    private String bankCode;


    @GetMapping
    public String getBeneficiaries(Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        logger.info("local BEN {}", corpLocalBeneficiaryService.getCorpLocalBeneficiaries(corporateUser.getCorporate()));
        Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries = corpLocalBeneficiaryService.getCorpLocalBeneficiaries(corporateUser.getCorporate());


        for (CorpLocalBeneficiary localBenef : corpLocalBeneficiaries) {

            String code = localBenef.getBeneficiaryBank();
            FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(code);
            if (financialInstitution != null) {
                String beneficiaryBank = financialInstitution.getInstitutionName();
                localBenef.setBeneficiaryBank(beneficiaryBank);
            }

        }
        model.addAttribute("localBen", corpLocalBeneficiaries);

       /* Iterable<CorpInterBen> intBeneficiary = corpInternationalBeneficiaryService.getCorpInternationalBeneficiaries(corporateUser);
        for (CorpInterBen intBenef : intBeneficiary){
            intBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(intBenef.getBeneficiaryBank()).getInstitutionName());
        }
        model.addAttribute("intBen", intBeneficiary);*/
        return "corp/beneficiary/view";
    }



    @GetMapping("/new")
    public String addBeneficiary(Model model) {
        model.addAttribute("corpLocalBeneficiaryDTO", new CorpLocalBeneficiaryDTO());
        model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
        model.addAttribute("foreignCurrencyCodes", codeService.getCodesByType("CURRENCY"));
        return "corp/beneficiary/add";
    }

    @PostMapping("/local")

    public String createCorpLocalBeneficiary(@Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result, Principal principal, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale) {

        model.addAttribute("bank", financialInstitutionService.getFinancialInstitutionByCode(corpLocalBeneficiaryDTO.getBeneficiaryBank()).getInstitutionName());
        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");
        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        if (/* service to check if token is enabled comes in here  */
                (setting != null && setting.isEnabled())
                ) {

            try {
                String token = request.getParameter("token");

                securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), token);
            } catch (InternetBankingSecurityException ibse) {

                if (/* service to check if token is enabled comes in here  */

                        (setting != null && setting.isEnabled())
                        )
                    model.addAttribute("auth", "auth");
                model.addAttribute("failure", ibse.getMessage());
                model.addAttribute("beneficiary", corpLocalBeneficiaryDTO);
                return "corp/beneficiary/localSummary";
            }
        }
        try {
            String message = corpLocalBeneficiaryService.addCorpLocalBeneficiary(corpLocalBeneficiaryDTO);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            try {
                redirectAttributes.addFlashAttribute("failure", messages.getMessage(e.getMessage(), null, locale));
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("failure", messages.getMessage("beneficiary.add.failure", null, locale));
            }
        }

        return "redirect:/corporate/beneficiary";
    }

    @PostMapping("/foreign")

    public String createForeignBeneficiary(@ModelAttribute("corpInternationalBeneficiary") @Valid CorpInternationalBeneficiaryDTO corpInternationalBeneficiaryDTO, Principal principal, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "corp/beneficiary/add";
        }
        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        corpInternationalBeneficiaryService.addCorpInternationalBeneficiary(user, corpInternationalBeneficiaryDTO);
        model.addAttribute("success", "Beneficiary added successfully");
        return "redirect:/corporate/beneficiary";
    }


    @GetMapping("/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId, Model model) {
        Beneficiary localBeneficiary = corpLocalBeneficiaryService.getCorpLocalBeneficiary(beneficiaryId);
        model.addAttribute("beneficiary", localBeneficiary);
        return localBeneficiary;
    }

    @PostMapping("/authenticate")
    public String delLocBenToken(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the ben tokeeen {}", token);
        String beneficiaryId = webRequest.getParameter("id");
        Long benefit = Long.parseLong(beneficiaryId);
        logger.info("this is the benID {}", benefit);
        if (token != "" && beneficiaryId != "") {
            try {
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = corpLocalBeneficiaryService.deleteCorpLocalBeneficiary(benefit);
                        redirectAttributes.addFlashAttribute("message", message);
                        return "redirect:/corporate/beneficiary";
                    } catch (InternetBankingException e) {
                        logger.error("International Beneficiary Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                    return "redirect:/corporate/beneficiary";
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                    return "redirect:/corporate/beneficiary";
                }
            } catch (InternetBankingException e) {
                logger.error("International Beneficiary Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/corporate/beneficiary";
            }
        } else {
            return "redirect:/corporate/beneficiary";
        }
    }


//    @GetMapping
//    public Iterable<Beneficiary> getLocalBeneficiaries(Model model){
//        Iterable<Beneficiary> localBeneficiaries = beneficiaryService.getLocalBeneficiaries(user);
//        model.addAttribute("beneficiaries",localBeneficiaries);
//        return localBeneficiaries;
//    }

    @GetMapping("/{beneficiaryId}/loc/delete")

    public String deleteLocBeneficiary(@PathVariable Long beneficiaryId, Model model) {
        corpLocalBeneficiaryService.deleteCorpLocalBeneficiary(beneficiaryId);
        model.addAttribute("success", "Beneficiary deleted successfully");

        return "redirect:/corporate/beneficiary";
    }

    @GetMapping("/{beneficiaryId}/int/delete")

    public String deleteIncBeneficiary(@PathVariable Long beneficiaryId, Model model) {
        corpInternationalBeneficiaryService.deleteCorpInternationalBeneficiary(beneficiaryId);
        model.addAttribute("success", "Beneficiary deleted successfully");
        return "redirect:/corporate/beneficiary";
    }

    @PostMapping("/local/summary")
    public String createLocalBeneficiary(@Valid CorpLocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result);

            model.addAttribute("corpLocalBeneficiaryDTO", new CorpLocalBeneficiaryDTO());
//            model.addAttribute("corpInternationalBeneficiaryDTO", new CorpInternationalBeneficiaryDTO());
//            model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));


            return "corp/beneficiary/add";
        }

        try {
            model.addAttribute("bank", financialInstitutionService.getFinancialInstitutionByCode(localBeneficiaryDTO.getBeneficiaryBank()).getInstitutionName());
            model.addAttribute("beneficiary", localBeneficiaryDTO);
            SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");


            if (/* service to check if token is enabled comes in here  */

                    (setting != null && setting.isEnabled())
                    )
                model.addAttribute("auth", "auth");

        } catch (InternetBankingException e) {


        }
        return "corp/beneficiary/localSummary";
    }


    @ModelAttribute
    public void getOtherLocalBanks(Model model) {
        List<FinancialInstitutionDTO> sortedBanks =
                financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL)
                        .stream()
                        .filter(i -> !i.getInstitutionCode().equalsIgnoreCase(bankCode))
                        .collect(Collectors.toList());

        sortedBanks.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));
        model.addAttribute("localBanks", sortedBanks);
        model.addAttribute("bankCode", bankCode);


    }


}



