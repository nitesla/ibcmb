package longbridge.repositories;

import longbridge.models.Role;
import longbridge.models.UserType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
@Transactional
public interface RoleRepo extends CommonRepo<Role, Long>{

    Role findFirstByName(String name);

    Role findByUserTypeAndName( UserType userType, String name);

    List<Role> findByUserType(UserType userType);



}
