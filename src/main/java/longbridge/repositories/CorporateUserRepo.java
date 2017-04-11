package longbridge.repositories;

import longbridge.models.CorporateUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Fortune on 4/5/2017.
 */
public interface CorporateUserRepo extends JpaRepository<CorporateUser, Long> {


    CorporateUser   findByUserName(String s);
}
