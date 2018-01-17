package longbridge.services;
import longbridge.dtos.BulkStatusDTO;
import longbridge.dtos.BulkTransferDTO;
import longbridge.dtos.CreditRequestDTO;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */

public interface BulkTransferService {
    String makeBulkTransferRequest(BulkTransfer bulkTransfer);
    String saveBulkTransferRequestForAuthorization(BulkTransfer bulkTransfer);
    CorpTransferAuth getAuthorizations(BulkTransfer transRequest);
    String addAuthorization(CorpTransReqEntry transReqEntry);
    List<BulkStatusDTO> getStatus(BulkTransfer bulkTransfer);

    Boolean refCodeExists(String refCode);

    Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details);
    Page<BulkTransferDTO> getBulkTransferRequests(Corporate corporate, Pageable details);
    String cancelBulkTransferRequest(Long id);
    BulkTransfer getBulkTransferRequest(Long id);
    Page<CreditRequestDTO> getCreditRequests(BulkTransfer bulkTransfer, Pageable pageable);
    Page<CreditRequest> getAllCreditRequests(BulkTransfer bulkTransfer, Pageable pageable);

    boolean userCanAuthorize(TransRequest transRequest);

    int getPendingBulkTransferRequests(Corporate corporate);
}
