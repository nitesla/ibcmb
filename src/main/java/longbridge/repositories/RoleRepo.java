package longbridge.repositories;

import longbridge.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */
<<<<<<< HEAD

=======
>>>>>>> bf54e1697758235cd7b624fca7d0c80d3414a148
@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{
    Role findByName(String name);
}
