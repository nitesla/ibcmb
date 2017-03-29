package longbridge.repositories;

import longbridge.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */
<<<<<<< HEAD
<<<<<<< HEAD


=======
>>>>>>> bf54e1697758235cd7b624fca7d0c80d3414a148
=======

>>>>>>> 51392ce51599684b0a967c1867b4c6af5d6fb5bd
@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    Customer findByEmail(String email);
}
