package longbridge.services;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpInternationalBeneficiary;
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
    CorpInternationalBeneficiary getCorpInternationalBeneficiary(Long id);

    /**
     * Returns a list of the corporate's beneficiaries
     * @param user the corporate user
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<CorpInternationalBeneficiary> getCorpInternationalBeneficiaries(CorporateUser user);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInternationalBeneficiary> internationalBeneficiaries);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInternationalBeneficiary internationalBeneficiary);

    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpInternationalBeneficiary convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO);

}
