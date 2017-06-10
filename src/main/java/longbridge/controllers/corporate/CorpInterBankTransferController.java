package longbridge.controllers.corporate;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.*;
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

@Controller
@RequestMapping(value = "/corporate/transfer/interbank")
public class CorpInterBankTransferController {


    private CorporateUserService corporateUserService;
    private CorpTransferService corpTransferService;
    private MessageSource messages;
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private String page = "corp/transfer/interbanktransfer/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public CorpInterBankTransferController(CorporateUserService corporateUserService, CorpTransferService corpTransferService, MessageSource messages, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator) {
        this.corporateUserService = corporateUserService;
        this.corpTransferService = corpTransferService;
        this.messages = messages;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
    }

    @GetMapping(value = "")
    public String index() {

        return page + "pagei";
    }


    @PostMapping(value = "/index")

    public String startTransfer(HttpServletRequest request, Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        model.addAttribute("localBen",
                StreamSupport.stream(corpLocalBeneficiaryService.getCorpLocalBeneficiaries(corporateUser.getCorporate()).spliterator(), false)
                        .filter(i -> i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode()))
                        .collect(Collectors.toList())

        );
        CorpTransferRequestDTO requestDTO= new CorpTransferRequestDTO();
        String type =request.getParameter("tranType") ;





        model.addAttribute("corpTransferRequest",requestDTO);
        return page + "pageiA";
    }


    @GetMapping("/new")
    public String newBeneficiary(@ModelAttribute("corpLocalBeneficiary") CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO,Model model ) throws Exception {
        model.addAttribute("localBanks",
                financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL)
                        .stream()
                        .filter(i -> !i.getInstitutionCode().equals(bankCode))
                        .collect(Collectors.toList())
        );

        return page + "pageiB";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("corpLocalBeneficiary") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("corpLocalBeneficiaryDTO", corpLocalBeneficiaryDTO);
        if (result.hasErrors()) {
            return page + "pageiB";
        }

        CorpTransferRequestDTO corpTransferRequestDTO = (CorpTransferRequestDTO) new TransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());

        corpTransferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(corpLocalBeneficiaryDTO.getBeneficiaryBank()));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);



        servletRequest.getSession().setAttribute("Lbeneficiary", corpLocalBeneficiaryDTO);

        return page + "pageii";
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        String benName = (String) request.getSession().getAttribute("benName");

        model.addAttribute("benName", benName);

        validator.validate(corpTransferRequestDTO, result);

        if (result.hasErrors()) {

            return page + "pageii";
        }

        if (request.getSession().getAttribute("NIP")!=null){
            String type = (String)request.getSession().getAttribute("NIP");
            if (type.equalsIgnoreCase("RTGS")){
                corpTransferRequestDTO.setTransferType(TransferType.RTGS);
            }else{
                corpTransferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
            }
            request.getSession().removeAttribute("NIP");

        }else{
            corpTransferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        }
        request.getSession().setAttribute("corpTransferRequest", corpTransferRequestDTO);
        return page + "pageiii";
    }

    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        CorpLocalBeneficiary beneficiary = corpLocalBeneficiaryService.getCorpLocalBeneficiary(id);
        CorpTransferRequestDTO requestDTO = new CorpTransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);

        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("beneficiary", beneficiary);
        model.addAttribute("benName", beneficiary.getPreferredName());
        request.getSession().setAttribute("benName", beneficiary.getPreferredName());
        return page + "pageii";
    }


}
