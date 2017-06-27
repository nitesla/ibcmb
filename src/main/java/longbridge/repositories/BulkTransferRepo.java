package longbridge.repositories;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
public interface BulkTransferRepo extends CommonRepo<BulkTransfer, Long>{


    Page<BulkTransfer> findByCorporate(Corporate corporate, Pageable details);

}