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

import java.util.Set;


/**
 * Created by Fortune on 4/5/2017.
 */
@Repository
@Transactional
public interface RetailUserRepo extends CommonRepo<RetailUser, Long> {

	Iterable<RetailUser> findByRole(Role r);
    Page<RetailUser> findByRole(Role r, Pageable pageDetail);
    Integer countByRole(Role r);
    RetailUser findFirstByUserName(String username);
    RetailUser findFirstByUserNameIgnoreCase(String username);
    RetailUser findFirstByEntrustIdIgnoreCase(String entrustId);
    RetailUser findFirstByEmailIgnoreCase(String email);
    RetailUser findFirstByCustomerId(String customerId);


    @Modifying
    @Query("update RetailUser  u set u.lastLoginDate = current_timestamp , u.lockedUntilDate = NULL, u.noOfLoginAttempts = 0, u.status='A' where u.userName = :name")
    void updateUserAfterLogin(@Param("name") String userName);

    @Query("select c from RetailUser c where lower(c.userName) like %:userName%  and lower(c.lastName) like %:lastName% " +
            " and lower(c.firstName) like %:firstName%  and lower(c.email) like %:email%  ")
    Page<RetailUser> findAllUsers(@Param("userName") String userName,
                                  @Param("lastName") String lastName,
                                  @Param("firstName") String firstName,
                                  @Param("email") String email,
                                  Pageable pageable);

    @Modifying(clearAutomatically=true)
    @Query(value="update RetailUser u set u.feedBackStatus=:status where u.id=:id")
    void updateFeedBackStatus(String status, Long id);


    @Query("select id from RetailUser ")
    Set<Long> getAllRetailUserId();

////
//    @Query("select a from RetailUser a where a.customerId in :customerIds")
//    List<RetailUserDTO> getCoverages(@Param("customerId")List<String> customerIds);

}
