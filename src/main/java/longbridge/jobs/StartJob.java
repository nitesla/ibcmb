package longbridge.jobs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created by Longbridge on 8/29/2017.
 */
@Component
public class StartJob implements InitializingBean{
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            CronJobScheduler.startJobs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
