package longbridge.repositories;

import longbridge.models.CorpTransRequest;
import longbridge.models.Corporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 5/18/2017.
 */
@Repository
public interface CorpTransferRequestRepo extends CommonRepo<CorpTransRequest,Long> {

    Page<CorpTransRequest> findByCorporate(Corporate corporate, Pageable pageable);

    Page<CorpTransRequest> findByCorporateAndStatus(Corporate corporate, String status, Pageable pageable);

    Page<CorpTransRequest> findByCorporateAndStatusDescription(Corporate corporate, String sd, Pageable pageable);

    CorpTransRequest findById(long id);

}
