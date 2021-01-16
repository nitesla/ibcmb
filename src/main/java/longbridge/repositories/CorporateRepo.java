package longbridge.repositories;

import longbridge.models.Corporate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface CorporateRepo extends CommonRepo<Corporate, Long> {

    Corporate findByCustomerId(String customerId);
    Corporate findFirstByCorporateId(String corporateId);
    Corporate findFirstByCorporateIdIgnoreCase(String corporateId);
    boolean existsByCorporateIdIgnoreCase(String corporateId);
    Corporate findFirstByCustomerId(String customerId);
    boolean existsByCustomerId(String customerId);
    @Query("select id from Corporate ")
    Set<Long> getAllCorporateId();



}
