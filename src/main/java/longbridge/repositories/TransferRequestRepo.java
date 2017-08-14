package longbridge.repositories;

import longbridge.models.TransRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by chigozirim on 3/31/17.
 */
public interface TransferRequestRepo extends CommonRepo<TransRequest, Long> {
    TransRequest findById(long id);
    List<TransRequest> findByUserReferenceNumber(String s);
    List<TransRequest> findTop10ByCustomerAccountNumberOrderByTranDateDesc(String acc);
    List<TransRequest> findByUserReferenceNumberAndStatus(String rn, String s);

    Page<TransRequest> findByUserReferenceNumberAndStatusIn(String rn, List<String> status, Pageable pageable);
}
