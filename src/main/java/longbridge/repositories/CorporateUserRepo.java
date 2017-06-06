package longbridge.repositories;

import longbridge.models.CorporateUser;
import longbridge.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Fortune on 4/5/2017.
 */
public interface CorporateUserRepo extends JpaRepository<CorporateUser, Long> {
    CorporateUser findFirstByUserName(String s);
//    CorporateUser findFirstByCustomerId(String customerId);
	Iterable<CorporateUser> findByRole(Role r);
    Page<CorporateUser> findByRole(Role r, Pageable pageDetail);
    CorporateUser   findByUserName(String s);
    CorporateUser   findFirstByUserNameIgnoreCase(String s);
    CorporateUser   findByUserNameAndCorporate_CustomerId(String s,String s1);
    CorporateUser   findFirstByUserNameIgnoreCaseAndCorporate_CustomerIdIgnoreCase(String s,String s1);
    Page<CorporateUser> findByCorporateId(Long corpId, Pageable pageDetail);
	Integer countByRole(Role role);


}
