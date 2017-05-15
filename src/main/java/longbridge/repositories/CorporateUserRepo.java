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
	Iterable<CorporateUser> findByRole(Role r);
    Page<CorporateUser> findByRole(Role r, Pageable pageDetail);
    CorporateUser   findByUserName(String s);
    Page<CorporateUser> findByCorporateId(Long corpId, Pageable pageDetail);
}
