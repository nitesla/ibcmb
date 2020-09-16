package longbridge.services.implementations;

import longbridge.dtos.TransactionFeeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.NeftTransfer;
import longbridge.models.TransactionFee;
import longbridge.repositories.NeftTransferRepo;
import longbridge.repositories.TransactionFeeRepo;
import longbridge.services.CodeService;
import longbridge.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/29/2017.
 */

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransactionFeeRepo transactionFeeRepo;

    @Autowired
    private CodeService codeService;

    @Autowired
    private MessageSource messageSource;


    @Autowired
     private NeftTransferRepo neftTransferRepo;



    Locale locale = LocaleContextHolder.getLocale();

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String addTransactionFee(TransactionFeeDTO transactionFeeDTO) throws InternetBankingException {
        try {
            TransactionFee transactionFee = convertDTOToEntity(transactionFeeDTO);
            transactionFee.setDateCreated(new Date());
            transactionFeeRepo.save(transactionFee);
            logger.info("Transaction fee added");
            return messageSource.getMessage("fee.add.success", null, locale);
        }
        catch (Exception e){
           throw new InternetBankingException(messageSource.getMessage("fee.add.failure",null,locale));
        }
    }

    @Override
    public TransactionFeeDTO getTransactionFee(Long id) {
        TransactionFee transactionFee = transactionFeeRepo.findById(id).get();
        return convertEntityToDTO(transactionFee);
    }

    @Override
    public String updateTransactionFee(TransactionFeeDTO transactionFeeDTO) throws InternetBankingException {
        try {
            TransactionFee transactionFee = transactionFeeRepo.findById(transactionFeeDTO.getId()).get();
            transactionFee.setVersion(transactionFeeDTO.getVersion());
            transactionFee.setFixed(transactionFeeDTO.getFixed());
            transactionFee.setPercentage(transactionFeeDTO.getPercentage());
            transactionFee.setCurrency(transactionFeeDTO.getCurrency());
            transactionFee.setEnabled(transactionFeeDTO.isEnabled());
            transactionFeeRepo.save(transactionFee);
            logger.info("Transaction fee updated");
            return messageSource.getMessage("fee.add.success", null, locale);

        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("fee.add.failure",null,locale),e);
        }
    }

    @Override
    public String deleteTransactionFee(Long id) throws InternetBankingException {
        try {
            transactionFeeRepo.deleteById(id);
            logger.info("Transaction fee deleted");
            return messageSource.getMessage("fee.delete.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("fee.delete.failure",null,locale));
        }
    }

    @Override
    public Iterable<TransactionFeeDTO> getTransactionFees() {
        return convertEntitiesToDTOs(transactionFeeRepo.findAll());
    }

    @Override
    public Page<TransactionFeeDTO> getTransactionFees(Pageable pageable){
        Iterable<TransactionFee> transactionFees = transactionFeeRepo.findAll(pageable);
        List<TransactionFeeDTO> dtos = convertEntitiesToDTOs(transactionFees);
        return new PageImpl<TransactionFeeDTO>(dtos,pageable,dtos.size());

    }

    @Override
    public Page<NeftTransfer> getNeftUnsettledTransactions(Pageable pageable) {
        return neftTransferRepo.findBySettlementTime(pageable);
    }


    private TransactionFee convertDTOToEntity(TransactionFeeDTO transactionFeeDTO) {
        return modelMapper.map(transactionFeeDTO, TransactionFee.class);
    }

    private TransactionFeeDTO convertEntityToDTO(TransactionFee transactionFee) {
        return modelMapper.map(transactionFee, TransactionFeeDTO.class);

    }

    private List<TransactionFeeDTO> convertEntitiesToDTOs(Iterable<TransactionFee> transactionFees) {
        List<TransactionFeeDTO> transactionFeeList = new ArrayList<>();
        for (TransactionFee transactionFee : transactionFees) {
            TransactionFeeDTO transactionFeeDTO = convertEntityToDTO(transactionFee);
            transactionFeeDTO.setTransactionType(codeService.getByTypeAndCode("TRANSACTION_TYPE",transactionFee.getTransactionType()).getDescription());
            transactionFeeList.add(transactionFeeDTO);
        }
        return transactionFeeList;
    }
}

