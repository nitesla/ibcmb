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

    Page<CorpTransRequest> findByCorporateOrderByStatusAscTranDateDesc(Corporate corporate, Pageable pageable);

    Page<CorpTransRequest> findByCorporateOrderByStatusDesc(Corporate corporate,  Pageable pageable);

    int countByCorporateAndStatus(Corporate corporate, String status);

    boolean existsByCorporate_IdAndCustomerAccountNumberAndStatus(Long corpId,String accNumber, String status);

//    Page<CorpTransRequest> findByCorporateAndStatusInAndTranDateNotNullOrderByTranDateDesc(Corporate corporate, List<String> status, Pageable pageable);
    Page<CorpTransRequest> findByCorporateAndStatusNotAndTranDateNotNullOrderByTranDateDesc(Corporate corporate,List<String> status, Pageable pageable);

    Page<CorpTransRequest> findByCorporateAndStatusDescription(Corporate corporate, String sd, Pageable pageable);

    CorpTransRequest findById(long id);

    @Query("select r from CorpTransRequest r where r.corporate=:corporate and (upper(r.remarks) like %:search% or upper(r.amount) like %:search% or upper(r.beneficiaryAccountName) like %:search% or upper(r.tranDate) like %:search%)")
    Page<CorpTransRequest> findUsingPattern(@Param("corporate")Corporate corporate,@Param("search") String search,Pageable pageable);


}
