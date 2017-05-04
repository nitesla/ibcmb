package longbridge.services;

import longbridge.dtos.TransactionFeeDTO;
import longbridge.models.TransactionFee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *The {@code TransactionService} interface provides the methods  for managing general transactions
 * such as setting up and retrieving transaction fees.
 * Created by Fortune on 4/29/2017.
 */
public interface TransactionService {

    /**
     * Adds a transaction fee
     * @param transactionFee
     */
    void addTransactionFee(TransactionFeeDTO transactionFee) throws  Exception;

    /**
     * Returns a transaction fee
     * @param id
     * @return
     */
    TransactionFeeDTO getTransactionFee(Long id);

    /**
     * Updates the transaction fees
     * @param transactionFeeDTO
     */
    void updateTransactionFee(TransactionFeeDTO transactionFeeDTO) throws Exception;

    /**
     * Deletes the transaction fees
     * @param id
     */
    void deleteTransactionFee(Long id);


    /**
     * Returns a  list of transaction fees
     * @return a list
     */
    Iterable<TransactionFeeDTO> getTransactionFees();


    /**
     * Returns a page list of transaction fees
     * @return a list
     */
    Page<TransactionFeeDTO> getTransactionFees(Pageable pageable);





}
