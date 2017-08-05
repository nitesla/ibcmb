package longbridge.services.bulkTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Component
public class BulkTransferJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferJobLauncher.class);

    private final Job job;



    private final JobLauncher jobLauncher;

    @Autowired
    public BulkTransferJobLauncher(@Qualifier("customJob") Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }


    @Async
    public void launchBulkTransferJob(String s) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        LOGGER.info("Starting Bulk transfer job");

        jobLauncher.run(job, newExecution(s));

        LOGGER.info("Stopping Bulk transfer job");
    }



    private JobParameters newExecution(String s) {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter parameter = new JobParameter(new Date());
        JobParameter batch = new JobParameter(s);
        parameters.put("currentTime", parameter);
        parameters.put("batchId", batch);

        return new JobParameters(parameters);
    }


}
