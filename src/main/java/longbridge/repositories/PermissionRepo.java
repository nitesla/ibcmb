package longbridge.repositories;

import longbridge.models.Permission;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface PermissionRepo extends CommonRepo<Permission, Long> {

    Iterable<Permission> findByIdNotIn(Long[] permissions);

    Iterable<Permission> findByUserType(String type);
}
