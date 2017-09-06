package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CreditRequestRepo;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Ayoade Farooq
 * @ayoade_farooq@yahoo.com
 */
@Component
public class TransferStatusJobLauncher {


    private static final Logger LOGGER = LoggerFactory.getLogger(TransferStatusJobLauncher.class);

    private final Job job;

    private final JobLauncher jobLauncher;


    private BulkTransferRepo bulkTransferRepo;
    private CreditRequestRepo creditRequestRepo;

    @Autowired
    TransferStatusJobLauncher(@Qualifier("restJob") Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    @Autowired
    public void setBulkTransferRepo(BulkTransferRepo bulkTransferRepo) {
        this.bulkTransferRepo = bulkTransferRepo;
    }

    @Autowired
    public void setCreditRequestRepo(CreditRequestRepo creditRequestRepo) {
        this.creditRequestRepo = creditRequestRepo;
    }

    @Scheduled(cron = "${rest.api.to.database.job.cron}")
    void updateTransferStatusJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        LOGGER.info("Starting restJob job");

        List<BulkTransfer> transferList = bulkTransferRepo.findByStatusNotInIgnoreCase(Arrays.asList("S"));
        transferList.stream().filter(Objects::nonNull)
                .forEach(

                        i -> {

                            try {
                                String batch = "" + i.getId();
//                                LOGGER.info("running update job for batch  {}", batch);
                                jobLauncher.run(job, newExecution(batch));
                            } catch (JobExecutionAlreadyRunningException e) {
                                e.printStackTrace();
                            } catch (JobRestartException e) {
                                e.printStackTrace();
                            } catch (JobInstanceAlreadyCompleteException e) {
                                e.printStackTrace();
                            } catch (JobParametersInvalidException e) {
                                e.printStackTrace();
                            }
                        }

                );


        // jobLauncher.run(job, newExecution(s));

//        LOGGER.info("Stopping restJob job");
    }

    private JobParameters newExecution(String s) {
        Map<String, JobParameter> parameters = new HashMap<>();

        JobParameter parameter = new JobParameter(new Date());
        JobParameter batch = new JobParameter(s);
        parameters.put("time", parameter);
        parameters.put("batchId", batch);

        return new JobParameters(parameters);
    }


}
