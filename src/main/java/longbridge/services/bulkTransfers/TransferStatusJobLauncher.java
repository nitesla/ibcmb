package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CreditRequestRepo;
import longbridge.utils.StatusCode;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
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
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${naps.cutoff.time}")
    private  String cutoffTime;

    @Value("${naps.status.check.rate}")
    private String statusCheckRate;

   // private final Long checkRate = Long.parseLong(statusCheckRate);

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
    @Autowired
    public void setCorporateRepo(CorporateRepo corporateRepo) {
        this.corporateRepo = corporateRepo;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    void updateTransferStatusJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        LOGGER.info("Starting restJob job");


        List<BulkTransfer> transferList = bulkTransferRepo.findByStatus(StatusCode.PROCESSING.toString());
        transferList.stream().filter(Objects::nonNull).filter(i -> !isPastCutOffTime(getSubmittedDate(i),cutoffTime))
                .forEach(

                        i -> {

                            try {
                                String batch = "" + i.getId();
//                                LOGGER.info("running update job for batch  {}", batch);
                                jobLauncher.run(job, newExecution(batch));
                            } catch (JobExecutionAlreadyRunningException e) {
                                LOGGER.error("Error occurred", e);
                            } catch (JobRestartException e) {
                                LOGGER.error("Error occurred", e);
                            } catch (JobInstanceAlreadyCompleteException e) {
                            } catch (JobParametersInvalidException e) {
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


    private boolean isPastCutOffTime(Date submittedTime, String cutoffTime) {

        Long submittedAt = submittedTime.getTime();
        Long nextStatusCheck = System.currentTimeMillis();

        long durationInMins = (nextStatusCheck - submittedAt) / (1000 * 60);

        if (durationInMins > Long.parseLong(cutoffTime)) {
            return true;
        }

        return false;
    }

    private  Date getSubmittedDate(BulkTransfer bulkTransfer){

        Date submittedDate;
        Corporate corporate = bulkTransfer.getCorporate();
        if("SOLE".equalsIgnoreCase(corporate.getCorporateType())){
            submittedDate =  bulkTransfer.getTranDate();
        }
        else {
            submittedDate = bulkTransfer.getTransferAuth().getLastEntry();
        }
        return submittedDate;
    }
}
