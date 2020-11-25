package longbridge.services;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.models.Beneficiary;
import longbridge.models.QuickBeneficiary;
import longbridge.models.User;

/**
 * The {@code BeneficiaryService} interface provides the methods to manage the customer's beneficiaries
 * @author Fortunatus Ekenachi
 * @see Beneficiary
 * @see User
 *
 */
public interface QuickBeneficiaryService {

    Iterable<QuickBeneficiary> getQuickBeneficiaries();

    String addQuickBeneficiary(LocalBeneficiaryDTO beneficiary);

    QuickBeneficiary convertDTOToEntity(LocalBeneficiaryDTO localBeneficiaryDTO);

    QuickBeneficiary getQuickBeneficiary(Long id);

    LocalBeneficiaryDTO convertEntityToDTO(QuickBeneficiary quickBeneficiary);
}