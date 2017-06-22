package longbridge.repositories;

import longbridge.models.Verification;
import longbridge.utils.verificationStatus;
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

    Verification findFirstByEntityNameAndStatus(String name, verificationStatus status);

    Page<Verification > findByStatusAndCreatedBy(verificationStatus status , String createdby,Pageable pageable);

    Verification findFirstByEntityNameAndEntityIdAndStatus(String name,long id,verificationStatus status);

    long countByCreatedByAndUserTypeAndStatus(String username,String userType, verificationStatus status);

    Page<Verification> findByStatusAndCreatedByAndUserType(verificationStatus status , String createdby,String userType,Pageable pageable);


    @Query("select v from Verification v where v.createdBy != :createdBy and v.userType=:userType")
    List<Verification> findVerificationForUser(@Param("createdBy") String createdBy,@Param("userType") String userType);

}
