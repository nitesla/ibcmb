package longbridge.services.implementations;

import longbridge.dtos.TransferFeeAdjustmentDTO;
import longbridge.dtos.TransferSetLimitDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.TransferFeeAdjustment;
import longbridge.models.TransferSetLimit;
import longbridge.repositories.TransferAdjustFeeRepository;
import longbridge.repositories.TransferSetLimitRepository;
import longbridge.services.IntegrationService;
import longbridge.services.TransferSettingsService;
import longbridge.utils.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class TransferSettingsImpl implements TransferSettingsService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TransferAdjustFeeRepository transferAdjustFeeRepository;

    @Autowired
    private TransferSetLimitRepository transferSetLimitRepository;

    @Autowired
    private IntegrationService integrationService;

    private Locale locale = LocaleContextHolder.getLocale();

    @Override
    @Transactional
    @Verifiable(operation = "ADJUST_TRANSFER_FEE", description = "Adjust transfer fee for a platform")
    public String adjustTransferFee(TransferFeeAdjustmentDTO tfaDTO) throws InternetBankingException {
        try {
            TransferFeeAdjustment tfa = new TransferFeeAdjustment();
            tfa.setFeeDescription(tfaDTO.getFeeDescription());
            tfa.setFixedAmount(tfaDTO.getFixedAmount());
            tfa.setTransactionChannel(tfaDTO.getTransactionChannel());
            tfa.setFixedAmountValue(tfaDTO.getFixedAmountValue());
            tfa.setRate(tfaDTO.getRate());
            tfa.setRateValue(tfaDTO.getRateValue());
           tfa.setFeeRange(tfaDTO.getFeeRange());
            tfa.setDelFlag("N");
            transferAdjustFeeRepository.save(tfa);
            String apiService = integrationService.updateCharge(tfa);
            if (apiService == "fail"){
                throw new RuntimeException("Error processing request");
            }
            return messageSource.getMessage("transfer.adjustment.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.adjustment.failure", null, locale), e);
        }
    }


    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_TRANSFER_LIMIT", description = "Update transfer limit")
    public String updateTransferLimit(TransferSetLimitDTO tslDTO)throws InternetBankingException{
        try {
            TransferSetLimit tsl = new TransferSetLimit();
            tsl.setChannel(tslDTO.getChannel());
            tsl.setCustomerType(tslDTO.getCustomerType());
            tsl.setDescription(tslDTO.getDescription());
            tsl.setLowerLimit(tslDTO.getLowerLimit());
            tsl.setUpperLimit(tslDTO.getUpperLimit());
            tsl.setDelFlag("N");
            transferSetLimitRepository.save(tsl);
            String apiService = integrationService.updateTransferLimit(tsl);
            if (apiService == "fail"){
                throw new RuntimeException("Error processing request");
            }
            return messageSource.getMessage("transfer.limit.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("transfer.limit.failure", null, locale), e);
        }

    }

}
