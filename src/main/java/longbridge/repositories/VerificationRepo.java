package longbridge.repositories;

import longbridge.models.UserType;
import longbridge.models.Verification;
import longbridge.utils.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Repository
public interface VerificationRepo extends CommonRepo<Verification, Long>{

    Verification findFirstByEntityNameAndStatus(String name, VerificationStatus status);

    Page<Verification> findByStatusAndInitiatedByOrderByInitiatedOnDesc(VerificationStatus status, String initiatedBy, Pageable pageable);

    Page<Verification> findByOperationAndInitiatedByAndUserTypeAndStatusOrderByInitiatedOnDesc(String operation, String initiatedBy, UserType userType, VerificationStatus status, Pageable pageable);

    List<Verification> findByStatusAndInitiatedBy(VerificationStatus status, String initiatedBy);

    Page<Verification> findByStatusAndInitiatedByAndUserType(VerificationStatus status, String initiatedBy, UserType userType, Pageable pageable);

    Verification findFirstByEntityNameAndEntityIdAndStatus(String name, long id, VerificationStatus status);

    long countByInitiatedByAndUserTypeAndStatus(String username, UserType userType, VerificationStatus status);

    Page<Verification> findByInitiatedByAndUserTypeAndStatusOrderByInitiatedOnDesc(String initiatedby, UserType userType, VerificationStatus status, Pageable pageable);

    List<Verification> findByInitiatedByAndUserType(String initiatedby, UserType userType);

    @Query( "select v from Verification v where v.initiatedBy != :initiated and v.userType=:userType and v.operation in :permissionlist and v.status ='PENDING' order by v.initiatedOn Desc")
    List<Verification> findVerificationForUser(@Param("initiated") String initiatedBy, @Param("userType") UserType userType, @Param("permissionlist") List<String> operation);


    @Query( "select v from Verification v where v.initiatedBy <> :initiated and v.userType=:userType and v.operation in :permissionlist and v.status ='PENDING' order by v.initiatedOn Desc")
    Page<Verification> findVerificationForUsers(@Param("initiated") String initiatedBy, @Param("userType") UserType userType, @Param("permissionlist") List<String> operation,Pageable pageable);


    @Query( "select v from Verification v where v.verifiedBy = :verified and v.userType=:userType and (v.status ='APPROVED' or v.status ='DECLINED') order by v.verifiedOn Desc")
    Page<Verification> findVerifiedOperationsForUser(@Param("verified") String verifiedBy,@Param("userType") UserType userType, Pageable pageable);


}
