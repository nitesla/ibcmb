package longbridge.repositories;

import longbridge.models.Verification;
import longbridge.utils.verificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    int countByCreatedByAndUserTypeAndStatus(String username,String userType, verificationStatus status);
}
