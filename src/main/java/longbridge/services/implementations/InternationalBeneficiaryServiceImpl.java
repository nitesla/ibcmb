package longbridge.services.implementations;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.services.InternationalBeneficiaryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InternationalBeneficiaryServiceImpl implements InternationalBeneficiaryService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private InternationalBeneficiaryRepo internationalBeneficiaryRepo;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	public InternationalBeneficiaryServiceImpl(InternationalBeneficiaryRepo internationalBeneficiaryRepo) {
		this.internationalBeneficiaryRepo = internationalBeneficiaryRepo;
	}

	@Override
	public boolean addInternationalBeneficiary(RetailUser user, InternationalBeneficiaryDTO beneficiary) {
		boolean result = false;

		try {
			InternationalBeneficiary internationalBeneficiary = convertDTOToEntity(beneficiary);
			internationalBeneficiary.setUser(user);
			this.internationalBeneficiaryRepo.save(internationalBeneficiary);
			logger.info("International beneficiary {} has been added for user {}",
					user.getUserName());
			result = true;
		} catch (Exception e) {
			logger.error("Could not add beneficiary", e.getMessage());

		}
		return result;
	}

	@Override
	public boolean deleteInternationalBeneficiary(Long beneficiaryId) {
		boolean result = false;

		try {
			InternationalBeneficiary beneficiary = internationalBeneficiaryRepo.findOne(beneficiaryId);
			beneficiary.setDelFlag("Y");
			this.internationalBeneficiaryRepo.save(beneficiary);
			logger.info("Deleted beneficiary {}", beneficiary.getAccountName());
			result = true;
		} catch (Exception e) {
			logger.error("Could not create beneficiary {}", e.getMessage());

		}
		return result;
	}

	@Override
	public InternationalBeneficiary getInternationalBeneficiary(Long id) {
		return internationalBeneficiaryRepo.findOne(id);
	}

	@Override
	public Iterable<InternationalBeneficiary> getInternationalBeneficiaries(User user) {
		// return internationalBeneficiaryRepo.findByUserAndDelFlag(user, "N");
		return null;
	}

	@Override
	public List<InternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<InternationalBeneficiary> internationalBeneficiaries){
		List<InternationalBeneficiaryDTO> internationalBeneficiaryDTOList = new ArrayList<>();
		for(InternationalBeneficiary internationalBeneficiary: internationalBeneficiaries){
			InternationalBeneficiaryDTO benDTO = convertEntityToDTO(internationalBeneficiary);
			internationalBeneficiaryDTOList.add(benDTO);
		}
		return internationalBeneficiaryDTOList;
	}

	@Override
	public InternationalBeneficiaryDTO convertEntityToDTO(InternationalBeneficiary internationalBeneficiary){
		InternationalBeneficiaryDTO internationalBeneficiaryDTO = new InternationalBeneficiaryDTO();
		internationalBeneficiaryDTO.setAccountName(internationalBeneficiary.getAccountName());
		internationalBeneficiaryDTO.setAccountNumber(internationalBeneficiary.getAccountNumber());
		internationalBeneficiaryDTO.setBeneficiaryBank(internationalBeneficiary.getBeneficiaryBank());
		internationalBeneficiaryDTO.setSwiftCode(internationalBeneficiary.getSwiftCode());
		internationalBeneficiaryDTO.setSortCode(internationalBeneficiary.getSortCode());
		internationalBeneficiaryDTO.setBeneficiaryAddress(internationalBeneficiary.getBeneficiaryAddress());
		internationalBeneficiaryDTO.setIntermediaryBankAccountNumber(internationalBeneficiary.getIntermediaryBankAccountNumber());
		internationalBeneficiaryDTO.setIntermediaryBankName(internationalBeneficiary.getIntermediaryBankName());
		return  modelMapper.map(internationalBeneficiary,InternationalBeneficiaryDTO.class);
	}

	@Override
	public InternationalBeneficiary convertDTOToEntity(InternationalBeneficiaryDTO internationalBeneficiaryDTO){
		return  modelMapper.map(internationalBeneficiaryDTO,InternationalBeneficiary.class);
	}


}
