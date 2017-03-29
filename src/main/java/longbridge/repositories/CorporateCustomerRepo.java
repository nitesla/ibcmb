package longbridge.repositories;

import longbridge.models.CorporateUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Showboy on 27/03/2017.
 */

@Repository
<<<<<<< HEAD
public interface CorporateCustomerRepo extends JpaRepository<CorporateCustomer, Long> {
=======
public interface CorporateCustomerRepo extends JpaRepository<CorporateUser, Long> {
>>>>>>> 51392ce51599684b0a967c1867b4c6af5d6fb5bd
}
