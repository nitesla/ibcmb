package longbridge.services;

import longbridge.models.Beneficiary;
import longbridge.models.Customer;

/**
 * The {@code BeneficiaryService} interface provides the methods to manage the customer's beneficiaries
 * @author Fortunatus Ekenachi
 *
 */
public interface BeneficiaryService {

    /**
     * Adds a new beneficiary of a transfer
     * @param customer the customer
     * @param  beneficiary  the beneficiary
     */
    void  addBeneficiary(Customer customer, Beneficiary beneficiary);

    /**
     * Dele
     */
    void deleteBeneficiary();

    Beneficiary getBeneficiary(Long id);

    Beneficiary getBeneficiaries(Customer customer);

    void updateBeneficiary(Long id);
}
