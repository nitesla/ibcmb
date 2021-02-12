package longbridge.repositories;

import longbridge.models.CorporateRole;
import longbridge.models.CorporateUser;
import longbridge.models.UserGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface UserGroupRepo extends CommonRepo<UserGroup, Long> {
    UserGroup findByNameIgnoreCase(String groupName);

}
