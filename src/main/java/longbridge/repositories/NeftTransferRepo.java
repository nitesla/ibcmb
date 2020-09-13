package longbridge.repositories;

import longbridge.models.NeftTransfer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NeftTransferRepo extends CommonRepo<NeftTransfer, Long>{

    @Query(value = "select * from NEFT_TRANSFER n where n.settlementTime is null", nativeQuery = true)
    List<NeftTransfer> getAllUnsettledList();

}
