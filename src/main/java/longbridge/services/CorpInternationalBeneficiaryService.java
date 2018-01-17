package longbridge.services;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpInterBen;
import longbridge.models.CorporateUser;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
public interface CorpInternationalBeneficiaryService {
    /**
     * Adds a new beneficiary of a transfer
     * @param user the corporate user
     * @param  beneficiary  the beneficiary
     */
    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addCorpInternationalBeneficiary(CorporateUser user, CorpInternationalBeneficiaryDTO beneficiary) throws InternetBankingException;

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteCorpInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException;

    /**
     * Returns a beneficiary specified by the {@code id}
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInterBen getCorpInternationalBeneficiary(Long id);

    /**
     * Returns a list of the corporate's beneficiaries
     * @param user the corporate user
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<CorpInterBen> getCorpInternationalBeneficiaries(CorporateUser user);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInterBen> internationalBeneficiaries);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInterBen internationalBeneficiary);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInterBen convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO);

}
