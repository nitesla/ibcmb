package longbridge.services.implementations;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;
import longbridge.services.CorpLocalBeneficiaryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Service
public class CorpLocalBeneficiaryServiceImpl implements CorpLocalBeneficiaryService {

    @Override
    public String addCorpLocalBeneficiary(CorporateUser user, CorpLocalBeneficiaryDTO beneficiary) {
        return null;
    }

    @Override
    public String deleteCorpLocalBeneficiary(Long beneficiaryId) {
        return null;
    }

    @Override
    public CorpLocalBeneficiary getCorpLocalBeneficiary(Long id) {
        return null;
    }

    @Override
    public Iterable<CorpLocalBeneficiary> getCorpLocalBeneficiaries(CorporateUser user) {
        return null;
    }

    @Override
    public List<CorpLocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries) {
        return null;
    }

    @Override
    public CorpLocalBeneficiaryDTO convertEntityToDTO(CorpLocalBeneficiary corpLocalBeneficiary) {
        return null;
    }

    @Override
    public CorpLocalBeneficiary convertDTOToEntity(CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO) {
        return null;
    }
}
