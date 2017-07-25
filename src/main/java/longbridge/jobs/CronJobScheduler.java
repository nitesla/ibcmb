
package longbridge.jobs;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Longbridge on 6/25/2017.
 */
@Service
public class CronJobScheduler {
    public static void startJobs() {
        JobKey OneTimeKey = new JobKey("OneTime", "ibtest");
        JobDetail OneTimeJobs = JobBuilder.newJob(RunningJob.class)
                .withIdentity(OneTimeKey).build();
        /**
         * JOB Triggers
         * @Shedules
         */
        Trigger oneTime = TriggerBuilder
                .newTrigger()
                .withIdentity("oneTime", "ibtest")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0 0/1 1/1 * ? *"))
                .build();

        /**
         * STart Schedules.............
         */
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(OneTimeJobs, oneTime);
            scheduler.start();
        } catch (SchedulerException e) {

            e.printStackTrace();
        }
    }
}