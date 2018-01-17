package longbridge.jobs;

import longbridge.config.SpringContext;
import longbridge.services.CronJobService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by Longbridge on 8/29/2017.
 */
@Component
public class StartJob implements InitializingBean{
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            ApplicationContext context = SpringContext.getApplicationContext();
            CronJobService cronJobService = context.getBean (CronJobService.class);
            System.out.println("start job "+cronJobService.startCronJob());
            if(cronJobService.startCronJob()) {
                CronJobScheduler.startJobs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
