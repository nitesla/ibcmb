package longbridge.services;

import longbridge.dtos.CorpNeftBeneficiaryDTO;
import longbridge.models.CorpNeftBeneficiary;

import java.util.List;


public interface CorpNeftBeneficiaryService {


    String addNeftBeneficiary(CorpNeftBeneficiaryDTO beneficiary);

    String deleteNeftBeneficiary(Long beneficiaryId);

    CorpNeftBeneficiary getNeftBeneficiary(Long id);
    Iterable<CorpNeftBeneficiary> getCorpNeftBeneficiaries();



    List<CorpNeftBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpNeftBeneficiary> localBeneficiaries);

    CorpNeftBeneficiaryDTO convertEntityToDTO(CorpNeftBeneficiary localBeneficiary);

    CorpNeftBeneficiary convertDTOToEntity(CorpNeftBeneficiaryDTO localBeneficiaryDTO);
}
