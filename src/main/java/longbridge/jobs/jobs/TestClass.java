
package longbridge.jobs;

import longbridge.services.CronJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Longbridge on 6/29/2017.
 */
public class TestClass implements Job {
    @Autowired
    private CronJobService cronJobService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        cronJobService.updateAllAccountName();
//        cronJobService.updateAllBVN();
//        cronJobService.updateAllAccountCurrency();
//        System.out.println("job runing");
    }
}

