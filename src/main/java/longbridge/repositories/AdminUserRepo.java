package longbridge.repositories;

import longbridge.models.AdminUser;
import longbridge.models.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;





/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository

@org.springframework.transaction.annotation.Transactional

public interface AdminUserRepo extends CommonRepo<AdminUser, Long>{
    AdminUser findFirstByUserName(String s);
    Iterable<AdminUser> findByRole(Role r);
    Page<AdminUser> findByRole(Role r, Pageable pageDetail);
    @Modifying
    @Query("update AdminUser u set u.lastLoginDate = current_timestamp() , u.lockedUntilDate = NULL, u.noOfLoginAttempts = 0 where u.userName = :name")
    void updateUserAfterLogin(@Param("name") String userName);
}
