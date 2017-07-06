package longbridge.repositories;

import longbridge.models.CronJobMonitor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Longbridge on 7/5/2017.
 */
@Repository
public interface CronJobMonitorRepo  extends CommonRepo<CronJobMonitor,Long> {
    List<CronJobMonitor> findLastByStillRunning(boolean status);
}
