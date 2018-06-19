package longbridge.services.bulkTransfers;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.repositories.BulkTransferRepo;
import longbridge.utils.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BulkTransferStatusNotificationListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(BulkTransferStatusNotificationListener.class);

    @Autowired
    private BulkTransferRepo bulkTransferRepo;
    @Value("${naps.cutoff.time}")
    private  String cutoffTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {

            log.info("!!! Bulk Transfer Status Update Job Started at {}! ", new Date());

        } catch (Exception e) {
            log.error("Exception occurred {}", e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        JobParameters parameters = jobExecution.getJobParameters();
        String batchId = parameters.getString("batchId");

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            try {
                BulkTransfer bulkTransfer = bulkTransferRepo.findFirstByRefCode(batchId);

                if(isPastCutOffTime(getSubmittedDate(bulkTransfer),cutoffTime)){
                    bulkTransfer.setStatus(StatusCode.COMPLETED.toString());
                    bulkTransfer.setStatusDescription("Completed");
                    bulkTransferRepo.save(bulkTransfer);
                    log.info("Completed Status Update for Bulk Transfer Batch ID {} with status {}", batchId, BatchStatus.COMPLETED.toString());

                }
            } catch (Exception e) {
                log.error("Exception occurred  {}", e);
            }

        } else {
            log.error("JOB FINISHED for Bulk Transfer ID {} with status {}", batchId, jobExecution.getStatus().toString());
        }
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
