package longbridge.repositories;

import longbridge.models.TransRequest;

/**
 * Created by chigozirim on 3/31/17.
 */
public interface TransferRequestRepo extends CommonRepo<TransRequest, Long> {
    TransRequest findById(long id);




}
