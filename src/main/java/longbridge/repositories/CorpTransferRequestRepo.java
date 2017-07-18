package longbridge.repositories;

import longbridge.models.CorpTransRequest;
import longbridge.models.Corporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */
@Repository
public interface CorpTransferRequestRepo extends CommonRepo<CorpTransRequest,Long> {

    Page<CorpTransRequest> findByCorporate(Corporate corporate, Pageable pageable);

    CorpTransRequest findById(long id);

}
