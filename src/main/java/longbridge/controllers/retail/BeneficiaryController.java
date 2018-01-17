package longbridge.controllers.retail;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.*;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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


    Locale locale = LocaleContextHolder.getLocale();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LocalBeneficiaryService localBeneficiaryService;
    private MessageSource messages;

    private InternationalBeneficiaryService internationalBeneficiaryService;

    private FinancialInstitutionService financialInstitutionService;
    private SecurityService securityService;

    private RetailUserService retailUserService;

    private MessageSource messageSource;


    @Value("${bank.code}")
    private String bankCode;
    private ConfigurationService configService;

    private CodeService codeService;


    @Autowired
    public BeneficiaryController(LocalBeneficiaryService localBeneficiaryService, MessageSource messages, InternationalBeneficiaryService internationalBeneficiaryService, FinancialInstitutionService financialInstitutionService, RetailUserService retailUserService, CodeService codeService
            , ConfigurationService configService, SecurityService securityService, MessageSource messageSource
    ) {
        this.localBeneficiaryService = localBeneficiaryService;
        this.messages = messages;
        this.internationalBeneficiaryService = internationalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.retailUserService = retailUserService;
        this.codeService = codeService;
        this.configService = configService;
        this.securityService = securityService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String getBeneficiaries(Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());


        Iterable<LocalBeneficiary> localBeneficiary = localBeneficiaryService.getLocalBeneficiaries(retailUser);
        List<LocalBeneficiary> localBeneficiaries = StreamSupport.stream(localBeneficiary.spliterator(), false)
                .filter(Objects::nonNull)
                .filter(i -> financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank()) != null)
                .collect(Collectors.toList());

        localBeneficiaries.forEach(i -> i.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank()).getInstitutionName()));


        model.addAttribute("localBen",
                localBeneficiaries);

        Iterable<InternationalBeneficiary> intBeneficiary = internationalBeneficiaryService.getInternationalBeneficiaries(retailUser);
        for (InternationalBeneficiary intBenef : intBeneficiary) {
            intBenef.setBeneficiaryBank(intBenef.getBeneficiaryBank());
        }
        model.addAttribute("intBen", intBeneficiary);
        return "cust/beneficiary/view";
    }

    @GetMapping("/new")
    public String addBeneficiary(Model model) {
        model.addAttribute("localBeneficiaryDTO", new LocalBeneficiaryDTO());
        model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
        model.addAttribute("foreignCurrencyCodes", codeService.getCodesByType("CURRENCY"));
        List<FinancialInstitutionDTO> financialInstitutionDTOS = financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL);
        financialInstitutionDTOS
                .stream()
                .sorted(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));
        model.addAttribute("localBanks", financialInstitutionDTOS);
        return "cust/beneficiary/add";
    }

    @PostMapping("/local")
    public String createLocalBeneficiary(@Valid LocalBeneficiaryDTO localBeneficiaryDTO, Principal principal, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request) {

        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");

        if (/* service to check if token is enabled comes in here  */

                (setting != null && setting.isEnabled())
                ) {

            try {
                model.addAttribute("bank", financialInstitutionService.getFinancialInstitutionByCode(localBeneficiaryDTO.getBeneficiaryBank()).getInstitutionName());
                String token = request.getParameter("token");
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
            } catch (InternetBankingSecurityException ibse) {
                if (/* service to check if token is enabled comes in here  */

                        (setting != null && setting.isEnabled())
                        )
                    model.addAttribute("auth", "auth");
                model.addAttribute("failure", ibse.getMessage());
                model.addAttribute("beneficiary", localBeneficiaryDTO);
                return "cust/beneficiary/localSummary";
            }

        }

        try {

            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message2 = localBeneficiaryService.addLocalBeneficiary(user, localBeneficiaryDTO);


            redirectAttributes.addFlashAttribute("message", message2);
        } catch (InternetBankingException e) {
            e.printStackTrace();
            try {
                redirectAttributes.addFlashAttribute("failure", messages.getMessage(e.getMessage(), null, locale));
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("failure", messages.getMessage("beneficiary.add.failure", null, locale));

            }
        }
        return "redirect:/retail/beneficiary";
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
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = localBeneficiaryService.deleteLocalBeneficiary(benefit);
                        redirectAttributes.addFlashAttribute("message", message);
                    } catch (InternetBankingException e) {
                        logger.error("Beneficiary Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                    return "redirect:/retail/beneficiary";
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                    return "redirect:/retail/beneficiary";
                }
            } catch (InternetBankingException e) {
                logger.error("International Beneficiary Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/retail/beneficiary";
            }
        } else {
            return "redirect:/retail/beneficiary";
        }
    }


    @PostMapping("/foreign")
    public String createForeignBeneficiary(@ModelAttribute("internationalBeneficiaryDTO") @Valid InternationalBeneficiaryDTO internationalBeneficiaryDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        if (result.hasErrors()) {

            model.addAttribute("localBeneficiaryDTO", new LocalBeneficiaryDTO());
            model.addAttribute("internationalBeneficiaryDTO", internationalBeneficiaryDTO);
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
            logger.error("Beneficiary Error", e);
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
    public void getLocalBanks(Model model) {
        List<FinancialInstitutionDTO> dtos = financialInstitutionService.getOtherLocalBanks(bankCode);
        dtos.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));
        model.addAttribute("localBanks", dtos);
    }


    @PostMapping("/local/summary")
    public String createLocalBeneficiary(@Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model, Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
            return "cust/beneficiary/add";
        }

        try {
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            boolean exist = localBeneficiaryService.doesBeneficiaryExist(retailUser, localBeneficiaryDTO);
            if (exist) {
                //model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
                model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
                model.addAttribute("failure", messageSource.getMessage("beneficiary.add.exist", null, locale));
                return "cust/beneficiary/add";
            }


            model.addAttribute("bank", financialInstitutionService.getFinancialInstitutionByCode(localBeneficiaryDTO.getBeneficiaryBank()).getInstitutionName());
            model.addAttribute("beneficiary", localBeneficiaryDTO);
            SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");


            if (/* service to check if token is enabled comes in here  */

                    (setting != null && setting.isEnabled())
                    )
                model.addAttribute("auth", "auth");

        } catch (InternetBankingException e) {


        }
        return "cust/beneficiary/localSummary";
    }

}
