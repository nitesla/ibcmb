package longbridge.servicerequests.client;

import longbridge.models.UserType;
import longbridge.repositories.CommonRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RequestRepository extends CommonRepo<ServiceRequest, Long> {

    @Query("select sr from ServiceRequest sr where sr.entityId=:entityId and sr.userType= :type")
    Page<ServiceRequest> findForUser(@Param("type") UserType type, @Param("entityId") Long entityId);

    Optional<ServiceRequest> findFirstByRequestName(String name);
}
