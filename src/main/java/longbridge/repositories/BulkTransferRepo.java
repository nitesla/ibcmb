package longbridge.repositories;

import longbridge.dtos.BulkTransferDTO;
import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.models.CreditRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
public interface BulkTransferRepo extends CommonRepo<BulkTransfer, Long>{


    Page<BulkTransfer> findByCorporate(Corporate corporate, Pageable details);

}
