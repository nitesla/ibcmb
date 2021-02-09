package longbridge.services;

import longbridge.dtos.TransferFeeAdjustmentDTO;
import longbridge.dtos.TransferSetLimitDTO;
import longbridge.exception.InternetBankingException;

public interface TransferSettingsService {


    String adjustTransferFee(TransferFeeAdjustmentDTO tfaDTO) ;
    String updateTransferLimit(TransferSetLimitDTO tslDTO);


}
