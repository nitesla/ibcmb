package longbridge.repositories;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.models.RetailUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Repository
public interface BulkTransferRepo extends CommonRepo<BulkTransfer, Long>{

    Page<BulkTransfer> findByCorporateOrderByTranDateDesc(Corporate corporate, Pageable details);
    Page<BulkTransfer> findByRetailUserOrderByTranDateDesc(RetailUser retailUser, Pageable details);



    Page<BulkTransfer> findByCorporateOrderByStatusAscTranDateDesc(Corporate corporate, Pageable details);

    boolean existsByCorporate_IdAndCustomerAccountNumberAndStatus(Long corpId,String accNumber, String status);

    List<BulkTransfer> findByStatusNotInIgnoreCase(List<String> list);

    List<BulkTransfer> findByStatus(String status);

    BulkTransfer findFirstByRefCode(String refCode);

    int countByCorporateAndStatus(Corporate corporate, String status);
    List<BulkTransfer> findByCorporate(Corporate corporate);
    List<BulkTransfer> findByRetailUser(RetailUser retailUser);

}
