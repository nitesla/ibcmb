package longbridge.repositories;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.models.CorpPaymentRequest;
import longbridge.models.CorpTransRequest;
import longbridge.models.Corporate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface CorpPaymentRequestRepo extends CommonRepo<CorpPaymentRequest, Long>{

    Page<CorpPaymentRequest> findByCorporateOrderByTranDateDesc(Corporate corporate, Pageable details);
    Page<CorpPaymentRequest> findAllByOrderByTranDateDesc(Pageable details);

    boolean existsByCorporate_IdAndCustomerAccountNumberAndStatus(Long corpId,String accNumber, String status);

    List<CorpPaymentRequest> findByStatusNotInIgnoreCase(List<String> list);

    List<CorpPaymentRequest> findByStatus(String status);

    CorpPaymentRequest findFirstByRefCode(String refCode);

    int countByCorporateAndStatus(Corporate corporate, String status);

    CorpPaymentRequest findById(long id);
    Page<CorpPaymentRequest> findCorpPaymentRequestByCorporateAndTranDateBetween(Corporate corporate, Pageable details, Date startDate, Date endDate);

    @Query("select c from CustomRevisionEntity c where c.id=:rev")
    CustomRevisionEntity findUniqueCustomEnity(@Param("rev") Integer revisionNumber);

    @Query("SELECT t from CorpPaymentRequest t where t.corporate.id =:id and concat( t.amount,'')  like %:amount%")
    Page<CorpPaymentRequest> filterByAmount(@Param("id")long id,Pageable details, @Param("amount")String amount);


}
