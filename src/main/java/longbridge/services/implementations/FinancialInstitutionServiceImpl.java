package longbridge.services.implementations;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import longbridge.repositories.FinancialInstitutionRepo;
import longbridge.services.FinancialInstitutionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
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

    @Override
    public List<FinancialInstitutionDTO> convertEntitiesToDTOs(Iterable<FinancialInstitution> financialInstitutions){
        List<FinancialInstitutionDTO> financialInstitutionDTOList = new ArrayList<>();
        for(FinancialInstitution financialInstitution: financialInstitutions){
            FinancialInstitutionDTO fiDTO = convertEntityToDTO(financialInstitution);
            financialInstitutionDTOList.add(fiDTO);
        }
        return financialInstitutionDTOList;
    }

    @Override
    public FinancialInstitutionDTO convertEntityToDTO(FinancialInstitution financialInstitution){
        FinancialInstitutionDTO financialInstitutionDTO = new FinancialInstitutionDTO();
        financialInstitutionDTO.setInstitutionCode(financialInstitution.getInstitutionCode());
        financialInstitutionDTO.setInstitutionType(financialInstitution.getInstitutionType());
        financialInstitutionDTO.setInstitutionName(financialInstitution.getInstitutionName());
        return  modelMapper.map(financialInstitution,FinancialInstitutionDTO.class);
    }

    @Override
    public FinancialInstitution convertDTOToEntity(FinancialInstitutionDTO financialInstitutionDTO){
        return  modelMapper.map(financialInstitutionDTO,FinancialInstitution.class);
    }

    @Override
    public boolean addFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) {
        boolean ok = false;
        if (financialInstitutionDTO != null) {
            FinancialInstitution financialInstitution = new FinancialInstitution();
            financialInstitution.setInstitutionCode(financialInstitutionDTO.getInstitutionCode());
            financialInstitution.setInstitutionType(financialInstitutionDTO.getInstitutionType());
            financialInstitution.setInstitutionName(financialInstitutionDTO.getInstitutionName());
            this.financialInstitutionRepo.save(financialInstitution
            );
            logger.info("New financial institution: {} created", financialInstitution.getInstitutionName());
            ok=true;
        } else {
            logger.error("FINANCIAL INSTITUTION NOT FOUND");
        }
        return ok;
    }

    @Override
    public boolean updateFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) {
            boolean ok = false;
            if (financialInstitutionDTO != null) {
                FinancialInstitution financialInstitution = new FinancialInstitution();
                financialInstitution.setId((financialInstitutionDTO.getId()));
                financialInstitution.setVersion(financialInstitutionDTO.getVersion());
                financialInstitution.setInstitutionCode(financialInstitutionDTO.getInstitutionCode());
                financialInstitution.setInstitutionType(financialInstitutionDTO.getInstitutionType());
                financialInstitution.setInstitutionName(financialInstitutionDTO.getInstitutionName());
                this.financialInstitutionRepo.save(financialInstitution);
                logger.info("Financial Institution {} updated", financialInstitution.getInstitutionName());
                ok=true;
            } else {
                logger.error("Null Fi provided");
            }
            return ok;
    }

    @Override
    public List<FinancialInstitutionDTO> getFinancialInstitutions() {
        Iterable<FinancialInstitution> fis =financialInstitutionRepo.findAll();
        logger.info("FinancialInstitutions {}",fis.toString());
        return convertEntitiesToDTOs(fis);
    }

    @Override
    public List<FinancialInstitutionDTO> getFinancialInstitutionsByType(FinancialInstitutionType institutionType) {
        Iterable<FinancialInstitution> fis =financialInstitutionRepo.findByInstitutionType(institutionType);
        logger.info("FinancialInstitutions {}",fis.toString());
        return convertEntitiesToDTOs(fis);
    }

    @Override
    public FinancialInstitutionDTO getFinancialInstitution(Long id) {
        return convertEntityToDTO(financialInstitutionRepo.findOne(id));
    }

    @Override
    public boolean deleteFi(Long id) {
        boolean result= false;

        try {
            FinancialInstitution financialInstitution = financialInstitutionRepo.findOne(id);
            financialInstitution.setDelFlag("Y");
            this.financialInstitutionRepo.save(financialInstitution);
            logger.info("Fi {} HAS BEEN DELETED ",id.toString());
            result=true;
        }
        catch (Exception e){
            logger.error("ERROR OCCURRED {}",e.getMessage());

        }
        return result;
    }

    @Override
    public Page<FinancialInstitutionDTO> getFinancialInstitutions(Pageable pageDetails) {
        Page<FinancialInstitution> page = financialInstitutionRepo.findAll(pageDetails);
        List<FinancialInstitutionDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<FinancialInstitutionDTO> pageImpl = new PageImpl<FinancialInstitutionDTO>(dtOs,pageDetails,t);
        return pageImpl;
    }

}
