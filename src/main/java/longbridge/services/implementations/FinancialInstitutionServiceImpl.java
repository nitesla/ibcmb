package longbridge.services.implementations;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.models.FinancialInstitution;
import longbridge.models.RetailUser;
import longbridge.repositories.FinancialInstitutionRepo;
import longbridge.services.FinancialInstitutionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Showboy on 24/04/2017.
 */
@Service
public class FinancialInstitutionServiceImpl implements FinancialInstitutionService {

    private FinancialInstitutionRepo financialInstitutionRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    public FinancialInstitutionServiceImpl(FinancialInstitutionRepo financialInstitutionRepo) {
        this.financialInstitutionRepo = financialInstitutionRepo;
    }

    private List<FinancialInstitutionDTO> convertEntitiesToDTOs(Iterable<FinancialInstitution> financialInstitutions){
        List<FinancialInstitutionDTO> financialInstitutionDTOList = new ArrayList<>();
        for(FinancialInstitution financialInstitution: financialInstitutions){
            FinancialInstitutionDTO fiDTO = convertEntityToDTO(financialInstitution);
            financialInstitutionDTOList.add(fiDTO);
        }
        return financialInstitutionDTOList;
    }

    private FinancialInstitutionDTO convertEntityToDTO(FinancialInstitution financialInstitution){
        FinancialInstitutionDTO financialInstitutionDTO = new FinancialInstitutionDTO();
        financialInstitutionDTO.setInstitutionCode(financialInstitution.getInstitutionCode());
        financialInstitutionDTO.setInstitutionName(financialInstitution.getInstitutionName());
        return  modelMapper.map(financialInstitution,FinancialInstitutionDTO.class);
    }

    private FinancialInstitution convertDTOToEntity(FinancialInstitutionDTO financialInstitutionDTO){
        return  modelMapper.map(financialInstitutionDTO,FinancialInstitution.class);
    }

    @Override
    public Iterable<FinancialInstitutionDTO> getFinancialInstitutions() {
        Iterable<FinancialInstitution> fis =financialInstitutionRepo.findAll();
        logger.info("FinancialInstitutions {}",fis.toString());
        return convertEntitiesToDTOs(fis);
    }

}
