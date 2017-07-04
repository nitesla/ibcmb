package longbridge.repositories;

import longbridge.models.CronJob;
import org.springframework.stereotype.Repository;

/**
 * Created by Longbridge on 6/15/2017.
 */
@Repository
public interface CronJobRepo extends CommonRepo<CronJob,Long> {
    CronJob findByFlag(String flag);

}
