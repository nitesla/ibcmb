package longbridge.repositories;

import longbridge.models.AdminUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository

public interface AdminUserRepo extends CommonRepo<AdminUser, Long>{
    AdminUser findFirstByUserName(String s);
   
    @Modifying
    @Query("update AdminUser u set u.lastLoginDate = current_timestamp() , u.lockedUntilDate = NULL, u.noOfLoginAttempts = 0 where u.userName = :name")
    void updateUserAfterLogin(@Param("name") String userName);
}
