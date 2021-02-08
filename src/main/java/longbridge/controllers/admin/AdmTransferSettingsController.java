package longbridge.controllers.admin;

import longbridge.dtos.TransferFeeAdjustmentDTO;
import longbridge.dtos.TransferSetLimitDTO;
import longbridge.models.Code;
import longbridge.repositories.CodeRepo;
import longbridge.services.TransferSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/transfersettings")
public class AdmTransferSettingsController {

    @Autowired
    private CodeRepo codeRepo;

    @Autowired
    private TransferSettingsService transferSettingsService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/adjustTransfer")
    public String adjustTransferFee(Model model)
    {
        return "adjustTransferFee";
    }

    @GetMapping("/setLimit")
    public String setTransferLimit(Model model)
    {
        String type = "TRANSFER_CHANNEL";
        List<Code> getTransferChannel = codeRepo.findAllByType(type);
        model.addAttribute("transferchannels",getTransferChannel);
        return "setTransferLimit";
    }




    @ResponseBody
    @PostMapping("/submitTransferLimitForAllTransactionChannels")
    public ResponseEntity<String> submitTransferLimit(TransferSetLimitDTO transferSetLimit){
        logger.info("Input details for transfer limit = " + transferSetLimit);
        String response = "Update limit request successfully sent";

        try {
            String submitTransferLimit = transferSettingsService.updateTransferLimit(transferSetLimit);
        }catch (Exception e){
            response = "Error processing request, Please try again!";
           return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }



    @ResponseBody
    @PostMapping("/submitTransferAdjustments")
    public ResponseEntity<String> submitTransferAdjustments(TransferFeeAdjustmentDTO transferFeeAdjustment){
        logger.info("Input details for transfer adjustments " + transferFeeAdjustment);
        String response = "";
        try {
            response = transferSettingsService.adjustTransferFee(transferFeeAdjustment);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e){
            response = "Error processing request, Please try again!";
           return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

    }

}
