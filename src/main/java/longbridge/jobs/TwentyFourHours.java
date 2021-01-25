package longbridge.jobs;

import longbridge.config.IbankingContext;
import longbridge.services.CronJobService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * Created by Longbridge on 9/12/2017.
 */
@DisallowConcurrentExecution
public class TwentyFourHours implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ApplicationContext contextFiveMinutes = IbankingContext.getApplicationContext();
        CronJobService cronJobService = contextFiveMinutes.getBean (CronJobService.class);
        System.out.println("twentyFourHour minute job runing");
        try {
            cronJobService.saveRunningJob("in-built",cronJobService.getCurrentExpression("category3"));
            cronJobService.updateAccountDetials();
            cronJobService.updateRetailUserDetails();
//            cronJobService.updateCorporateUserDetails();
            cronJobService.addNewAccount();
            cronJobService.updateRunningJob();
            System.out.println("twenty four hours");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
