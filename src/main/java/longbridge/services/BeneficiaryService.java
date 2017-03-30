package longbridge.services;

import longbridge.models.Beneficiary;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.LocalBeneficiary;
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
    boolean addLocalBeneficiary(User user, LocalBeneficiary beneficiary);

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    boolean deleteLocalBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    Beneficiary getLocalBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    Iterable<Beneficiary> getLocalBeneficiaries(User user);

    /**
     * Adds a new beneficiary of a transfer
     * @param user the customer
     * @param  beneficiary  the beneficiary
     */
    boolean addInternationalBeneficiary(User user, InternationalBeneficiary beneficiary);

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    boolean deleteInternationalBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    Beneficiary getInternationalBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    Iterable<Beneficiary> getInternationalBeneficiaries(User user);


}