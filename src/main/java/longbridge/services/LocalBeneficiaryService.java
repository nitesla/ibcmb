package longbridge.services;

import longbridge.models.*;

/**
 * The {@code BeneficiaryService} interface provides the methods to manage the customer's beneficiaries
 * @author Fortunatus Ekenachi
 * @see Beneficiary
 * @see User

 *
 */
public interface LocalBeneficiaryService {

    /**
     * Adds a new beneficiary of a transfer
     * @param user the customer
     * @param  beneficiary  the beneficiary
     */
    boolean addLocalBeneficiary(RetailUser user, LocalBeneficiary beneficiary);

    /**
     * Deletes a beneficiary that has been created by the user
     * @param beneficiaryId the beneficiary's id
     */
    boolean deleteLocalBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    LocalBeneficiary getLocalBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    Iterable<LocalBeneficiary> getLocalBeneficiaries(User user);

 

}