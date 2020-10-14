package longbridge.jobs;

import longbridge.config.SpringContext;
import longbridge.exception.InternetBankingException;
import longbridge.services.CronJobService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * Created by Longbridge on 9/12/2017.
 */
@org.springframework.stereotype.Component
@DisallowConcurrentExecution
public class FiveMins implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ApplicationContext contextFiveMinutes = SpringContext.getApplicationContext();
        CronJobService cronJobService = contextFiveMinutes.getBean (CronJobService.class);
        System.out.println("five minute job runing");
        try {
            cronJobService.saveRunningJob("in-built",cronJobService.getCurrentExpression("category1"));
            cronJobService.updateAccountDetials();
            cronJobService.updateRetailUserDetails();
            cronJobService.updateCorporateDetails();

            cronJobService.addNewAccount();
            cronJobService.updateRunningJob();
            System.out.println("five minutes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
