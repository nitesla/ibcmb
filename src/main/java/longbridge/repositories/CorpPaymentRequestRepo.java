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

import java.util.List;

@Repository
public interface CorpPaymentRequestRepo extends CommonRepo<CorpPaymentRequest, Long>{

    Page<CorpPaymentRequest> findByCorporateOrderByTranDateDesc(Corporate corporate, Pageable details);

    boolean existsByCorporate_IdAndCustomerAccountNumberAndStatus(Long corpId,String accNumber, String status);

    List<CorpPaymentRequest> findByStatusNotInIgnoreCase(List<String> list);

    List<CorpPaymentRequest> findByStatus(String status);

    CorpPaymentRequest findFirstByRefCode(String refCode);

    int countByCorporateAndStatus(Corporate corporate, String status);

    CorpPaymentRequest findById(long id);
}
