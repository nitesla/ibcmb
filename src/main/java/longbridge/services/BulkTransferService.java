package longbridge.services;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;

/**
 * Created by ayoade_farooq@yahoo.com on 6/14/2017.
 */
public interface BulkTransferService
{

	String makeBulkTransferRequest(BulkTransfer bulkTransfer);

	void makeBulkTransferRequest(String batchId) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

//TransferRequestDTO make;

	BulkTransfer getBulkTransferRequest(Long id);

	String cancelBulkTransferRequest(Long id);

	Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details);


}
