package longbridge.repositories;

import longbridge.dtos.BulkStatusDTO;
import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditRequestRepo extends CommonRepo<CreditRequest,Long>
{

    List<CreditRequest> findByBulkTransfer_Id(Long id);

    @Query("select new longbridge.dtos.BulkStatusDTO(c.status,count(c.status)) from CreditRequest c  where c.bulkTransfer =:bulkTransfer group by c.status ")
    List<BulkStatusDTO> getCreditRequestSummary(@Param("bulkTransfer") BulkTransfer bulkTransfer);




}
