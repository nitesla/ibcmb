package longbridge.repositories;

import longbridge.models.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface CustomerTypeRepo extends JpaRepository<CustomerType, Long>{
}
