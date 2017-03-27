package longbridge.repositories;

import longbridge.models.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface ServiceRequestRepo extends JpaRepository<ServiceRequest, Long> {
}
