package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CreditRequestRepo;
import longbridge.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
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
    private CorporateRepo corporateRepo;



   // private final Long checkRate = Long.parseLong(statusCheckRate);

    @Autowired
    public TransferStatusJobLauncher(@Qualifier("restJob") Job job, JobLauncher jobLauncher) {
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
    @Autowired
    public void setCorporateRepo(CorporateRepo corporateRepo) {
        this.corporateRepo = corporateRepo;
    }

    @Scheduled(cron = "${naps.status.check.rate}")
    void updateTransferStatusJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        LOGGER.info("Launching job scheduler to check NAPS transaction status");

        List<BulkTransfer> transferList = bulkTransferRepo.findByStatus(StatusCode.PROCESSING.toString());
        transferList.stream().filter(Objects::nonNull)
                .forEach(

                        i -> {
                            LOGGER.info("status {}",i.getStatus());

                            try {
                                String batch = "" + i.getRefCode();
                                LOGGER.info("Running status update job for transfer batchId {}", batch);
                                JobExecution jobExecution = jobLauncher.run(job, newExecution(batch));
                                LOGGER.info("Job Exit status: {}",jobExecution.getExitStatus().toString());
                                LOGGER.info("Job Execution status: {}",jobExecution.toString());

                            } catch (JobExecutionAlreadyRunningException e) {
                                LOGGER.error("Error occurred", e);
                            } catch (JobRestartException e) {
                                LOGGER.error("Error occurred", e);
                            } catch (JobInstanceAlreadyCompleteException e) {
                                LOGGER.error("Error occurred", e);
                            } catch (JobParametersInvalidException e) {
                                LOGGER.error("Error occurred", e);
                            }
                            catch (Exception e){
                                LOGGER.error("Error occurred", e);

                            }

                        }

                );

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
