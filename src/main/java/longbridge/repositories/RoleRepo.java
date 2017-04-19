package longbridge.repositories;

import longbridge.models.Role;
import longbridge.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
@Transactional
public interface RoleRepo extends CommonRepo<Role, Long>{
    Role findByName(String name);
}
