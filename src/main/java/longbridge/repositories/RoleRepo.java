package longbridge.repositories;

import longbridge.models.Role;
import longbridge.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface RoleRepo extends JpaRepository<UserType, Long>{
    Role findByName(String name);
}
