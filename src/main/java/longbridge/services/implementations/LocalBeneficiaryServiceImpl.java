package longbridge.services.implementations;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.services.LocalBeneficiaryService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class LocalBeneficiaryServiceImpl implements LocalBeneficiaryService {

    private LocalBeneficiaryRepo localBeneficiaryRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    public LocalBeneficiaryServiceImpl(LocalBeneficiaryRepo localBeneficiaryRepo) {
        this.localBeneficiaryRepo = localBeneficiaryRepo;
    }

    @Override
    public boolean addLocalBeneficiary(RetailUser user, LocalBeneficiaryDTO beneficiary) {
        boolean result= false;

        try {
            LocalBeneficiary localBeneficiary = convertDTOToEntity(beneficiary);
            localBeneficiary.setUser(user);
            this.localBeneficiaryRepo.save(localBeneficiary);
            logger.trace("Beneficiary {} has been added", localBeneficiary.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("Could not create beneficiary",e);
        }
        return result;
    }

    @Override
    public boolean deleteLocalBeneficiary(Long beneficiaryId) {
        boolean result= false;

        try {

            LocalBeneficiary beneficiary = localBeneficiaryRepo.findOne(beneficiaryId);
            beneficiary.setDelFlag("Y");
            this.localBeneficiaryRepo.save(beneficiary);
            logger.info("Beneficiary {} has been deleted", beneficiary.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("Could not delete beneficiary",e.getMessage());

        }
        return result;
    }

    @Override
    public LocalBeneficiary getLocalBeneficiary(Long id) {
        return localBeneficiaryRepo.findOne(id);
    }

    @Override
    public Iterable<LocalBeneficiary> getLocalBeneficiaries(RetailUser user) {
        return localBeneficiaryRepo.findByUser(user);
    }

    @Override
    public List<LocalBeneficiaryDTO> convertEntitiesToDTOs(Iterable<LocalBeneficiary> localBeneficiaries){
        List<LocalBeneficiaryDTO> localBeneficiaryDTOList = new ArrayList<>();
        for(LocalBeneficiary localBeneficiary: localBeneficiaries){
            LocalBeneficiaryDTO benDTO = convertEntityToDTO(localBeneficiary);
            localBeneficiaryDTOList.add(benDTO);
        }
        return localBeneficiaryDTOList;
    }

    @Override
    public LocalBeneficiaryDTO convertEntityToDTO(LocalBeneficiary localBeneficiary){
        LocalBeneficiaryDTO localBeneficiaryDTO = new LocalBeneficiaryDTO();
        localBeneficiaryDTO.setAccountName(localBeneficiary.getAccountName());
        localBeneficiaryDTO.setAccountNumber(localBeneficiary.getAccountNumber());
        localBeneficiaryDTO.setBeneficiaryBank(localBeneficiary.getBeneficiaryBank());
        localBeneficiaryDTO.setPreferredName(localBeneficiary.getPreferredName());
        return  modelMapper.map(localBeneficiary,LocalBeneficiaryDTO.class);
    }

    @Override
    public LocalBeneficiary convertDTOToEntity(LocalBeneficiaryDTO localBeneficiaryDTO){
        return  modelMapper.map(localBeneficiaryDTO,LocalBeneficiary.class);
    }
	
	
}
