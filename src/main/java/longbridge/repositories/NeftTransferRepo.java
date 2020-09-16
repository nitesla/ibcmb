package longbridge.repositories;

import longbridge.models.NeftTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeftTransferRepo extends CommonRepo<NeftTransfer, Long>{

    @Query(value = "select * from NEFT_TRANSFER n where n.settlementTime = 'not settled' ", nativeQuery = true)
    List<NeftTransfer> getAllUnsettledList();

    @Query(value = "select * from NEFT_TRANSFER n where n.settlementTime = 'not settled' ", nativeQuery = true)
    Page<NeftTransfer> findBySettlementTime(Pageable pageable);
}
