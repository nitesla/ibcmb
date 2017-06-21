package longbridge.repositories;

import longbridge.models.MakerChecker;
import org.springframework.stereotype.Repository;

/**
 * Created by chiomarose on 15/06/2017.
 */

@Repository
public interface MakerCheckerRepo extends CommonRepo<MakerChecker, Long>
{
    MakerChecker findFirstByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndEnabled(String name, String enabled);
    boolean existsByCode(String code);
}

