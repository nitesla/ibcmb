package longbridge.controllers.retail;

import longbridge.dtos.*;
import longbridge.models.Account;
import longbridge.models.NeftBeneficiary;
import longbridge.models.NeftTransfer;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/retail/transfer")
public class NEFTTransferController {

    @Autowired
    private MessageSource messageSource;

    private final AccountService accountService;
    private final RetailUserService retailUserService;
    private NeftBeneficiaryService neftBeneficiaryService;
    private final String page = "cust/transfer/bulktransfer/neft/";
    private final CodeService codeService;
    @Autowired
    IntegrationService integrationService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransferUtils transferUtils;



    @Autowired
    public NEFTTransferController(AccountService accountService, RetailUserService retailUserService, NeftBeneficiaryService neftBeneficiaryService, CodeService codeService) {
        this.accountService = accountService;
        this.retailUserService = retailUserService;
        this.neftBeneficiaryService = neftBeneficiaryService;
        this.codeService = codeService;
    }

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/bulk/neft/request")
    public String neftRequestsPage(){ return page + "neftrequest";}

    @RequestMapping(value = "/bulk/index", method = {RequestMethod.GET, RequestMethod.POST})
    public String startTransfer(HttpServletRequest request, Model model, Principal principal) {
        if(principal == null){
            return "redirect:/retail/logout";
        }

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        logger.info("Retail user : "+ retailUser);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        String type = request.getParameter("tranType");
        logger.info("type {} ", type);

        Iterable<NeftBeneficiary> neftBeneficiaries = neftBeneficiaryService.getNeftBeneficiaries();



        if ("NAPS".equalsIgnoreCase(type)) {
            return "/cust/transfer/bulktransfer/list";
        }  else if ("NEFT".equalsIgnoreCase(type))
            request.getSession().setAttribute("NIP", "NEFT");
            requestDTO.setTransferType(TransferType.NEFT_BULK);

            model.addAttribute("transferRequest", requestDTO);
            model.addAttribute("neftBeneficiaries", neftBeneficiaries);
            return page + "pageiAc";

    }

    @ModelAttribute
    public void getNeftbanks(Model model) {
        List<CodeDTO> bankNames = codeService.getCodesByType("NEFT_BANKS");
        Set<String> names = bankNames
                .stream()
                .map(CodeDTO::getDescription)
                .collect(Collectors.toSet());
        Collections.sort(new ArrayList<>(names));
        model.addAttribute("neftBanks"
                ,names);

    }

//    @ModelAttribute
//    public void getNeftCollectionType(Model model) {
//        List<CodeDTO> sortedNames = codeService.getCodesByType("NEFT_COLLECTION_TYPE");
//        model.addAttribute("neftCollectionTypes"
//                , sortedNames);
//
//    }

    @GetMapping("/bulk/neft")
    public String newNeftBeneficiary(Model model, NeftBeneficiaryDTO neftBeneficiaryDTO) throws Exception {

        model.addAttribute("neftBeneficiaryDTO", neftBeneficiaryDTO);
        return page + "pageiBN";
    }

    @PostMapping("/settle")
    @ResponseBody
    public String settleTransactions(){
        integrationService.submitNeftTransfer();
        return "successful";
    }


    @GetMapping(path = "/neft/all")
    public @ResponseBody
    DataTablesOutput<NeftTransfer> getAllNeftRequest(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<NeftTransfer> neftTransfers = null;
        neftTransfers = transactionService.getNeftUnsettledTransactions(pageable);
        DataTablesOutput<NeftTransfer> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(neftTransfers.getContent());
        out.setRecordsFiltered(neftTransfers.getTotalElements());
        out.setRecordsTotal(neftTransfers.getTotalElements());
        return out;
    }

    @ResponseBody
    @GetMapping("bulktransfer/{bankName}/branch")
    public List<CodeDTO> getNeftBankBranch(@PathVariable("bankName") String bankName) {
        return codeService.getCodesByTypeAndDescription("NEFT_BANKS", bankName);
    }

    @ResponseBody
    @GetMapping("user/account/{accountNumber}")
    public AccountDetailsDTO getTransferDetails(@PathVariable("accountNumber") String accountNumber, Model model) {
        Object obj = model.getAttribute("accounts");
        AccountDetailsDTO dto = new AccountDetailsDTO();

        if(obj != null){
            List<Account> accountList = (List<Account>) obj;
            Account account = accountList.stream()
                    .filter(Objects::nonNull)
                    .filter(account1 -> account1.getAccountNumber().equalsIgnoreCase(accountNumber))
                    .findFirst().get();

            dto.setCurrencyCode(account.getCurrencyCode());
            if(account.getCurrencyCode().equalsIgnoreCase("NGN")){
                dto.setCollectionType("31");
                dto.setInstrumentType("CR");
            }
        }
        return dto;
    }

    @PostMapping("/bulk/alpha")
    public String getBeneficiary(@ModelAttribute("neftBeneficiaryDTO") @Valid NeftBeneficiaryDTO neftBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest, Principal principal) throws Exception {
        model.addAttribute("neftBeneficiaryDTO", neftBeneficiaryDTO);
        if (servletRequest.getSession().getAttribute("add") != null)
            servletRequest.getSession().removeAttribute("add");
        if (result.hasErrors()) {
            return page + "pageiBN";
        }


        System.out.println("This is the beneficiary : " + neftBeneficiaryDTO);

        NeftTransferRequestDTO transferRequestDTO = new NeftTransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(neftBeneficiaryDTO.getBeneficiaryAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(neftBeneficiaryDTO.getBeneficiaryAccountNumber());
        transferRequestDTO.setBeneficiaryBVN(neftBeneficiaryDTO.getBeneficiaryBVN());
        transferRequestDTO.setBeneficiaryBankName(neftBeneficiaryDTO.getBeneficiaryBankName());
        transferRequestDTO.setBeneficiarySortCode(neftBeneficiaryDTO.getBeneficiarySortCode());

        logger.info("Beneficiary is {}",transferRequestDTO.getBeneficiaryAccountName());
        logger.info("Neft Transfer Request is {} ", transferRequestDTO);

        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("benName", neftBeneficiaryDTO.getBeneficiaryAccountName());

        servletRequest.getSession().setAttribute("Nbeneficiary", neftBeneficiaryDTO);

        if (servletRequest.getParameter("add") != null){
            servletRequest.getSession().setAttribute("add", "add");
        }
        return page + "pageiN2";
    }

    @GetMapping("bulktransfer/{id}")
    public String neftTransfer(@PathVariable Long id, Model model, HttpServletRequest request, Locale locale, RedirectAttributes attributes) throws Exception {
        NeftBeneficiary beneficiary = neftBeneficiaryService.getNeftBeneficiary(id);

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getBeneficiaryAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getBeneficiaryAccountNumber());
        requestDTO.setBeneficiaryBVN(beneficiary.getBeneficiaryBVN());
        requestDTO.setBeneficiarySortCode(beneficiary.getBeneficiarySortCode());
        requestDTO.setBeneficiaryBankName(beneficiary.getBeneficiaryBankName());
        requestDTO.setTransferType(TransferType.NEFT_BULK);
//
        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("beneficiary", neftBeneficiaryService.convertEntityToDTO(beneficiary));
        request.getSession().setAttribute("beneficiaryName", beneficiary.getBeneficiaryAccountName());
        model.addAttribute("benName", beneficiary.getBeneficiaryAccountName());
        request.getSession().setAttribute("Nbeneficiary", neftBeneficiaryService.convertEntityToDTO(beneficiary));
        return page + "pageiN2";
    }

    @RequestMapping(value = "/bulktransfer/summary", method = {RequestMethod.POST , RequestMethod.GET})
    public String neftTransferSummary(@ModelAttribute("transferRequest") @Valid NeftTransferRequestDTO transferRequestDTO1, BindingResult result, Model model, HttpServletRequest request) throws Exception {

        String newbenName = (String) request.getSession().getAttribute("beneficiaryName");
        logger.info("I GOT HERE WITH ALL DETAILS {}", transferRequestDTO1);
        TransferRequestDTO transferRequestDTO = convertToTransferRequest(transferRequestDTO1);
        model.addAttribute("transferRequest", transferRequestDTO);
        String charge = "NAN";
        String benName = (String) request.getSession().getAttribute("benName");
        model.addAttribute("benName", benName);
        if (result.hasErrors()) {
            return page + "pageiN2";
        }
        if (request.getSession().getAttribute("NIP") != null) {
            String type = (String) request.getSession().getAttribute("NIP");
            if("NEFT".equalsIgnoreCase(type)){
                transferRequestDTO.setTransferType(TransferType.NEFT_BULK);
                charge = transferUtils.getFee("NEFT",String.valueOf(transferRequestDTO.getAmount()));
                transferRequestDTO.setCharge(charge);
            }
        }

        request.getSession().setAttribute("transferRequest", transferRequestDTO);
        logger.info("Neft Transfer Request summary {} ", transferRequestDTO);
        model.addAttribute("charge", charge);
        return page + "neftsummary";
    }

    private TransferRequestDTO convertToTransferRequest(NeftTransferRequestDTO nft){
        TransferRequestDTO trt = new TransferRequestDTO();
        trt.setBeneficiaryBVN(nft.getBeneficiaryBVN());
        trt.setBeneficiaryAccountNumber(nft.getBeneficiaryAccountNumber());
        trt.setBeneficiaryAccountName(nft.getBeneficiaryAccountName());
        trt.setBeneficiaryBankName(nft.getBeneficiaryBankName());
        trt.setBeneficiarySortCode(nft.getBeneficiarySortCode());
        trt.setInstrumentType(nft.getInstrumentType());
        trt.setCollectionType(nft.getCollectionType());
//        trt.setCurrency(nft.getCurrencyCode());
        trt.setCurrencyCode(nft.getCurrencyCode());
        trt.setCustomerAccountNumber(nft.getCustomerAccountNumber());
        trt.setTransferType(TransferType.NEFT_BULK);
        trt.setAmount(new BigDecimal(nft.getAmount()));
        trt.setNarration(nft.getNarration());
        trt.setCharge(nft.getCharge());
        trt.setChannel(nft.getChannel());
        return trt;

    }

    @PostMapping("/neft/edit")
    public String editNeftTransfer(@ModelAttribute("transferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("benName", transferRequestDTO.getBeneficiaryAccountName());
        return page + "pageiN2";
    }

}
