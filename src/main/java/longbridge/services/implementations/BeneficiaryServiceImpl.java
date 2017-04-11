package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.repositories.InternationalBeneficiaryRepo;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.services.BeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private LocalBeneficiaryRepo localBeneficiaryRepo;
    private InternationalBeneficiaryRepo internationalBeneficiaryRepo;

    @Autowired
    public BeneficiaryServiceImpl(LocalBeneficiaryRepo localBeneficiaryRepo, InternationalBeneficiaryRepo internationalBeneficiaryRepo) {
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
    public Beneficiary getLocalBeneficiary(Long id) {
        return localBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<Beneficiary> getLocalBeneficiaries(User user) {
        //return localBeneficiaryRepo.findByUserAndDelFlag(user, "N");
        return null;
    }

    @Override
    public boolean addInternationalBeneficiary(RetailUser user, InternationalBeneficiary beneficiary) {
        boolean result= false;

        try {

            beneficiary.setUser(user);
            this.internationalBeneficiaryRepo.save(beneficiary);
            logger.info("BENEFICIARY {} HAS BEEN ADDED ");
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());

        }
        return result;
    }

    @Override
    public boolean deleteInternationalBeneficiary(Long beneficiaryId) {
        boolean result= false;

        try {

            InternationalBeneficiary beneficiary = internationalBeneficiaryRepo.findOne(beneficiaryId);
            beneficiary.setDelFlag("Y");
            this.internationalBeneficiaryRepo.save(beneficiary);
            logger.info("BENEFICIARY {} HAS BEEN DELETED ");
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());

        }
        return result;
    }

    @Override
    public Beneficiary getInternationalBeneficiary(Long id) {
        return internationalBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<Beneficiary> getInternationalBeneficiaries(User user) {
        //return internationalBeneficiaryRepo.findByUserAndDelFlag(user, "N");
        return null;
    }
}
