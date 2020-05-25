package longbridge.services;

import longbridge.dtos.BulkStatusDTO;
import longbridge.dtos.BulkTransferDTO;
import longbridge.dtos.CreditRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */

public interface BulkRetailTransferService {
    String makeBulkTransferRequest(BulkTransfer bulkTransfer);

    boolean creditRequestRefNumberExists(String refNumber);

    List<BulkStatusDTO> getStatus(BulkTransfer bulkTransfer);

    Boolean refCodeExists(String refCode);

    Page<BulkTransferDTO> getBulkTransferRequests(Pageable details);
    String cancelBulkTransferRequest(Long id);
    BulkTransfer getBulkTransferRequest(Long id);
    Page<CreditRequestDTO> getCreditRequests(BulkTransfer bulkTransfer, Pageable pageable);
    List<CreditRequestDTO> getCreditRequests(Long bulkTransferId);
    Page<CreditRequest> getAllCreditRequests(BulkTransfer bulkTransfer, Pageable pageable);
    CreditRequestDTO getCreditRequest(Long id);


    int getPendingBulkTransferRequests(Corporate corporate);
    boolean transactionAboveLimit(BigDecimal totalCreditAmount, String debitAcccount) throws InternetBankingException;

    List<BulkTransfer>getByStatus();
}