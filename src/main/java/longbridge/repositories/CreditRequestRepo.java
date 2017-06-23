package longbridge.repositories;

import longbridge.models.CreditRequest;

import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 6/21/2017.
 */
public interface CreditRequestRepo extends CommonRepo<CreditRequest,Long>
{

    List<CreditRequest> findByBulkTransfer_Id(Long id);






}
