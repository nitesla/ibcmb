
package longbridge.jobs;

import longbridge.config.SpringContext;
import longbridge.services.CronJobService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by Longbridge on 6/25/2017.
 */
@Service
public class CronJobScheduler {
    public static void startJobs() {
        JobKey OneTimeKey = new JobKey("OneTime", "ibtest");
        JobDetail OneTimeJobs = JobBuilder.newJob(longbridge.jobs.RunningJob.class)
                .withIdentity(OneTimeKey).build();

        JobKey FiveMinsKey = new JobKey("FiveMins", "bcons");
        JobDetail FiveMinsJobs = JobBuilder.newJob(longbridge.jobs.FiveMins.class)
                                .withIdentity(FiveMinsKey).build();
        JobKey TwentyFourHoursKey = new JobKey("TwentyFourHours", "bcons");
        JobDetail TwentyFourHoursJobs = JobBuilder.newJob(longbridge.jobs.TwentyFourHours.class)
                .withIdentity(TwentyFourHoursKey).build();
        /**
         * JOB Triggers
         * @Shedules
         */
        ApplicationContext context = SpringContext.getApplicationContext();
        CronJobService cronJobService = context.getBean (CronJobService.class);
        Trigger oneTime = TriggerBuilder
                .newTrigger()
                .withIdentity("oneTime", "ibtest")
                .withSchedule(
//                        CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
                        CronScheduleBuilder.cronSchedule(cronJobService.getCurrentExpression("category2")))
                .build();

        Trigger fiveMins = TriggerBuilder
                .newTrigger()
                .withIdentity("fiveMins", "ibtest")
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
                .withSchedule(CronScheduleBuilder.cronSchedule(cronJobService.getCurrentExpression("category1")))
                .build();
        Trigger twentyFourHours = TriggerBuilder
                .newTrigger()
                .withIdentity("twentyFourHours", "bcons")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronJobService.getCurrentExpression("category3")))

//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
                .build();
        /**
         * STart Schedules.............
         */
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(FiveMinsJobs, fiveMins);
            scheduler.scheduleJob(TwentyFourHoursJobs, twentyFourHours);
            scheduler.scheduleJob(OneTimeJobs, oneTime);
            scheduler.start();
        }catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}