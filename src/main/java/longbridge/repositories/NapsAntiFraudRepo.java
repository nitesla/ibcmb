package longbridge.repositories;

import longbridge.models.NapsAntiFraudData;
import org.springframework.stereotype.Repository;

@Repository
public interface NapsAntiFraudRepo extends CommonRepo<NapsAntiFraudData, Long> {

    NapsAntiFraudData save(NapsAntiFraudData napsAntiFraudData);

}
