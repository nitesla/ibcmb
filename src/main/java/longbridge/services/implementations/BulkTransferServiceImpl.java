package longbridge.services.implementations;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.services.BulkTransferService;
import longbridge.services.bulkTransfers.BulkTransferJobLauncher;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Service
public class BulkTransferServiceImpl implements BulkTransferService {

    private BulkTransferJobLauncher jobLauncher;

    @Autowired
    public void setJobLauncher(BulkTransferJobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Override
    public String makeBulkTransferRequest(BulkTransfer bulkTransfer) {
        return null;
    }

    @Override
    @Async
    public void makeBulkTransferRequest(String batchId) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.launchBulkTransferJob(batchId);
    }

    @Override
    public BulkTransfer getBulkTransferRequest(Long id) {
        return null;
    }

    @Override
    public String cancelBulkTransferRequest(Long id) {
        return null;
    }

    @Override
    public Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details) {
        return null;
    }
}
