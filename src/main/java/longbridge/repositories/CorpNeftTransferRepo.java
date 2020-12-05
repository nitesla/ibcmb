package longbridge.repositories;

import longbridge.models.CorpNeftTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorpNeftTransferRepo extends CommonRepo<CorpNeftTransfer, Long>{

    @Query(value = "select * from NEFT_TRANSFER n where n.settlementTime = 'not settled' ", nativeQuery = true)
    List<CorpNeftTransfer> getAllUnsettledList();

    @Query(value = "select * from NEFT_TRANSFER n where n.settlementTime = 'not settled' ", nativeQuery = true)
    Page<CorpNeftTransfer> findBySettlementTime(Pageable pageable);
}
