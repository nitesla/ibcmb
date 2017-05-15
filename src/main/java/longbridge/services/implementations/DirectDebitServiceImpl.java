package longbridge.services.implementations;

import java.util.List;

import org.joda.time.LocalDate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import longbridge.dtos.DirectDebitDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.DirectDebit;
import longbridge.models.FinancialInstitution;
import longbridge.models.RetailUser;
import longbridge.repositories.DirectDebitRepo;
import longbridge.services.DirectDebitService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.TransferService;
import longbridge.utils.TransferType;

public class DirectDebitServiceImpl implements DirectDebitService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DirectDebitRepo directDebitRepo;
	
	@Autowired
	private TransferService transferService;
	
	@Autowired
	private FinancialInstitutionService financialInstitutionService;

	@Override
	public String addDirectDebit(RetailUser user, DirectDebitDTO directDebitDTO) {
		//get current time
		LocalDate now = LocalDate.now(); 
		DirectDebit directDebit = convertDTOToEntity(directDebitDTO);
		directDebit.setDateCreated(now.toDate());
		directDebit.setRetailUser(user);
		directDebit.setNextDebitDate(now.plusDays(directDebit.getIntervalDays()).toDate());
		directDebitRepo.save(directDebit);
		return "Successfully added directdebit";
	}

	@Override
	public void deleteDirectDebit(Long directDebitId) {
		DirectDebit directDebit = getDirectDebit(directDebitId);
		try{
			directDebit.setDelFlag("Y");
			directDebitRepo.delete(directDebit);
			logger.info("Successfully deleted DirectDebit");
		}catch(Exception exc){
			logger.error("Error", exc);
		}

	}

	@Override
	public DirectDebit getDirectDebit(Long directDebitId) {
		return directDebitRepo.getOne(directDebitId);
	}

	@Override
	public void performDirectDebit(DirectDebit directDebit) {
		TransferRequestDTO transferRequest = new TransferRequestDTO();
		transferRequest.setAmount(directDebit.getAmount());
		transferRequest.setBeneficiaryAccountName(directDebit.getBeneficiary().getAccountName());
		transferRequest.setBeneficiaryAccountNumber(directDebit.getBeneficiary().getAccountNumber());
		transferRequest.setCustomerAccountNumber(directDebit.getDebitAccount());
		FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(directDebit.getBeneficiary().getBeneficiaryBank());
		transferRequest.setFinancialInstitution(financialInstitution);
		transferRequest.setNarration("Direct Debit:" + directDebit.getNarration());
//		transferRequest.setReferenceNumber(referenceNumber);
		transferRequest.setRemarks(directDebit.getNarration());
		//TODO use the correct bank code of coronation merchant bank
		if(directDebit.getBeneficiary().getBeneficiaryBank().equals("CORONAION")){
			transferRequest.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
		}else{
			transferRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
		}
		transferService.makeTransfer(transferRequest);
		directDebit.proceedToNextDebitDate();
		directDebitRepo.save(directDebit);
	}

	private DirectDebitDTO convertEntityToDTO(DirectDebit directDebit) {
		return modelMapper.map(directDebit, DirectDebitDTO.class);
	}

	private DirectDebit convertDTOToEntity(DirectDebitDTO directDebitDTO) {
		return modelMapper.map(directDebitDTO, DirectDebit.class);
	}

	@Override
	public List<DirectDebit> getDueDirectDebits() {
		// TODO Auto-generated method stub
		return directDebitRepo.findByNextDebitDate(LocalDate.now().toDate());
	}

	@Override
	public List<DirectDebit> getUserDirectDebits(RetailUser user) {
		// TODO Auto-generated method stub
		
		return null;
	}
}
