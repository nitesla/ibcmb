package longbridge.services;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CorpInternationalBeneficiary;
import longbridge.models.CorporateUser;

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
    String addCorpInternationalBeneficiary(CorporateUser user, CorpInternationalBeneficiaryDTO beneficiary) throws InternetBankingException;

    /**
     * Deletes a beneficiary
     * @param beneficiaryId the beneficiary's id
     */
    String deleteCorpInternationalBeneficiary(Long beneficiaryId) throws InternetBankingException;

    /**
     * Returns a beneficiary specified by the {@code id}
     * @param id the beneficiary's id
     * @return the specified beneficiary
     */
    CorpInternationalBeneficiary getCorpInternationalBeneficiary(Long id);

    /**
     * Returns a list of the corporate's beneficiaries
     * @param user the corporate user
     * @return a list of the beneficiaries
     */
    Iterable<CorpInternationalBeneficiary> getCorpInternationalBeneficiaries(CorporateUser user);

    List<CorpInternationalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpInternationalBeneficiary> internationalBeneficiaries);

    CorpInternationalBeneficiaryDTO convertEntityToDTO(CorpInternationalBeneficiary internationalBeneficiary);

    CorpInternationalBeneficiary convertDTOToEntity(CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO);

}
