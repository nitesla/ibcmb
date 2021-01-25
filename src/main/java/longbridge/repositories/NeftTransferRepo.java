package longbridge.repositories;

import longbridge.models.Corporate;
import longbridge.models.NeftTransfer;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeftTransferRepo extends CommonRepo<NeftTransfer, Long>{

    @Query(value = "select n from NeftTransfer n where n.status = 'Submitted' ")
    List<NeftTransfer> checkStatus();

    @Query(value = "select n from NeftTransfer n where n.SettlementTime = 'not settled' ")
    List<NeftTransfer> getAllUnsettledList();

//    @Query(value = "select n from NeftTransfer n where n.SettlementTime = 'not settled' and n.retailUser =:user ")
    @Query(value = "select n from NeftTransfer n where n.retailUser =:user ")
    Page<NeftTransfer> findByRetailUser(@Param("user") RetailUser user, Pageable pageable);

    @Query(value = "select n from NeftTransfer n where n.corporate =:corporate ")
    Page<NeftTransfer> findByCorporate(@Param("corporate") Corporate corporate, Pageable pageable);
}
