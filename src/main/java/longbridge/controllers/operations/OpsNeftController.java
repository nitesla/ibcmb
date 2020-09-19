package longbridge.controllers.operations;

import longbridge.models.NeftTransfer;
import longbridge.repositories.NeftTransferRepo;
import longbridge.services.IntegrationService;
import longbridge.services.TransactionService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ops/neft")
public class OpsNeftController {

    Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NeftTransferRepo neftTransferRepo;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String neftRequestsPage(){
        return "ops/neftrequests";
    }

    @PostMapping("/settle")
    @ResponseBody
    public String settleTransactions(){
        integrationService.submitNeftTransfer();
        return "successful";
    }


    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<NeftTransfer> getAllCategoriesAndDescription(DataTablesInput input){
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
