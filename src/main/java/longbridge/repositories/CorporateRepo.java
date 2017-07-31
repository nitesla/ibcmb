package longbridge.repositories;

import longbridge.models.Corporate;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface CorporateRepo extends CommonRepo<Corporate, Long> {

    Corporate findByCustomerId(String customerId);
    Corporate findFirstByCorporateId(String corporateId);
    boolean existsByCorporateIdIgnoreCase(String corporateId);
    Corporate findFirstByCustomerId(String customerId);
    boolean existsByCustomerId(String customerId);
}
