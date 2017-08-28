package longbridge.services;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.models.Beneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.models.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addLocalBeneficiary(RetailUser user, LocalBeneficiaryDTO beneficiary);

    /**
     * Deletes a beneficiary that has been created by the user
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteLocalBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    LocalBeneficiary getLocalBeneficiary(Long id);


    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<LocalBeneficiary> getLocalBeneficiaries(RetailUser user);
    Iterable<LocalBeneficiary> getBankBeneficiaries(RetailUser user);

    boolean doesBeneficiaryExist(RetailUser user, LocalBeneficiaryDTO beneficiaryDTO);


    List<LocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<LocalBeneficiary> localBeneficiaries);

    LocalBeneficiaryDTO convertEntityToDTO(LocalBeneficiary localBeneficiary);

    LocalBeneficiary convertDTOToEntity(LocalBeneficiaryDTO localBeneficiaryDTO);
}