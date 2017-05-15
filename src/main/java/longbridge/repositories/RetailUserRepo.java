package longbridge.repositories;

import longbridge.models.RetailUser;
import longbridge.models.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 4/5/2017.
 */
@Repository

public interface RetailUserRepo extends CommonRepo<RetailUser, Long> {

	Iterable<RetailUser> findByRole(Role r);
    Page<RetailUser> findByRole(Role r, Pageable pageDetail);
    RetailUser findFirstByUserName(String s);
    RetailUser findFirstByCustomerId(String customerId);

}
