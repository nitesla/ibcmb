package longbridge.repositories;

import longbridge.models.Verification;
import org.springframework.stereotype.Repository;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
@Repository
public interface VerificationRepo extends CommonRepo<Verification, Long>{

    Verification findFirstByEntityNameAndVerificationStatus(String name, Verification.VerificationStatus status);
}
