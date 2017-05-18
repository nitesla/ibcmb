package longbridge.repositories;

import longbridge.models.PendingAuthorization;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 5/18/2017.
 */
@Repository
public interface PendingAuthorizationRepo extends CommonRepo<PendingAuthorization,Long> {
}
