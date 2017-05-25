package longbridge.repositories;

import longbridge.models.Corporate;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface ServiceRequestRepo extends CommonRepo<ServiceRequest, Long> {

    Iterable<ServiceRequest> findByUser(User user);

    Page<ServiceRequest> findAllByUserOrderByDateRequestedDesc(RetailUser user, Pageable pageable);

    Page<ServiceRequest> findAllByCorporateOrderByDateRequestedDesc(Corporate corporate, Pageable pageable);

    Page<ServiceRequest> findAllByOrderByDateRequestedDesc(Pageable pageable);

    List<ServiceRequest> findByRequestStatus(String status);
}
