package longbridge.controllers.retail;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.LocalBeneficiaryService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 5/19/2017.
 */
@Controller
@RequestMapping(value = "/retail/transfer/interbank")
public class InterBankTransferController {


    private RetailUserService retailUserService;
    private TransferService transferService;
    private MessageSource messages;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private String page = "cust/transfer/interbank/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public InterBankTransferController(RetailUserService retailUserService, TransferService transferService, MessageSource messages, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.messages = messages;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
    }

    @GetMapping(value = "")
    public String index() {

        return page + "pagei";
    }


    @PostMapping(value = "/index")

    public String startTransfer(HttpServletRequest request, Model model, Principal principal) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("localBen",
                StreamSupport.stream(localBeneficiaryService.getLocalBeneficiaries(retailUser).spliterator(), false)
                        .filter(i -> !i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode()))
                        .collect(Collectors.toList())


        );
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        String type = request.getParameter("tranType");

        if (type.equalsIgnoreCase("NIP")) {
            request.getSession().setAttribute("NIP", "NIP");
            requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        } else {
            request.getSession().setAttribute("NIP", "RTGS");
            requestDTO.setTransferType(TransferType.RTGS);
        }


        model.addAttribute("transferRequest", requestDTO);
        return page + "pageiA";
    }


    @GetMapping("/new")
    public String newBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {

        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        return page + "pageiB";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        if (result.hasErrors()) {
            return page + "pageiB";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(localBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(localBeneficiaryDTO.getAccountNumber());

        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(localBeneficiaryDTO.getBeneficiaryBank()));
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("benName",localBeneficiaryDTO.getPreferredName());

        servletRequest.getSession().setAttribute("Lbeneficiary", localBeneficiaryDTO);
        servletRequest.getSession().setAttribute("benName", localBeneficiaryDTO.getPreferredName());



        return page + "pageii";
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        model.addAttribute("transferRequest", transferRequestDTO);
        String benName = (String) request.getSession().getAttribute("benName");

        model.addAttribute("benName", benName);

        validator.validate(transferRequestDTO, result);

        if (result.hasErrors()) {
            System.out.println(result.getErrorCount());
            return page + "pageii";
        }

        if (request.getSession().getAttribute("NIP") != null) {
            String type = (String) request.getSession().getAttribute("NIP");
            if (type.equalsIgnoreCase("RTGS")) {
                transferRequestDTO.setTransferType(TransferType.RTGS);
            } else {
                transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
            }
            request.getSession().removeAttribute("NIP");

        } else {
            transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        }
        request.getSession().setAttribute("transferRequest", transferRequestDTO);
        return page + "pageiii";
    }

    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        LocalBeneficiary beneficiary = localBeneficiaryService.getLocalBeneficiary(id);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        FinancialInstitution institution = financialInstitutionService.getFinancialInstitutionByCode(beneficiary.getBeneficiaryBank());
        requestDTO.setFinancialInstitution(institution);

        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("beneficiary", beneficiary);
        model.addAttribute("benName", beneficiary.getPreferredName());
        request.getSession().setAttribute("benName", beneficiary.getPreferredName());
        return page + "pageii";
    }


}
