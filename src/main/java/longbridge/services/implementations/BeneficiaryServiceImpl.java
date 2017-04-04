package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.repositories.BeneficiaryRepo;
import longbridge.services.BeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Showboy on 29/03/2017.
 */
@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private BeneficiaryRepo<LocalBeneficiary, Long> localBeneficiary;
    private BeneficiaryRepo<InternationalBeneficiary, Long> internationalBeneficiary;

    @Autowired
    public BeneficiaryServiceImpl(BeneficiaryRepo<LocalBeneficiary, Long> localBeneficiary, BeneficiaryRepo<InternationalBeneficiary, Long> internationalBeneficiary) {
        this.localBeneficiary = localBeneficiary;
        this.internationalBeneficiary=internationalBeneficiary;
    }

    @Override
    public boolean addLocalBeneficiary(RetailUser user, LocalBeneficiary beneficiary) {
        boolean result= false;

        try {

            beneficiary.setUser(user);
            this.localBeneficiary.save(beneficiary);
            logger.info("BENEFICIARY {} HAS BEEN ADDED ");
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

            LocalBeneficiary beneficiary = localBeneficiary.findOne(beneficiaryId);
            beneficiary.setDelFlag("Y");
            this.localBeneficiary.save(beneficiary);
            logger.info("BENEFICIARY {} HAS BEEN DELETED ");
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());

        }
        return result;
    }

    @Override
    public Beneficiary getLocalBeneficiary(Long id) {
        return localBeneficiary.findOne(id);
    }

    @Override
    public Iterable<Beneficiary> getLocalBeneficiaries(User user) {
        return localBeneficiary.findByUserAndDelFlag(user, "N");
    }

    @Override
    public boolean addInternationalBeneficiary(RetailUser user, InternationalBeneficiary beneficiary) {
        boolean result= false;

        try {

            beneficiary.setUser(user);
            this.internationalBeneficiary.save(beneficiary);
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

            InternationalBeneficiary beneficiary = internationalBeneficiary.findOne(beneficiaryId);
            beneficiary.setDelFlag("Y");
            this.internationalBeneficiary.save(beneficiary);
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
        return internationalBeneficiary.findOne(id);
    }

    @Override
    public Iterable<Beneficiary> getInternationalBeneficiaries(User user) {
        return internationalBeneficiary.findByUserAndDelFlag(user, "N");
    }
}
