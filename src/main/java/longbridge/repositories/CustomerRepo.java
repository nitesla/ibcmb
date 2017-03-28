package longbridge.repositories;

import longbridge.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */
<<<<<<< HEAD


=======
>>>>>>> bf54e1697758235cd7b624fca7d0c80d3414a148
@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    Customer findByEmail(String email);
}
