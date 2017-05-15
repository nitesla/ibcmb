package longbridge.services;

import longbridge.dtos.TransactionFeeDTO;
import longbridge.exception.InternetBankingException;
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
    String addTransactionFee(TransactionFeeDTO transactionFee) throws InternetBankingException;

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
    String updateTransactionFee(TransactionFeeDTO transactionFeeDTO) throws InternetBankingException;

    /**
     * Deletes the transaction fees
     * @param id
     */
    String deleteTransactionFee(Long id) throws InternetBankingException;


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
