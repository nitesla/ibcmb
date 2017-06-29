package longbridge.repositories;

import longbridge.models.MakerChecker;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by chiomarose on 15/06/2017.
 */

@Repository
public interface MakerCheckerRepo extends CommonRepo<MakerChecker, Long>
{
    MakerChecker findFirstByOperation(String operation);
    boolean existsByOperation(String operation);
    boolean existsByOperationAndEnabled(String name, String enabled);
}

