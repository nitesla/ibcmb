package longbridge.jobs;

import longbridge.controllers.BatchJobController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Longbridge on 6/9/2017.
 */
@Component
public class CronJobs {
@Autowired
private BatchJobController jobController;
    @Scheduled(cron = "${cronJob.value}")
    public void startJob(){
//        jobController.updatAllAccounts();
//    System.out.println("cron job running");

    }
}
