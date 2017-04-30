package longbridge.services.implementations;

import longbridge.dtos.TransactionFeeDTO;
import longbridge.models.TransactionFee;
import longbridge.repositories.TransactionFeeRepo;
import longbridge.services.CodeService;
import longbridge.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/29/2017.
 */

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TransactionFeeRepo transactionFeeRepo;

    @Autowired
    CodeService codeService;

    @Override
    public void addTransactionFee(TransactionFeeDTO transactionFeeDTO) throws  Exception{
        TransactionFee transactionFee = convertDTOToEntity(transactionFeeDTO);
        transactionFee.setDateCreated(new Date());
        transactionFeeRepo.save(transactionFee);
    }

    @Override
    public TransactionFeeDTO getTransactionFee(Long id) {
        TransactionFee transactionFee = transactionFeeRepo.findOne(id);
        return convertEntityToDTO(transactionFee);
    }

    @Override
    public void updateTransactionFee(TransactionFeeDTO transactionFeeDTO) throws Exception{
        TransactionFee transactionFee = transactionFeeRepo.findOne(transactionFeeDTO.getId());
        transactionFee.setVersion(transactionFeeDTO.getVersion());
        transactionFee.setFixed(transactionFeeDTO.getFixed());
        transactionFee.setPercentage(transactionFeeDTO.getPercentage());
        transactionFee.setCurrency(transactionFeeDTO.getCurrency());
        transactionFee.setEnabled(transactionFeeDTO.isEnabled());
        transactionFeeRepo.save(transactionFee);
    }

    @Override
    public void deleteTransactionFee(Long id) {
        transactionFeeRepo.delete(id);
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

