package longbridge.repositories;

import longbridge.dtos.BulkStatusDTO;
import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CreditRequestRepo extends CommonRepo<CreditRequest,Long>
{

    List<CreditRequest> findByBulkTransfer_Id(Long id);
    boolean existsByReferenceNumber(String refNumber);
    CreditRequest findByAccountNumberAndBulkTransferIdAndReferenceNumber(String acctNo,Long id,String refno);

    @Query("select new longbridge.dtos.BulkStatusDTO(c.status,count(c.status)) from CreditRequest c  where c.bulkTransfer =:bulkTransfer group by c.status ")
    List<BulkStatusDTO> getCreditRequestSummary(@Param("bulkTransfer") BulkTransfer bulkTransfer);

    List<CreditRequest> findAllByStatusAndBulkTransferId(String status,Long bulkTransferId);




}
