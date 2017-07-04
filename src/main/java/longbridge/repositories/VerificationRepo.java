package longbridge.repositories;

import longbridge.dtos.VerificationDTO;
import longbridge.models.UserType;
import longbridge.models.Verification;
import longbridge.utils.verificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Repository
public interface VerificationRepo extends CommonRepo<Verification, Long>{

    Verification findFirstByEntityNameAndStatus(String name, verificationStatus status);

    Page<Verification> findByStatusAndInitiatedBy(verificationStatus status, String initiatedBy, Pageable pageable);

    Page<Verification> findByOperationAndInitiatedByAndUserTypeAndStatus(String operation, String initiatedBy, UserType userType, verificationStatus status, Pageable pageable);

    List<Verification> findByStatusAndInitiatedBy(verificationStatus status, String initiatedBy);

    Page<Verification> findByStatusAndInitiatedByAndUserType(verificationStatus status, String initiatedBy, UserType userType, Pageable pageable);

    Verification findFirstByEntityNameAndEntityIdAndStatus(String name, long id, verificationStatus status);

    long countByInitiatedByAndUserTypeAndStatus(String username, UserType userType, verificationStatus status);

    Page<Verification> findByInitiatedByAndUserType(String initiatedby, UserType userType,Pageable pageable);


    @Query( "select v from Verification v where v.initiatedBy != :initiated and v.userType=:userType and v.operation in :permissionlist and v.status ='PENDING'")
    List<Verification> findVerificationForUser(@Param("initiated") String initiatedBy, @Param("userType") UserType userType, @Param("permissionlist") List<String> operation);


    @Query( "select v from Verification v where v.initiatedBy != :initiated and v.userType=:userType and v.operation in :permissionlist and v.status ='PENDING'")
    Page<Verification> findVerificationForUsers(@Param("initiated") String initiatedBy, @Param("userType") UserType userType, @Param("permissionlist") List<String> operation,Pageable pageable);


    @Query( "select v from Verification v where v.verifiedBy = :verified and v.userType=:userType and v.status !='PENDING'")
    Page<Verification> findVerifiedOperationsForUser(@Param("verified") String verifiedBy,@Param("userType") UserType userType, Pageable pageable);





}
