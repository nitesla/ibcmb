package longbridge.services;

import longbridge.models.Beneficiary;
import longbridge.models.Customer;

/**
 * The {@code BeneficiaryService} interface provides the methods to manage the customer's beneficiaries
<<<<<<< HEAD
 * @author Fortunatus Ekenachi
=======
 *
>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae
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
<<<<<<< HEAD
     * Dele
     */
    void deleteBeneficiary();
=======
     * Deletes a beneficiary
     * @param customer the customer
     * @param beneficiaryId the beneficiary's id
     */
    void deleteBeneficiary(Customer customer, Long beneficiaryId);
>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    Beneficiary getBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param customer the customer
     * @return a list of the beneficiaries
     */
    Iterable<Beneficiary> getBeneficiaries(Customer customer);


}
