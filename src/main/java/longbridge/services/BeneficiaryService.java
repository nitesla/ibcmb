package longbridge.services;

import longbridge.models.Beneficiary;
import longbridge.models.Customer;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface BeneficiaryService {

    void  addBeneficiary();

    void deleteBeneficiary();

    Beneficiary getBeneficiary(Long id);

    Beneficiary getBeneficiaries(Customer customer);

    void updateBeneficiary(Long id);
}
