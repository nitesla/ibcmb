package longbridge.repositories;

import longbridge.models.AdminUser;
import longbridge.models.RetailUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 4/5/2017.
 */
@Repository

public interface RetailUserRepo extends CommonRepo<RetailUser, Long> {

    RetailUser findByUserName(String s);

}
