package longbridge.repositories;

import longbridge.models.RetailUser;
import longbridge.models.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.Transactional;

/**
 * Created by Fortune on 4/5/2017.
 */
@Repository
@Transactional
public interface RetailUserRepo extends CommonRepo<RetailUser, Long> {

	Iterable<RetailUser> findByRole(Role r);
    Page<RetailUser> findByRole(Role r, Pageable pageDetail);
    RetailUser findFirstByUserName(String s);
    RetailUser findFirstByCustomerId(String customerId);
    @Modifying
    @Query("update RetailUser  u set u.lastLoginDate = current_timestamp() , u.lockedUntilDate = NULL, u.noOfLoginAttempts = 0 where u.userName = :name")
    void updateUserAfterLogin(@Param("name") String userName);

}
