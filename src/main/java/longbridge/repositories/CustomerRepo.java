package longbridge.repositories;

import longbridge.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface CustomerRepo extends JpaRepository<Customer, Long> {
}
