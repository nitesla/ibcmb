package longbridge.repositories;

import longbridge.models.Corporate;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    @Query("select count(s) from ServiceRequest s, SRConfig sc, UserGroup ug "
    		+ "where s.serviceReqConfigId = sc.id and sc.groupId = ug.id and"
    		+ " s.requestStatus=:status and ug.id=:grId")
    Integer countRequestForStatus(@Param("status") String status,@Param("grId")Long grId);



}
