package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.repositories.BeneficiaryRepo;
import longbridge.services.BeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Showboy on 29/03/2017.
 */
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private BeneficiaryRepo<LocalBeneficiary, Long> localBeneficiaryRepo;

    private BeneficiaryRepo<InternationalBeneficiary, Long> internationalBeneficiaryRepo;

    @Autowired
    public BeneficiaryServiceImpl(BeneficiaryRepo<LocalBeneficiary, Long> localBeneficiaryRepo, BeneficiaryRepo<InternationalBeneficiary, Long> internationalBeneficiaryRepo) {
        this.localBeneficiaryRepo = localBeneficiaryRepo;
        this.internationalBeneficiaryRepo = internationalBeneficiaryRepo;
    }

    @Override
    public boolean addLocalBeneficiary(User user, LocalBeneficiary beneficiary) {
        boolean result= false;

        try {
            this.localBeneficiaryRepo.save(beneficiary);
            logger.info("Beneficiary {} HAS BEEN ADDED ",beneficiary.getId());
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
        return result;
    }

    @Override
    public boolean deleteLocalBeneficiary(Long beneficiaryId) {
        boolean result= false;
        try {
            LocalBeneficiary delBeneficiary = this.localBeneficiaryRepo.findOne(beneficiaryId);
            delBeneficiary.setDelFlag("Y");
            this.localBeneficiaryRepo.save(delBeneficiary);
            logger.info("Code {} HAS BEEN DELETED ");
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
        return result;
    }

    @Override
    public Beneficiary getLocalBeneficiary(Long id) {
        return this.localBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<Beneficiary> getLocalBeneficiaries(User user) {
        return this.localBeneficiaryRepo.findByUser(user);
    }

    @Override
    public boolean addInternationalBeneficiary(User user, InternationalBeneficiary beneficiary) {
        boolean result= false;

        try {
            this.internationalBeneficiaryRepo.save(beneficiary);
            logger.info("Beneficiary {} HAS BEEN ADDED ",beneficiary.getId());
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
            InternationalBeneficiary delBeneficiary = this.internationalBeneficiaryRepo.findOne(beneficiaryId);
            delBeneficiary.setDelFlag("Y");
            this.internationalBeneficiaryRepo.save(delBeneficiary);
            logger.info("Code {} HAS BEEN DELETED ");
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());
        }
        return result;
    }

    @Override
    public Beneficiary getInternationalBeneficiary(Long id) {
        return this.internationalBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<Beneficiary> getInternationalBeneficiaries(User user) {
        return this.internationalBeneficiaryRepo.findByUser(user);
    }
}
