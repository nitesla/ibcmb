package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.repositories.BulkTransferRepo;
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

import java.util.*;

/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Component
public class BulkTransferJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkTransferJobLauncher.class);

    private Job job;

    private Job updateJob;
    private JobLauncher jobLauncher;
    private BulkTransferRepo transferRepo;

    @Autowired
    public void setTransferRepo(BulkTransferRepo transferRepo) {
        this.transferRepo = transferRepo;
    }

    @Autowired
    public void setJob(@Qualifier("customJob") Job job) {
        this.job = job;
    }

    @Autowired
    public void setUpdateJob(@Qualifier("restJob") Job updateJob) {
        this.updateJob = updateJob;
    }

    @Autowired
    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Async
    public void launchBulkTransferJob(String s) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        LOGGER.info("Starting Bulk transfer job");

        jobLauncher.run(job, newExecution(s));

        LOGGER.info("Stopping Bulk transfer job");
    }

    /**
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    @Async
    @Scheduled(cron = "${rest.api.to.database.job.cron}")
    void launchTransferUpdateJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        LOGGER.info("Starting bulkUpdateJob job");
        List<BulkTransfer> bulkTransfers = transferRepo.findByStatusNotInIgnoreCase(Arrays.asList("S"));


        bulkTransfers.stream().filter(Objects::nonNull)
                .forEach(i -> {
                    String s = i.getId().toString();
                    try {
                        jobLauncher.run(updateJob, newExecution(s));
                    } catch (JobExecutionAlreadyRunningException e) {
                        e.printStackTrace();
                    } catch (JobRestartException e) {
                        e.printStackTrace();
                    } catch (JobInstanceAlreadyCompleteException e) {
                        e.printStackTrace();
                    } catch (JobParametersInvalidException e) {
                        e.printStackTrace();
                    }

                    LOGGER.info("Stopping bulk transfer update job");

                });

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

