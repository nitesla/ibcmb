package longbridge.services;

import longbridge.models.Beneficiary;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;

public interface InternationalBeneficiaryService {
	   /**
     * Adds a new beneficiary of a transfer
     * @param user the customer
     * @param  beneficiary  the beneficiary
     */
    boolean addInternationalBeneficiary(RetailUser user, InternationalBeneficiary beneficiary);

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    boolean deleteInternationalBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary specified by the {@code id}
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    InternationalBeneficiary getInternationalBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    Iterable<InternationalBeneficiary> getInternationalBeneficiaries(User user);

}
