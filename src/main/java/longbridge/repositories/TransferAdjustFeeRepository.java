package longbridge.repositories;

import longbridge.models.TransferFeeAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferAdjustFeeRepository extends CommonRepo<TransferFeeAdjustment, Long> {
}
