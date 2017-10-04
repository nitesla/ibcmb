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


    Page<BulkTransfer> findByCorporateOrderByTranDateDesc(Corporate corporate, Pageable details);

    Page<BulkTransfer> findByCorporateOrderByStatusAsc(Corporate corporate, Pageable details);


    List<BulkTransfer> findByStatusNotInIgnoreCase(List<String> list);

   BulkTransfer findFirstByRefCode(String refCode);

   int countByCorporateAndStatus(Corporate corporate, String status);

}
