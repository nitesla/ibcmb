package longbridge.repositories;

import longbridge.models.Verification;
import longbridge.utils.verificationStatus;
import org.springframework.stereotype.Repository;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Repository
public interface VerificationRepo extends CommonRepo<Verification, Long>{

    Verification findFirstByEntityNameAndStatus(String name, verificationStatus status);

    Verification findFirstByEntityNameAndEntityIdAndStatus(String name, Long id, verificationStatus status);

}
