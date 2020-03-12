package longbridge.repositories;

import longbridge.models.TransRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by chigozirim on 3/31/17.
 */
public interface TransferRequestRepo extends CommonRepo<TransRequest, Long> {
    TransRequest findById(long id);
    List<TransRequest> findByUserReferenceNumber(String s);
    List<TransRequest> findTop10ByCustomerAccountNumberOrderByTranDateDesc(String acc);
    List<TransRequest> findByUserReferenceNumberAndStatus(String rn, String s);
    Page<TransRequest> findByUserReferenceNumberAndStatusInAndTranDateNotNullOrderByTranDateDesc(String rn, List<String> status, Pageable pageable);
    Page<TransRequest> findByUserReferenceNumberAndTranDateNotNullOrderByTranDateDesc(String rn, Pageable pageable);//by GB
    TransRequest findByReferenceNumberAndStatus(String referenceNumber,String status);
    TransRequest findByReferenceNumber(String referenceNumber);
    List<TransRequest> findByStatus(String status);
    @Query("select r from TransRequest r where r.userReferenceNumber=:userReference and (upper(r.remarks) like %:search% or r.amount like %:search% or upper(r.beneficiaryAccountName) like %:search% or upper(r.tranDate) like %:search%)")
    Page<TransRequest> findUsingPattern(@Param("userReference") String userReference,@Param("search") String search, Pageable pageable);

}
