package longbridge.controllers.retail;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.NeftBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.NeftBeneficiary;
import longbridge.models.NeftTransfer;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferType;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;


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
            requestDTO.setTransferType(TransferType.NEFT);

            model.addAttribute("transferRequest", requestDTO);
            model.addAttribute("neftBeneficiaries", neftBeneficiaries);
            return page + "pageiAc";

    }

    @ModelAttribute
    public void getNeftbanks(Model model) {
        List<CodeDTO> sortedNames = codeService.getCodesByType("NEFT_BANKS");
        sortedNames.sort(Comparator.comparing(CodeDTO::getDescription));
        model.addAttribute("neftBanks"
                , sortedNames);

    }

    @ModelAttribute
    public void getNeftCollectionType(Model model) {
        List<CodeDTO> sortedNames = codeService.getCodesByType("NEFT_COLLECTION_TYPE");
        model.addAttribute("neftCollectionTypes"
                , sortedNames);

    }

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

}
