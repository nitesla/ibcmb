package longbridge.repositories;

import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface CorporateRepo extends CommonRepo<Corporate, Long> {


}
