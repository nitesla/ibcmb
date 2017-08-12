package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Showboy on 12/08/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class CompletedTransferController {

    @Autowired
    TransferService transferService;

    @GetMapping("/history")
    public String completedTransfers(){
        return "cust/transfer/completed";
    }

    @GetMapping("/history/all")
    public @ResponseBody
    DataTablesOutput<TransferRequestDTO> getTransfersCompleted(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<TransferRequestDTO> transferRequests = transferService.getCompletedTransfers(pageable);
//        if (StringUtils.isNoneBlank(search)) {
//            transferRequests = transferService.findCompletedTransfers(search, pageable);
//        }else{
//            transferRequests = transferService.getCompletedTransfers(pageable);
//        }
        DataTablesOutput<TransferRequestDTO> out = new DataTablesOutput<TransferRequestDTO>();
        out.setDraw(input.getDraw());
        out.setData(transferRequests.getContent());
        out.setRecordsFiltered(transferRequests.getTotalElements());
        out.setRecordsTotal(transferRequests.getTotalElements());

        return out;
    }
}
