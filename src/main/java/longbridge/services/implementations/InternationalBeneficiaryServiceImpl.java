package longbridge.services.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import longbridge.models.AdminUser;
import longbridge.models.Beneficiary;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.services.InternationalBeneficiaryService;

@Service
public class InternationalBeneficiaryServiceImpl implements InternationalBeneficiaryService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private LocalBeneficiaryRepo localBeneficiaryRepo;
	private InternationalBeneficiaryRepo internationalBeneficiaryRepo;



	@Override
	public boolean addInternationalBeneficiary(RetailUser user, InternationalBeneficiary beneficiary) {
		boolean result = false;

		try {

			beneficiary.setUser(user);
			this.internationalBeneficiaryRepo.save(beneficiary);
			logger.info("International beneficiary {} has been added for user {}", beneficiary.getName(),
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
			logger.info("Deleted beneficiary {}", beneficiary.getName());
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


}
