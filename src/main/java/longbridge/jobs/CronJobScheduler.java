
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

//        JobKey FiveMinsKey = new JobKey("FiveMins", "ibtest");
//        JobDetail FiveMinsJobs = JobBuilder.newJob(FiveMins.class)
//                .withIdentity(FiveMinsKey).build();
//        JobKey OneHourKey = new JobKey("OneHour", "ibtest");
//        JobDetail OneHourJobs = JobBuilder.newJob(OneHour.class)
//                .withIdentity(OneHourKey).build();
//        JobKey ThreeSecsKey = new JobKey("ThreeSecs", "ibtest");
//        JobDetail ThreeSecsJobs = JobBuilder.newJob(ThreeSecs.class)
//                .withIdentity(ThreeSecsKey).build();
//
//        JobKey TwentySecsKey = new JobKey("TwentySecs", "ibtest2");
//        JobDetail TwentySecsJobs = JobBuilder.newJob(TwentySecs.class)
//                .withIdentity(TwentySecsKey).build();
//
//        JobKey Daily0030Key = new JobKey("Daily0030", "ibtest");
//        JobDetail Daily0030Jobs = JobBuilder.newJob(Daily0030.class)
//                .withIdentity(Daily0030Key).build();
//
//
//        JobKey EveryMinuteKey = new JobKey("EveryMinute", "ibtest");
//        JobDetail EveryMinuteJobs = JobBuilder.newJob(EveryMinute.class)
//                .withIdentity(EveryMinuteKey).build();
//
//        JobKey TwentyFourHoursKey = new JobKey("TwentyFourHours", "ibtest");
//        JobDetail TwentyFourHoursJobs = JobBuilder.newJob(TwentyFourHours.class)
//                .withIdentity(TwentyFourHoursKey).build();
//
//        JobKey FortyEightHoursKey = new JobKey("FourtyEightHours", "ibtest");
//        JobDetail FortyEightHoursJobs = JobBuilder.newJob(FortyEightHours.class)
//                .withIdentity(FortyEightHoursKey).build();
//
//        JobKey SevenDaysKey = new JobKey("SevenDays", "ibtest");
//        JobDetail SevenDaysKeyJobs = JobBuilder.newJob(SevenDays.class)
//                .withIdentity(SevenDaysKey).build();


        /**
         * JOB Triggers
         * @Shedules
         */
        Trigger oneTime = TriggerBuilder
                .newTrigger()
                .withIdentity("oneTime", "ibtest")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0/10 0/1 * 1/1 * ? *"))
                .build();
//        Trigger fiveMins = TriggerBuilder
//                .newTrigger()
//                .withIdentity("fiveMins", "bcons")
//                .withSchedule(
//                        CronScheduleBuilder.cronSchedule("0 0/5 * 1/1 * ? *"))
//                .build();
//        Trigger oneHour = TriggerBuilder
//                .newTrigger()
//                .withIdentity("oneHour", "bcons")
//                .withSchedule(
//                        CronScheduleBuilder.cronSchedule("0 0/59 * 1/1 * ? *"))
//                .build();
//        Trigger threeMins = TriggerBuilder
//                .newTrigger()
//                .withIdentity("threeSecs", "bcons")
//                .withSchedule(
//                        CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
//                .build();
//        Trigger everyMinute = TriggerBuilder

//                .newTrigger()
//                .withIdentity("oneTime", "bcons3")
//                .withSchedule(
//                        CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
//                .build();
////        Trigger fiveMins = TriggerBuilder
////                .newTrigger()
////                .withIdentity("fiveMins", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 0/5 * 1/1 * ? *"))
////                .build();
////        Trigger oneHour = TriggerBuilder
////                .newTrigger()
////                .withIdentity("oneHour", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 0/59 * 1/1 * ? *"))
////                .build();
////        Trigger threeMins = TriggerBuilder
////                .newTrigger()
////                .withIdentity("threeSecs", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
////                .build();
////        Trigger everyMinute = TriggerBuilder
////                .newTrigger()
////                .withIdentity("everyminute", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
////                .build();
////        Trigger twentySec = TriggerBuilder
////                .newTrigger()
////                .withIdentity("twentySec", "bcons2")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0/20 * * * * ?"))
////                .build();
////
////        Trigger daily0030  = TriggerBuilder
////                .newTrigger()
////                .withIdentity("daily0030", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 30 0 1/1 * ? *"))
////                .build();
////
////        Trigger twentyFourHours = TriggerBuilder
////                .newTrigger()
////                .withIdentity("twentyFourHours", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 0 23 1/1 * ? *"))
//////                        CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *"))
////                .build();
////        Trigger fortyEightHours = TriggerBuilder
////                .newTrigger()
////                .withIdentity("fortyEightHours", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 0 4 1/2 * ? *"))
////                .build();
////        Trigger sevenDays = TriggerBuilder
////                .newTrigger()
////                .withIdentity("sevenDays", "bcons")
////                .withSchedule(
////                        CronScheduleBuilder.cronSchedule("0 0 3 1/7 * ? *"))
////                .build();
//
//
//
//
//
//
//
//
//
//
//
//
//        /**
//         * STart Schedules.............
//         */
//        try {
//
//            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
////            scheduler.scheduleJob(OneTimeJobs, oneTime);
//
////           scheduler.scheduleJob(OneTimeJobs, oneTime);
////            scheduler.scheduleJob(TwentySecsJobs, twentySec);
//
////            scheduler.scheduleJob(TwentySecsJobs, twentySec);
////            scheduler.scheduleJob(TwentySecsJobs, twentySec);
////            scheduler.scheduleJob(Daily0030Jobs, daily0030);
////            scheduler.scheduleJob(EveryMinuteJobs, everyMinute);
////            scheduler.scheduleJob(FiveMinsJobs, fiveMins);
////            scheduler.scheduleJob(OneHourJobs, oneHour);
////            scheduler.scheduleJob(ThreeSecsJobs, threeMins);
//            scheduler.scheduleJob(OneTimeJobs, oneTime);
////            scheduler.scheduleJob(FortyEightHoursJobs, fortyEightHours);
////            scheduler.scheduleJob(SevenDaysKeyJobs, sevenDays);
//            scheduler.start();
//
//        } catch (SchedulerException e) {
//
//            e.printStackTrace();
//        }
//    }
//
//}
    }
}