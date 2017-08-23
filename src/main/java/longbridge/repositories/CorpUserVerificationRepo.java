package longbridge.repositories;

import longbridge.models.CorpUserType;
import longbridge.models.CorpUserVerification;
import longbridge.utils.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Showboy on 28/07/2017.
 */
@Repository
public interface CorpUserVerificationRepo extends CommonRepo<CorpUserVerification, Long>{

    Page<CorpUserVerification> findByStatusAndInitiatedBy(VerificationStatus status, String initiatedBy, Pageable pageable);

    Page<CorpUserVerification>  findByCorpId(Long corpId, Pageable pageable);

    CorpUserVerification findFirstByEntityNameAndEntityIdAndStatus(String name, long id, VerificationStatus status);

    long countByCorpIdAndStatus(Long id, VerificationStatus status);

    Page<CorpUserVerification> findByInitiatedByAndCorpUserTypeAndStatus(String initiatedby, CorpUserType corpUserType, VerificationStatus status, Pageable pageable);

    List<CorpUserVerification> findByInitiatedByAndCorpUserType(String initiatedby, CorpUserType corpUserType);

    @Query( "select v from CorpUserVerification v where v.initiatedBy != :initiated and v.corpUserType=:corpUserType and v.operation in :permissionlist and v.status ='PENDING'")
    List<CorpUserVerification> findVerificationForUser(@Param("initiated") String initiatedBy, @Param("corpUserType") CorpUserType corpUserType, @Param("permissionlist") List<String> operation);


    @Query( "select v from CorpUserVerification v where v.initiatedBy != :initiated and v.corpUserType=:corpUserType and v.operation in :permissionlist and v.status ='PENDING'")
    Page<CorpUserVerification> findVerificationForUsers(@Param("initiated") String initiatedBy, @Param("corpUserType") CorpUserType corpUserType, @Param("permissionlist") List<String> operation,Pageable pageable);


    @Query( "select v from CorpUserVerification v where v.verifiedBy = :verified and v.corpUserType=:corpUserType and v.status !='PENDING'")
    Page<CorpUserVerification> findVerifiedOperationsForUser(@Param("verified") String verifiedBy,@Param("corpUserType") CorpUserType corpUserType, Pageable pageable);

}