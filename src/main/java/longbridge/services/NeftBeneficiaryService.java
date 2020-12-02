package longbridge.services;

import longbridge.dtos.NeftBeneficiaryDTO;
import longbridge.models.NeftBeneficiary;
import longbridge.models.RetailUser;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface NeftBeneficiaryService {

    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addNeftBeneficiary(NeftBeneficiaryDTO beneficiary);

    @PreAuthorize("hasAuthority('ADD_BENEFICIARY')")
    String addNeftBeneficiaryMobileApi(NeftBeneficiaryDTO beneficiary);

    /**
     * Deletes a beneficiary that has been created by the user
     * @param beneficiaryId the beneficiary's id
     */
    @PreAuthorize("hasAuthority('DELETE_BENEFICIARY')")
    String deleteNeftBeneficiary(Long beneficiaryId);

    /**
     * Returns a beneficiary
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    NeftBeneficiary getNeftBeneficiary(Long id);


    /**
     * Returns a list of the customer's beneficiaries
     * @param user the customer
     * @return a list of the beneficiaries
     */
    @PreAuthorize("hasAuthority('GET_BENEFICIARIES')")
    Iterable<NeftBeneficiary> getNeftBeneficiaries();
    //    List<LocalBeneficiary> getLocalBeneficiaries();
    Iterable<NeftBeneficiary> getBankBeneficiaries();

    boolean doesBeneficiaryExist(RetailUser user, NeftBeneficiaryDTO beneficiaryDTO);


    List<NeftBeneficiaryDTO> convertEntitiesToDTOs(Iterable<NeftBeneficiary> localBeneficiaries);

    NeftBeneficiaryDTO convertEntityToDTO(NeftBeneficiary localBeneficiary);

    NeftBeneficiary convertDTOToEntity(NeftBeneficiaryDTO localBeneficiaryDTO);
}
