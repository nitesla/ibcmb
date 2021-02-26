package longbridge.services;

import longbridge.dtos.TransferFeeAdjustmentDTO;
import longbridge.dtos.TransferSetLimitDTO;

public interface TransferSettingsService {


    String adjustTransferFee(TransferFeeAdjustmentDTO tfaDTO) ;
    String updateTransferLimit(TransferSetLimitDTO tslDTO);


}
