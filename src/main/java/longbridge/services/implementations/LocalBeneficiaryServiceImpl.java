package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.services.LocalBeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class LocalBeneficiaryServiceImpl implements LocalBeneficiaryService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private LocalBeneficiaryRepo localBeneficiaryRepo;
    private InternationalBeneficiaryRepo internationalBeneficiaryRepo;

    @Autowired
    public LocalBeneficiaryServiceImpl(LocalBeneficiaryRepo localBeneficiaryRepo, InternationalBeneficiaryRepo internationalBeneficiaryRepo) {
        this.localBeneficiaryRepo = localBeneficiaryRepo;
        this.internationalBeneficiaryRepo = internationalBeneficiaryRepo;
    }

    @Override
    public boolean addLocalBeneficiary(RetailUser user, LocalBeneficiary beneficiary) {
        boolean result= false;

        try {
            beneficiary.setUser(user);
            this.localBeneficiaryRepo.save(beneficiary);
            logger.trace("Beneficiary {} has been added", this.localBeneficiaryRepo.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("Could not create beneficiary",e);
        }
        return result;
    }

    @Override
    public boolean deleteLocalBeneficiary(Long beneficiaryId) {
        boolean result= false;

        try {

            LocalBeneficiary beneficiary = localBeneficiaryRepo.findOne(beneficiaryId);
            beneficiary.setDelFlag("Y");
            this.localBeneficiaryRepo.save(beneficiary);
            logger.info("Beneficiary {} has been deleted", beneficiary.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("Could not delete beneficiary",e.getMessage());

        }
        return result;
    }

    @Override
    public LocalBeneficiary getLocalBeneficiary(Long id) {
        return localBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<LocalBeneficiary> getLocalBeneficiaries(User user) {
        //return localBeneficiaryRepo.findByUserAndDelFlag(user, "N");
        return null;
    }

    
	
	
}
