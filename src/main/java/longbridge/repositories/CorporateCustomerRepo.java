package longbridge.repositories;

import longbridge.models.CorporateCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface CorporateCustomerRepo extends JpaRepository<CorporateCustomer, Long> {
}
