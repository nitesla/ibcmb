package longbridge.repositories;

import longbridge.models.RegistrationCodeMgt;
import org.springframework.stereotype.Repository;

/**
 * Created by Longbridge on 7/14/2017.
 */
@Repository
public interface RegistrationCodeMgtRepo  extends CommonRepo<RegistrationCodeMgt,Long> {
    RegistrationCodeMgt findCodeByCifId(String cifId);
}
