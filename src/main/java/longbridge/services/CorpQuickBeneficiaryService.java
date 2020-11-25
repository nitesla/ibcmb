package longbridge.services;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.models.CorpQuickBeneficiary;

public interface CorpQuickBeneficiaryService {

    String addCorpQuickBeneficiary( CorpLocalBeneficiaryDTO beneficiary);

    CorpQuickBeneficiary getCorpQuickBeneficiary(Long id);

    Iterable<CorpQuickBeneficiary> getCorpQuickBeneficiaries();

//    List<CorpLocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries);

    CorpLocalBeneficiaryDTO convertEntityToDTO(CorpQuickBeneficiary corpQuickBeneficiary);

    CorpQuickBeneficiary convertDTOToEntity(CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO);



}
