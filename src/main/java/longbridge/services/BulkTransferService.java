package longbridge.services;
import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.models.CreditRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;


/**
 * Created by Longbridge on 14/06/2017.
 */

public interface BulkTransferService {
    String makeBulkTransferRequest(BulkTransfer bulkTransfer);
    Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details);
    String cancelBulkTransferRequest(Long id);
    BulkTransfer getBulkTransferRequest(Long id);


}
