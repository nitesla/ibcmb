package longbridge.repositories;

import longbridge.models.TransferSetLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferSetLimitRepository extends CommonRepo<TransferSetLimit , Long> {
}
