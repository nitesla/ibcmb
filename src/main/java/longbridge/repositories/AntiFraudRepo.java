package longbridge.repositories;

import longbridge.models.AntiFraudData;
import org.springframework.stereotype.Repository;

@Repository
public interface AntiFraudRepo extends CommonRepo<AntiFraudData, Long> {
}
