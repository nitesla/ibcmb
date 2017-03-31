package longbridge.services.implementations;

import longbridge.models.Beneficiary;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.User;
import longbridge.services.BeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Showboy on 29/03/2017.
 */
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean addLocalBeneficiary(User user, LocalBeneficiary beneficiary) {
        return false;
    }

    @Override
    public boolean deleteLocalBeneficiary(Long beneficiaryId) {
        return false;
    }

    @Override
    public Beneficiary getLocalBeneficiary(Long id) {
        return null;
    }

    @Override
    public Iterable<Beneficiary> getLocalBeneficiaries(User user) {
        return null;
    }

    @Override
    public boolean addInternationalBeneficiary(User user, InternationalBeneficiary beneficiary) {
        return false;
    }

    @Override
    public boolean deleteInternationalBeneficiary(Long beneficiaryId) {
        return false;
    }

    @Override
    public Beneficiary getInternationalBeneficiary(Long id) {
        return null;
    }

    @Override
    public Iterable<Beneficiary> getInternationalBeneficiaries(User user) {
        return null;
    }
}
