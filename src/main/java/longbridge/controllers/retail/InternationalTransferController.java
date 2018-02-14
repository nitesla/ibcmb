package longbridge.controllers.retail;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.FinancialInstitution;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.CodeService;
import longbridge.services.InternationalBeneficiaryService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 5/19/2017.
 */
@Controller
@RequestMapping(value = "/retail/transfer/international")
public class InternationalTransferController {

    @Autowired
    TransferErrorService transferErrorService;

    private RetailUserService retailUserService;
    private TransferService transferService;
    private MessageSource messages;
    private CodeService codeService;
    private InternationalBeneficiaryService beneficiaryService;
    private TransferValidator validator;
    private String page = "cust/transfer/international/";

    @Autowired
    public InternationalTransferController(CodeService codeService, RetailUserService retailUserService, TransferService transferService, MessageSource messages, TransferValidator validator, InternationalBeneficiaryService beneficiaryService) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.codeService = codeService;
        this.messages = messages;
        this.beneficiaryService = beneficiaryService;
        this.validator = validator;
    }

    @GetMapping("")
    public String index(Model model, Principal principal) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("interBen",
                StreamSupport.stream(beneficiaryService.getInternationalBeneficiaries(retailUser).spliterator(), false)
                        .collect(Collectors.toList())

        );

        return page + "pagei";
    }


    @PostMapping(value = "/index")
    public String startTransfer(HttpServletRequest request, Model model) {

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);

        model.addAttribute("transferRequest", requestDTO);
        return page + "pageiA";
    }


    @GetMapping("/new")
    public String newBeneficiary(Model model, InternationalBeneficiaryDTO internationalBeneficiaryDTO) throws Exception {
        model.addAttribute("foreignCurrencyCodes", codeService.getCodesByType("CURRENCY"));
        return page + "pageiA";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("internationalBeneficiary") @Valid InternationalBeneficiaryDTO internationalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("beneficiary", internationalBeneficiaryDTO);
        if (result.hasErrors()) {
            return page + "pageiB";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(internationalBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(internationalBeneficiaryDTO.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        FinancialInstitution financialInstitution = new FinancialInstitution();
        financialInstitution.setInstitutionCode(internationalBeneficiaryDTO.getBeneficiaryBank());
        transferRequestDTO.setFinancialInstitution(financialInstitution);
        model.addAttribute("transferRequest", transferRequestDTO);


        servletRequest.getSession().setAttribute("Ibeneficiary", internationalBeneficiaryDTO);
        servletRequest.getSession().setAttribute("benName", internationalBeneficiaryDTO.getAccountName());

        return page + "pageii";
    }



    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest) {
        model.addAttribute("transferRequest", transferRequestDTO);
        validator.validate(transferRequestDTO, result);
        if (servletRequest.getSession().getAttribute("benName") != null) {
           String benName = (String) servletRequest.getSession().getAttribute("benName");
            model.addAttribute("benName", benName);
        }
        if (servletRequest.getSession().getAttribute("Ibeneficiary") != null) {
            InternationalBeneficiaryDTO beneficiary = (InternationalBeneficiaryDTO) servletRequest.getSession().getAttribute("Ibeneficiary");
            model.addAttribute("beneficiary", beneficiary);
        }
        if (result.hasErrors()) {
            return page + "pageii";
        }
        try {
            transferService.validateTransfer(transferRequestDTO);
            transferRequestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
            servletRequest.getSession().setAttribute("transferRequest", transferRequestDTO);
            servletRequest.getSession().removeAttribute("benName");
            return page + "pageiii";
        } catch (InternetBankingTransferException e) {

            String errorMessage = transferErrorService.getMessage(e);

            model.addAttribute("failure", errorMessage);
            return page + "pageii";

        }

    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        InternationalBeneficiary beneficiary = beneficiaryService.getInternationalBeneficiary(id);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        FinancialInstitution financialInstitution = new FinancialInstitution();
        financialInstitution.setInstitutionCode(beneficiary.getBeneficiaryBank());
        financialInstitution.setInstitutionName(beneficiary.getBeneficiaryBank());
        requestDTO.setFinancialInstitution(financialInstitution);

        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("beneficiary", beneficiary);
        model.addAttribute("benName",  beneficiary.getAccountName());

        request.getSession().setAttribute("benName", beneficiary.getAccountName());
        return page + "pageii";
    }


}
