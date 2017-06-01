package longbridge.controllers.retail;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    private RetailUserService retailUserService;
    private TransferService transferService;
    private MessageSource messages;
    private InternationalBeneficiaryService beneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private String page = "cust/transfer/international/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public InternationalTransferController(RetailUserService retailUserService, TransferService transferService, MessageSource messages, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator, InternationalBeneficiaryService beneficiaryService) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.messages = messages;
        this.beneficiaryService = beneficiaryService;
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
        model.addAttribute("interBen",
                StreamSupport.stream(beneficiaryService.getInternationalBeneficiaries(retailUser).spliterator(), false)
                        .filter(i -> i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode()))
                        .collect(Collectors.toList())

        );
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);



        model.addAttribute("transferRequest", requestDTO);
        return page + "pageiA";
    }


    @GetMapping("/new")
    public String newBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks",
                financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL)
                        .stream()
                        .filter(i -> i.getInstitutionCode().equals(bankCode))
                        .collect(Collectors.toList())
        );

        return page + "pageiB";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("localBeneficiary") @Valid InternationalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("beneficiaryDTO", localBeneficiaryDTO);
        if (result.hasErrors()) {
            return page + "pageiB";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(localBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(localBeneficiaryDTO.getAccountNumber());

        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(localBeneficiaryDTO.getBeneficiaryBank()));
        model.addAttribute("transferRequest", transferRequestDTO);


        servletRequest.getSession().setAttribute("Lbeneficiary", localBeneficiaryDTO);

        return page + "pageii";
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        model.addAttribute("transferRequest", transferRequestDTO);
        String benName = (String) request.getSession().getAttribute("benName");

        model.addAttribute("benName", benName);

        validator.validate(transferRequestDTO, result);

        if (result.hasErrors()) {

            return page + "pageii";
        }


        request.getSession().setAttribute("transferRequest", transferRequestDTO);
        return page + "pageiii";
    }

    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        InternationalBeneficiary beneficiary = beneficiaryService.getInternationalBeneficiary(id);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);

        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("beneficiary", beneficiary);
        model.addAttribute("benName", beneficiary.getIntermediaryBankName());
        request.getSession().setAttribute("benName", beneficiary.getIntermediaryBankName());
        return page + "pageii";
    }


}
