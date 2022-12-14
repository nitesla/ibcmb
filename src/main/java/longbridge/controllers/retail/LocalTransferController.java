package longbridge.controllers.retail;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 5/4/2017.
 */
@Controller
@RequestMapping("/retail/transfer/local")
public class LocalTransferController {

    private final RetailUserService retailUserService;
    private final TransferService transferService;
    private final LocalBeneficiaryService localBeneficiaryService;
    private final FinancialInstitutionService financialInstitutionService;
    private final TransferValidator validator;
    private final TransferErrorService transferErrorService;
    private final TransferUtils transferUtils;
    private final String page = "cust/transfer/local/";
    @Value("${bank.code}")
    private String bankCode;


    /*@Value("${geolocation.url}")
    private String geolocationUrl;

    @Value("${geolocation.key}")
    private String geolocationKey;
*/
    @Autowired
    public LocalTransferController(RetailUserService retailUserService, TransferValidator validator, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService
            , TransferUtils transferUtils) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.transferErrorService = transferErrorService;
        this.transferUtils = transferUtils;
    }

    @GetMapping("")
    public String index(Model model, Principal principal, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");
        try {
            transferUtils.validateTransferCriteria();
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            Iterable<LocalBeneficiary> cmbBeneficiaries = localBeneficiaryService.getBankBeneficiaries();

            List<LocalBeneficiary> beneficiaries = StreamSupport.stream(cmbBeneficiaries.spliterator(), false)
                    .collect(Collectors.toList());
            beneficiaries.forEach(i ->
                    {
                        i.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank()).getInstitutionName());
                        if (i.getPreferredName() == null && i.getAccountName() != null) i.setPreferredName(i.getAccountName());
                    }
            );

            model.addAttribute("localBen", beneficiaries
            );


            return page + "pagei";
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            model.addAttribute("failure", errorMessage);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/retail/dashboard";


        }
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest, Principal principal) {
        model.addAttribute("transferRequest", transferRequestDTO);
        validator.validate(transferRequestDTO, result);


        if (servletRequest.getSession().getAttribute("Lbeneficiary") != null) {
            LocalBeneficiaryDTO beneficiary = (LocalBeneficiaryDTO) servletRequest.getSession().getAttribute("Lbeneficiary");


            model.addAttribute("beneficiary", beneficiary);


        }
//        if (result.hasErrors()) {
//            return page + "pageii";
//        }

        try {
            transferService.validateTransfer(transferRequestDTO);
            transferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
            servletRequest.getSession().setAttribute("transferRequest", transferRequestDTO);


            return page + "pageiii";
        } catch (InternetBankingTransferException e) {

            String errorMessage = transferErrorService.getMessage(e);
            model.addAttribute("failure", errorMessage);
            return page + "pageii";
        }
    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        LocalBeneficiaryDTO beneficiary = localBeneficiaryService.convertEntityToDTO(localBeneficiaryService.getLocalBeneficiary(id));
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        String benPreferedName = beneficiary.getPreferredName()==null?"":beneficiary.getPreferredName();
        beneficiary.setPreferredName(benPreferedName);
        request.getSession().setAttribute("Lbeneficiary", beneficiary);
        return page + "pageii";
    }


    @GetMapping("/new")
    public String addWithinBankBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        return page + "pageiA";
    }

    @PostMapping("/new")
    public String newBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
            return page + "pageiA";
        }
        if (request.getSession().getAttribute("add") != null)
            request.getSession().removeAttribute("add");

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(localBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(localBeneficiaryDTO.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        request.getSession().setAttribute("Lbeneficiary", localBeneficiaryDTO);
        model.addAttribute("beneficiary", localBeneficiaryDTO);
        if (request.getParameter("add") != null)
            request.getSession().setAttribute("add", "add");

        return page + "pageii";
    }

    @ModelAttribute
    public void getLocalFinancialInstitutions(Model model) {

        model.addAttribute("localBanks",
                financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL)
                        .stream()
                        .filter(i -> !i.getInstitutionCode().equals(bankCode))
                        .collect(Collectors.toList())
                        .stream()
                        .sorted(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName))
                        .collect(Collectors.toList())
        );


    }


    @ModelAttribute
    public void getBankCode(Model model) {
        model.addAttribute("bankCode", bankCode);
        model.addAttribute("fee", transferUtils.getFee("INTRABANK"));
    }

    @ResponseBody
    @GetMapping("{amount}/fee")
    public String getInterBankTransferFee(@PathVariable("amount") String amount) {
        String fee="";
        try {
            fee=transferUtils.getFee("INTRABANK", amount);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return fee;

    }


    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("transferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        transferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        if (request.getSession().getAttribute("Lbeneficiary") != null)
            model.addAttribute("beneficiary", request.getSession().getAttribute("Lbeneficiary"));

        return page + "pageii";
    }

    @ModelAttribute
    public void setNairaSourceAccount(Model model) {
        model.addAttribute("accountList", transferUtils.getNairaAccounts());


    }

   /* @ModelAttribute
    public void setLocationParams(Model model) {
        model.addAttribute("key", geolocationKey);
        model.addAttribute("url", geolocationUrl);

    }*/

}
