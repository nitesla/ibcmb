package longbridge.services;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpNeftBeneficiaryDTO;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorpNeftBeneficiary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

public interface  CorpNeftBeneficiaryService {

    /**
     * Adds a new beneficiary of a transfer
     * @param corporate the corporate
     * @param  beneficiary  the beneficiary
     */
    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addCorpNeftBeneficiary( CorpNeftBeneficiaryDTO beneficiary);

    /**
     * Deletes a beneficiary that has been created by the user
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteCorpNeftBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    CorpNeftBeneficiary getCorpNeftBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @param corporate the corporate
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<CorpNeftBeneficiary> getCorpNeftBeneficiaries();

    List<CorpNeftBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpNeftBeneficiary> corpNeftBeneficiaries);

    CorpNeftBeneficiaryDTO convertEntityToDTO(CorpNeftBeneficiary corpNeftBeneficiary);

    CorpNeftBeneficiary convertDTOToEntity(CorpNeftBeneficiaryDTO corpNeftBeneficiaryDTO);



}
