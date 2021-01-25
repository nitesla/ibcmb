package longbridge.jobs;

import longbridge.services.CronJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

/**
 * Created by Longbridge on 6/9/2017.
 */
@Service
@EnableScheduling
public class CronJobsbkp2 implements SchedulingConfigurer {
@Autowired
private CronJobService cronJobService;
    private String cronConfig() {
        //        if (defaultConfigDto != null && !defaultConfigDto.getFieldValue().isEmpty()) {
//            cronTabExpression = "0 0 4 * * ?";
//        }
        return "*/30 * * * * *";
    }
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> {
//                cronJobService.updateAllAccountName();
//                cronJobService.updateAllBVN();
//                cronJobService.updateAllAccountCurrency();
//                cronJobService.updateAccountStatus(null,null);
//                System.out.println("Cron job running");
        }, triggerContext -> {
            String cron = cronConfig();
//                System.out.println("cron job "+cron);
            CronTrigger trigger = new CronTrigger(cron);

//                System.out.println("the expression is "+trigger.getExpression());
            return trigger.nextExecutionTime(triggerContext);
        });
    }

//    @Scheduled(cron = "${cronJob.value}")
//    public void startJob(){
//        jobController.updatAllAccounts();
////    System.out.println("cron job running");
//
//    }

}
