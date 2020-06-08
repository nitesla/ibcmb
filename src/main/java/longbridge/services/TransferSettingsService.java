package longbridge.services;

import longbridge.dtos.TransferFeeAdjustmentDTO;
import longbridge.dtos.TransferSetLimitDTO;
import longbridge.exception.InternetBankingException;

public interface TransferSettingsService {


    public String adjustTransferFee(TransferFeeAdjustmentDTO tfaDTO) throws InternetBankingException;
    public String updateTransferLimit(TransferSetLimitDTO tslDTO)throws InternetBankingException;


}
