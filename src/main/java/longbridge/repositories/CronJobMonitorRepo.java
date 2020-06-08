package longbridge.repositories;

import longbridge.models.CronJobMonitor;
import org.springframework.stereotype.Repository;

/**
 * Created by Longbridge on 7/5/2017.
 */
@Repository
public interface CronJobMonitorRepo  extends CommonRepo<CronJobMonitor,Long> {
    CronJobMonitor findFirstByOrderByStillRunningDesc();
}
