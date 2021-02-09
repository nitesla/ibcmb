package longbridge.services;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.RetailUser;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface InternationalBeneficiaryService {
	   /**
     * Adds a new beneficiary of a transfer
     * @param  beneficiary  the beneficiary
     */

	   @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
       String addInternationalBeneficiary(InternationalBeneficiaryDTO beneficiary) ;

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteInternationalBeneficiary(Long beneficiaryId) ;

    /**
     * Returns a beneficiary specified by the {@code id}
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    InternationalBeneficiaryDTO getInternationalBeneficiary(Long id);

    /**
     * Returns a list of the customer's beneficiaries
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<InternationalBeneficiary> getInternationalBeneficiaries();

    boolean doesBeneficiaryExist(RetailUser user, InternationalBeneficiaryDTO internationalBeneficiaryDTO);


    List<InternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<InternationalBeneficiary> internationalBeneficiaries);

    InternationalBeneficiaryDTO convertEntityToDTO(InternationalBeneficiary internationalBeneficiary);

    InternationalBeneficiary convertDTOToEntity(InternationalBeneficiaryDTO internationalBeneficiaryDTO);

}
