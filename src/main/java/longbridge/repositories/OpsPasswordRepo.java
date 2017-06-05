package longbridge.repositories;

import longbridge.models.AdminUser;
import longbridge.models.OperationsUser;
import longbridge.models.OpsPassword;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 6/5/2017.
 */
@Repository
public interface OpsPasswordRepo extends CommonRepo<OpsPassword,Long> {

    List<OpsPassword> findByOperationsUser(OperationsUser opsUser);

    int countByOperationsUser(OperationsUser opsUser);

    OpsPassword findFirstByOperationsUser(OperationsUser operationsUser);


}
