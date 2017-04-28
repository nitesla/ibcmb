package longbridge.repositories;

import longbridge.models.OperationsUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Fortune on 4/5/2017.
 */
@Repository
@Transactional
public interface OperationsUserRepo extends CommonRepo<OperationsUser, Long> {

    OperationsUser findFirstByUserName(String s);

    @Modifying
    @Query("update OperationsUser  u set u.lastLoginDate = current_timestamp() , u.lockedUntilDate = NULL, u.noOfLoginAttempts = 0 where u.userName = :name")
    void updateUserAfterLogin(@Param("name") String userName);
}