package longbridge.repositories;

import longbridge.models.Corporate;
import longbridge.models.CorporateRole;
import longbridge.models.CorporateUser;
import longbridge.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */
public interface CorporateUserRepo extends JpaRepository<CorporateUser, Long> {
    CorporateUser findFirstByUserName(String s);
    CorporateUser findFirstByUserNameIgnoreCaseAndCorporate_Id(String s,Long id);
//    CorporateUser findFirstByCustomerId(String customerId);
	Iterable<CorporateUser> findByRole(Role r);
	boolean existsByUserNameIgnoreCase(String username);


    Page<CorporateUser> findByRole(Role r, Pageable pageDetail);
    CorporateUser   findByUserName(String s);
    CorporateUser findByPhoneNumberAndCorporate_Id(String phone, Long id);
    CorporateUser findFirstByCorporateAndEmailIgnoreCase(Corporate corporate, String email);
    CorporateUser   findFirstByUserNameIgnoreCase(String s);
    //CorporateUser   findByUserNameAndCorporate_CustomerId(String s,String s1);

    CorporateUser   findFirstByUserNameIgnoreCaseAndCorporate_CustomerIdIgnoreCase(String s,String s1);
    CorporateUser   findFirstByUserNameIgnoreCaseAndCorporate_CorporateIdIgnoreCase(String s,String s1);
    CorporateUser findByEmailIgnoreCase(String email);
    Page<CorporateUser> findByCorporateId(Long corpId, Pageable pageDetail);
	Integer countByRole(Role role);
   // List<CorporateUser> findByCorporateAndCorporateRoleIsNull(Corporate corporate);
    List<CorporateUser> findByCorporate(Corporate corporate);
    
    @Query("select cu from CorporateUser cu where not exists (select 1 from CorporateRole cr where cu member of cr.users and cr.corporate=:corp) and cu.corporate=:corp")
    List<CorporateUser> findUsersWithoutRole(@Param("corp") Corporate corporate);

    CorporateUser findByRole(CorporateRole role );
    @Query("select u from CorporateRole cr inner join cr.users u inner join u.corporate c where c=:corp")
    List<CorporateUser> findUsersInRole2(@Param("corp") Corporate corporate);

    @Transactional
    @Modifying(clearAutomatically=true)
    @Query(value="update corporate_user set feed_back_status=?1 where id=?2",nativeQuery=true)
    void updateFeedBackStatus(String status,Long id);

    @Query("select c from CorporateUser c where lower(c.corporate.corporateId) like %:corporateId% and lower(c.corporate.name) like %:name% ")
    Page<CorporateUser> findCorporateUser(@Param("corporateId")String corporateId,@Param("name") String name,Pageable pageable);



}
