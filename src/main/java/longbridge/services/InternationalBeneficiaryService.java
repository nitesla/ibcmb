package longbridge.services;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;

import java.util.List;

public interface InternationalBeneficiaryService {
	   /**
     * Adds a new beneficiary of a transfer
     * @param user the customer
     * @param  beneficiary  the beneficiary
     */
    String addInternationalBeneficiary(RetailUser user, InternationalBeneficiaryDTO beneficiary) throws InternetBankingException;

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    String deleteInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException;

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
    Iterable<InternationalBeneficiary> getInternationalBeneficiaries(RetailUser user);

    List<InternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<InternationalBeneficiary> internationalBeneficiaries);

    InternationalBeneficiaryDTO convertEntityToDTO(InternationalBeneficiary internationalBeneficiary);

    InternationalBeneficiary convertDTOToEntity(InternationalBeneficiaryDTO internationalBeneficiaryDTO);
}
