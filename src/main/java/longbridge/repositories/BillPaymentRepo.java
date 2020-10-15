package longbridge.repositories;

import longbridge.models.BillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 09/07/2018.
 */
@Repository
public interface BillPaymentRepo extends CommonRepo<BillPayment,Long> {

    Page<BillPayment> findByRequestReferenceAndStatusNotNullOrderByCreatedOnDesc(String rn, Pageable pageable);

    Page<BillPayment> findByCustomerIdAndStatusNotNullOrderByCreatedOnDesc(String ci, Pageable pageable);


    @Query("select r from BillPayment r where r.customerId=:customerId and (upper(r.billerName) like %:search% or concat(r.amount,'')  like %:search% or upper(r.status)  like %:search% or upper(r.paymentItemName)  like %:search% or upper(r.categoryName)  like %:search% or upper(r.createdOn) like %:search%)order by r.createdOn desc")
    Page<BillPayment> findUsingPattern(@Param("customerId") String customerId, @Param("search") String search, Pageable pageable);
}
