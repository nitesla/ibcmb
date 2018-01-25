package longbridge.services.bulkTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BulkTransferStatusNotificationListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(BulkTransferStatusNotificationListener.class);

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

            log.info("!!! Completed Status Update for Bulk Transfer Batch ID {} with status {}", batchId, BatchStatus.COMPLETED.toString());

            try {


                //TODO : Update the records status

            } catch (Exception e) {
                log.error("Exception occurred  {}", e);
            }


        } else {
            log.error("!!! JOB FINISHED for Bulk Transfer ID {} with status {}", batchId, jobExecution.getStatus().toString());
        }
    }


    private void updateStatus() {


    }
}
