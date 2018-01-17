package longbridge.repositories;

import longbridge.models.SRConfig;
import org.springframework.stereotype.Repository;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Repository
public interface ServiceReqConfigRepo extends CommonRepo<SRConfig, Long>{

    SRConfig findByRequestName(String requestName);
}
