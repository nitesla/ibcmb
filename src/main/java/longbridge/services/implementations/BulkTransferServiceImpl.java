package longbridge.services.implementations;

import longbridge.dtos.BulkTransferDTO;
import longbridge.dtos.CreditRequestDTO;
import longbridge.models.BulkTransfer;
import longbridge.models.Corporate;
import longbridge.models.CreditRequest;
import longbridge.repositories.BulkTransferRepo;
import longbridge.services.BulkTransferService;
import longbridge.services.bulkTransfers.BulkTransferJobLauncher;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Service
public class BulkTransferServiceImpl implements BulkTransferService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private BulkTransferRepo bulkTransferRepo;

    private MessageSource messageSource;

    private BulkTransferJobLauncher jobLauncher;

    private ModelMapper modelMapper;

    @Autowired
    public BulkTransferServiceImpl(BulkTransferRepo bulkTransferRepo, ModelMapper modelMapper
            , BulkTransferJobLauncher jobLauncher,MessageSource messageSource

    ) {
        this.bulkTransferRepo = bulkTransferRepo;
        this.modelMapper = modelMapper;
        this.jobLauncher=jobLauncher;
        this.messageSource=messageSource;
    }


    @Override
    public String makeBulkTransferRequest(BulkTransfer bulkTransfer) {
        logger.trace("Transfer details valid {}", bulkTransfer);
        //validate bulk transfer
        BulkTransfer transfer = bulkTransferRepo.save(bulkTransfer);
        try {
            jobLauncher.launchBulkTransferJob(""+transfer.getId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception occurred {}",e);
            return messageSource.getMessage("bulk.transfer.failure", null, null);
        }


        return messageSource.getMessage("bulk.transfer.success", null, null);
    }


    @Override
    public Page<BulkTransfer> getAllBulkTransferRequests(Corporate corporate, Pageable details) {
        //return bulkTransferRepo.findByCorporate(corporate,details);
        return null;
    }

    @Override
    public Page<BulkTransferDTO> getBulkTransferRequests(Corporate corporate, Pageable details) {
        Page<BulkTransfer> page = bulkTransferRepo.findByCorporate(corporate, details);
        List<BulkTransferDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<BulkTransferDTO> pageImpl = new PageImpl<BulkTransferDTO>(dtOs, details, t);
        return pageImpl;
    }

    public BulkTransferDTO convertEntityToDTO(BulkTransfer bulkTransfer) {
        BulkTransferDTO bulkTransferDTO = new BulkTransferDTO();
        bulkTransferDTO.setId(bulkTransfer.getId());
        bulkTransferDTO.setDebitAccount(bulkTransfer.getDebitAccount());
        bulkTransferDTO.setRefCode(bulkTransfer.getRefCode());
        bulkTransferDTO.setRequestDate(bulkTransfer.getRequestDate());
        bulkTransferDTO.setStatus(bulkTransfer.getStatus());
        return bulkTransferDTO;
    }

    public List<BulkTransferDTO> convertEntitiesToDTOs(Iterable<BulkTransfer> bulkTransfers) {
        List<BulkTransferDTO> bulkTransferDTOList = new ArrayList<>();
        for (BulkTransfer bulkTransfer : bulkTransfers) {
            BulkTransferDTO bulkTransferDTO = convertEntityToDTO(bulkTransfer);
            bulkTransferDTOList.add(bulkTransferDTO);
        }
        return bulkTransferDTOList;
    }


    @Override
    public String cancelBulkTransferRequest(Long id) {
        //cancelling bulk transaction request
        BulkTransfer one = bulkTransferRepo.getOne(id);
        one.setStatus("X");
        bulkTransferRepo.save(one);
        return null;
    }

    @Override
    public BulkTransfer getBulkTransferRequest(Long id) {
        return bulkTransferRepo.getOne(id);
    }

    @Override
    public Page<CreditRequestDTO> getCreditRequests(BulkTransfer bulkTransfer, Pageable pageable) {
        List<CreditRequest> creditRequests = bulkTransfer.getCrRequestList();
        Page<CreditRequest> page = new PageImpl<CreditRequest>(creditRequests);
        List<CreditRequestDTO> dtOs = convertEntToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<CreditRequestDTO> pageImpl = new PageImpl<CreditRequestDTO>(dtOs, pageable, t);
        return pageImpl;
    }

    @Override
    public Page<CreditRequest> getAllCreditRequests(BulkTransfer bulkTransfer, Pageable pageable) {
        Page<CreditRequest> page = (Page<CreditRequest>) bulkTransfer.getCrRequestList();
        List<CreditRequest> creditRequests = page.getContent();
        long t = page.getTotalElements();
        Page<CreditRequest> pageImpl = new PageImpl<CreditRequest>(creditRequests, pageable, t);
        return pageImpl;
    }


    public List<CreditRequestDTO> convertEntToDTOs(Iterable<CreditRequest> creditRequests) {
        List<CreditRequestDTO> creditRequestDTOList = new ArrayList<>();
        for (CreditRequest creditRequest : creditRequests) {
            CreditRequestDTO creditRequestDTO = convertEntityToDTO(creditRequest);
            creditRequestDTOList.add(creditRequestDTO);
        }
        return creditRequestDTOList;
    }


    public CreditRequestDTO convertEntityToDTO(CreditRequest creditRequest) {
        CreditRequestDTO creditRequestDTO = new CreditRequestDTO();
        creditRequestDTO.setRefCode(creditRequest.getRefCode());
        creditRequestDTO.setNarration(creditRequest.getNarration());
        creditRequestDTO.setAmount(creditRequest.getAmount());
        creditRequestDTO.setAccountName(creditRequest.getAccountName());
        creditRequestDTO.setSortCode(creditRequest.getSortCode());
        creditRequestDTO.setAccountNumber(creditRequest.getAccountNumber());
        creditRequestDTO.setSerial(creditRequest.getSerial());
        creditRequestDTO.setStatus(creditRequest.getStatus());
        return creditRequestDTO;
    }
}
