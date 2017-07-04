package longbridge.jobs;

import longbridge.services.CronJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Longbridge on 6/29/2017.
 */
public class RunningJob implements Job {
    @Autowired
    private CronJobService cronJobService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        cronJobService.updateAllAccountName();
//        cronJobService.updateAllBVN();
//        cronJobService.updateAllAccountCurrency();

        System.out.println("job runing");
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
