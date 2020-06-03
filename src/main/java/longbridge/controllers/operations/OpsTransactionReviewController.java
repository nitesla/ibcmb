package longbridge.controllers.operations;

import longbridge.dtos.TransactionReviewDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.services.TransferService;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static longbridge.utils.TransferType.getTransferTypes;
@Controller
@RequestMapping("/ops/transactions")
public class OpsTransactionReviewController {
    @Autowired
    private TransferService transferService;


    @GetMapping("")
    @ResponseBody
    public List<TransferType> getTransactions(Model model){
        List<TransferType> transferTypes=getTransferTypes();
        model.addAttribute("transferTypes",transferTypes);
        return transferTypes;
    }
    @GetMapping("/review")
    @ResponseBody
    public DataTablesOutput<TransferRequestDTO> getTransactionsReview(
            @ModelAttribute("transactionReviewDTO") TransactionReviewDTO transactionReviewDTO, DataTablesInput input)throws ParseException {
        TransferType transferType;
        String accountNumber=transactionReviewDTO.getCustomerAccountNumber();
        if(transactionReviewDTO.getTransactionType()==null){
            transferType=null;
        }else transferType=transactionReviewDTO.getTransactionType();
        String start=transactionReviewDTO.getStartDate();
        String end=transactionReviewDTO.getEndDate();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date startDate=format.parse(start);
        Date end2=format.parse(end);
        Date endDate = new Date(end2.getTime() + TimeUnit.DAYS.toMillis(1));
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<TransferRequestDTO> transferRequestDTOS = transferService.getTransferReviews(transferType,accountNumber,startDate,endDate,pageable);

        DataTablesOutput<TransferRequestDTO> out = new DataTablesOutput<>();
        if(out.equals(null)){
            return null;
        }else{
            out.setDraw(input.getDraw());
            out.setData(transferRequestDTOS.getContent());
            out.setRecordsFiltered(transferRequestDTOS.getTotalElements());
            out.setRecordsTotal(transferRequestDTOS.getTotalElements());
            return out;
        }

    }


}
