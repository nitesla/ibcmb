package longbridge.repositories;

import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Fortune on 4/4/2017.
 */
public interface LocalBeneficiaryRepo extends CommonRepo<LocalBeneficiary,Long>{

    Iterable<LocalBeneficiary> findByUser(RetailUser user);

    Iterable<LocalBeneficiary> findByUserAndDelFlag(RetailUser user, String delFlag);

}
