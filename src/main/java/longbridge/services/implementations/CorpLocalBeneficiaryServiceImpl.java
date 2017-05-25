package longbridge.services.implementations;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;
import longbridge.repositories.CorpLocalBeneficiaryRepo;
import longbridge.services.CorpLocalBeneficiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Service
public class CorpLocalBeneficiaryServiceImpl implements CorpLocalBeneficiaryService {
    private CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String addCorpLocalBeneficiary(CorporateUser user, CorpLocalBeneficiaryDTO beneficiary) {
        CorpLocalBeneficiary corpLocalBeneficiary=convertDTOToEntity(beneficiary);
        corpLocalBeneficiary.setUser(user);
        this.corpLocalBeneficiaryRepo.save(corpLocalBeneficiary);
        logger.trace("Beneficiary {} has been added", corpLocalBeneficiary.toString());

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
