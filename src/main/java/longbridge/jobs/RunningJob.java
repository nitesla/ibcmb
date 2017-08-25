package longbridge.jobs;

import longbridge.config.SpringContext;
import longbridge.exception.InternetBankingException;
import longbridge.services.CronJobService;
import longbridge.services.implementations.CronJobServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Longbridge on 6/29/2017.
 */
@org.springframework.stereotype.Component
public class RunningJob implements Job {
//    @Autowired
//    private CronJobService cronJobService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ApplicationContext context = SpringContext.getApplicationContext();
        CronJobService cronJobService = context.getBean (CronJobService.class);
        System.out.println("job runing");
        try {
            CompileJasper.compile();
//            cronJobService.saveRunningJob("in-built",cronJobService.getCurrentExpression());
//            cronJobService.updateAccountDetials();
            cronJobService.updateRetailUserDetails();
//            cronJobService.updateCorporateUserDetails();
//
//            cronJobService.updateRunningJob();
        } catch (InternetBankingException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void runJars() throws IOException {
//        Desktop.getDesktop().open(new File(""));
        try {
            Process ps=Runtime.getRuntime().exec(new String[]{"java","-jar","A.jar"});
            ps.waitFor();
            java.io.InputStream is=ps.getInputStream();
            byte b[]=new byte[is.available()];
            is.read(b,0,b.length);
            System.out.println(new String(b));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
