package longbridge.services;

import longbridge.models.Beneficiary;
import longbridge.models.User;

/**
 * The {@code BeneficiaryService} interface provides the methods to manage the customer's beneficiaries
 * @author Fortunatus Ekenachi

 *
 */
public interface BeneficiaryService {

    /**
     * Adds a new beneficiary of a transfer
     * @param user the customer
     * @param  beneficiary  the beneficiary
     */
    void  addBeneficiary(User user, Beneficiary beneficiary);

    /**
     * Deletes a beneficiary
     * @param user the customer
     * @param beneficiaryId the beneficiary's id
     */

    void deleteBeneficiary(User user, Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    Beneficiary getBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    Iterable<Beneficiary> getBeneficiaries(User user);


}
