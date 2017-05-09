package longbridge.repositories;

import longbridge.models.ServiceRequest;
import longbridge.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface ServiceRequestRepo extends CommonRepo<ServiceRequest, Long> {

    Iterable<ServiceRequest> findByUser(User user);

    Page<ServiceRequest> findByUser(User user, Pageable pageable);
}
