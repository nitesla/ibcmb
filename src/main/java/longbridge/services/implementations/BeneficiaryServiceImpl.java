package longbridge.services.implementations;

import longbridge.models.Beneficiary;
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
    public void addBeneficiary(User user, Beneficiary beneficiary) {

    }

    @Override
    public void deleteBeneficiary(User user, Long beneficiaryId) {

    }

    @Override
    public Beneficiary getBeneficiary(Long id) {
        return null;
    }

    @Override
    public Iterable<Beneficiary> getBeneficiaries(User user) {
        return null;
    }
}
