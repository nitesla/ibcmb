package longbridge.jobs;

import longbridge.services.CronJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Longbridge on 6/9/2017.
 */
@Service
@EnableScheduling
public class CronJobsbkp2 implements SchedulingConfigurer {
@Autowired
private CronJobService cronJobService;
    private String cronConfig() {
        String cronTabExpression = "*/30 * * * * *";
//        if (defaultConfigDto != null && !defaultConfigDto.getFieldValue().isEmpty()) {
//            cronTabExpression = "0 0 4 * * ?";
//        }
        return cronTabExpression;
    }
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
//                cronJobService.updateAllAccountName();
//                cronJobService.updateAllBVN();
//                cronJobService.updateAllAccountCurrency();

//                System.out.println("Cron job running");
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron = cronConfig();
//                System.out.println("cron job "+cron);
                CronTrigger trigger = new CronTrigger(cron);
                Date nextExec = trigger.nextExecutionTime(triggerContext);

//                System.out.println("the expression is "+trigger.getExpression());
                return nextExec;
            }
        });
    }

//    @Scheduled(cron = "${cronJob.value}")
//    public void startJob(){
//        jobController.updatAllAccounts();
////    System.out.println("cron job running");
//
//    }

}
