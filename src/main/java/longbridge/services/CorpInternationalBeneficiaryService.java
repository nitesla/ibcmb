package longbridge.services;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.models.CorpInterBeneficiary;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public interface CorpInternationalBeneficiaryService {
    /**
     * Adds a new beneficiary of a transfer
     * @param  beneficiary  the beneficiary
     */
    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addCorpInternationalBeneficiary(CorpInternationalBeneficiaryDTO beneficiary) ;

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteCorpInternationalBeneficiary(Long beneficiaryId) ;

    /**
     * Returns a beneficiary specified by the {@code id}
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInternationalBeneficiaryDTO getCorpInternationalBeneficiary(Long id);

    /**
     * Returns a list of the corporate's beneficiaries
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<CorpInterBeneficiary> getCorpInternationalBeneficiaries();

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInterBeneficiary> internationalBeneficiaries);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInterBeneficiary internationalBeneficiary);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInterBeneficiary convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO);

}
