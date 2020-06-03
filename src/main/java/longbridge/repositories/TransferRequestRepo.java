package longbridge.repositories;

import longbridge.models.TransRequest;
import longbridge.utils.TransferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
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
    List<TransRequest> findByStatus(String status);
    @Query("select r from TransRequest r where r.userReferenceNumber=:userReference and (upper(r.remarks) like %:search% or r.amount like %:search% or upper(r.beneficiaryAccountName) like %:search% or upper(r.tranDate) like %:search%)order by r.tranDate desc")
    Page<TransRequest> findUsingPattern(@Param("userReference") String userReference,@Param("search") String search, Pageable pageable);

    @Query("select c from TransRequest c where c.transferType=:transferType and " +
            "c.customerAccountNumber=:accountNumber and c.tranDate BETWEEN :startDate and :endDate ORDER BY c.tranDate desc")
    Page<TransRequest> findByReviewParams(@Param("transferType") TransferType transferType,
                                          @Param("accountNumber")String accountNumber,@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,Pageable pageable);
    @Query("select c from TransRequest c where " +
            "c.customerAccountNumber=:accountNumber and c.tranDate BETWEEN :startDate and :endDate ORDER BY c.tranDate desc")
    Page<TransRequest> findByReviewParamsForAllTransTypes(@Param("accountNumber")String accountNumber, @Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate, Pageable pageable);


}
