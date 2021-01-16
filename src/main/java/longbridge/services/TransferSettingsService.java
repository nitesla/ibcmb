package longbridge.services;

import longbridge.dtos.TransferFeeAdjustmentDTO;
import longbridge.dtos.TransferSetLimitDTO;
import longbridge.exception.InternetBankingException;

public interface TransferSettingsService {


    String adjustTransferFee(TransferFeeAdjustmentDTO tfaDTO) throws InternetBankingException;
    String updateTransferLimit(TransferSetLimitDTO tslDTO)throws InternetBankingException;


}
