package longbridge.services;


import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;

import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
public interface BulkTransferService {
    String makeBulkTransferRequest(BulkTransfer bulkTransfer);
    List<BulkTransfer> getAllBulkTransferRequests();
    String cancelBulkTransferRequest(Long id);

}
