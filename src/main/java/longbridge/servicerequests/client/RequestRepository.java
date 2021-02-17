package longbridge.servicerequests.client;

import longbridge.models.OperationsUser;
import longbridge.models.UserType;
import longbridge.repositories.CommonRepo;
import longbridge.servicerequests.config.RequestConfigInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RequestRepository extends CommonRepo<ServiceRequest, Long> {

    @Query("select sr from ServiceRequest sr where sr.entityId=:entityId and sr.userType= :type order by sr.dateRequested desc")
    Page<ServiceRequest> findForUser(@Param("type") UserType type, @Param("entityId") Long entityId, Pageable pageDetails);

    @Query("select sr from ServiceRequest sr where sr.serviceReqConfigId in (:configByGroup) order by sr.dateRequested desc ")
    Page<ServiceRequest> findByConfigIds(@Param("configByGroup") List<Long> configByGroupId, Pageable pageDetails);

    @Query("select count(sr) from ServiceRequest sr where sr.serviceReqConfigId in (:configByGroup) and sr.currentStatus not in ('C','R')")
    Integer unattendRequest(@Param("configByGroup") List<Long> configByGroupId);

    @Query("select count(sr) from ServiceRequest sr where sr.serviceReqConfigId in (:configByGroup)")
    Integer allRequest(@Param("configByGroup") List<Long> configByGroupId);
}
