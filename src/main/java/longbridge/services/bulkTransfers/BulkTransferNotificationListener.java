package longbridge.services.bulkTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BulkTransferNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(BulkTransferNotificationListener.class);


    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {

            log.info("!!! JOB STARTED AT {}! ", new Date());

        } catch (Exception e) {
           log.error("Exception occurred {}",e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info("!!! JOB FINISHED! ");


            try {
//TODO : Update the records status

            } catch (Exception e) {
                log.error("Exception occurred  {}", e);
            }


        } else {
            // throw new RuntimeException("ERROR WITH RUNNING BATCH JOB");
        }
    }
}
