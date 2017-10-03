package longbridge.repositories;

import longbridge.models.CorpTransRequest;
import longbridge.models.Corporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */
@Repository
public interface CorpTransferRequestRepo extends CommonRepo<CorpTransRequest,Long> {

    Page<CorpTransRequest> findByCorporate(Corporate corporate, Pageable pageable);

    Page<CorpTransRequest> findByCorporateAndStatusOrderByTranDateDesc(Corporate corporate, String status, Pageable pageable);

    Page<CorpTransRequest> findByCorporateOrderByTranDateDesc(Corporate corporate, Pageable pageable);

    Page<CorpTransRequest> findByCorporateOrderByStatusAsc(Corporate corporate, Pageable pageable);


    Page<CorpTransRequest> findByCorporateOrderByStatusDesc(Corporate corporate,  Pageable pageable);


    int countByCorporateAndStatus(Corporate corporate, String status);

    Page<CorpTransRequest> findByCorporateAndStatusInAndTranDateNotNullOrderByTranDateDesc(Corporate corporate, List<String> status, Pageable pageable);


    Page<CorpTransRequest> findByCorporateAndStatusDescription(Corporate corporate, String sd, Pageable pageable);

    CorpTransRequest findById(long id);




}
