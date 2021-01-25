package longbridge.repositories;

import longbridge.models.CorpNeftTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorpNeftTransferRepo extends CommonRepo<CorpNeftTransfer, Long>{

    @Query(value = "select n from CorpNeftTransfer n where n.settlementTime = 'not settled' ")
    List<CorpNeftTransfer> getAllUnsettledList();

    @Query(value = "select n from CorpNeftTransfer n where n.settlementTime = 'not settled' ")
    Page<CorpNeftTransfer> findBySettlementTime(Pageable pageable);
}
