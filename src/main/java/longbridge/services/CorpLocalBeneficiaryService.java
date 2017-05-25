package longbridge.services;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * The{@code beneficiaryService} interface provides the methods to manage the corporate user beneficiaries
 * Created by SYLVESTER on 5/19/2017.
 */


public interface CorpLocalBeneficiaryService {
    /**
     * Adds a new beneficiary of a transfer
     * @param user the corporate user
     * @param  beneficiary  the beneficiary
     */
    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addCorpLocalBeneficiary(CorporateUser user, CorpLocalBeneficiaryDTO beneficiary);

    /**
     * Deletes a beneficiary that has been created by the user
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteCorpLocalBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpLocalBeneficiary getCorpLocalBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param user the corporate user
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<CorpLocalBeneficiary> getCorpLocalBeneficiaries(CorporateUser user);

    List<CorpLocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries);

    CorpLocalBeneficiaryDTO convertEntityToDTO(CorpLocalBeneficiary corpLocalBeneficiary);

    CorpLocalBeneficiary convertDTOToEntity(CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO);


}
