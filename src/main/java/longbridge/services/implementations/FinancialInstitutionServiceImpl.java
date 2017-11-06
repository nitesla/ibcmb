package longbridge.services.implementations;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import longbridge.repositories.FinancialInstitutionRepo;
import longbridge.services.FinancialInstitutionService;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
@Service
public class FinancialInstitutionServiceImpl implements FinancialInstitutionService {

    private FinancialInstitutionRepo financialInstitutionRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public FinancialInstitutionServiceImpl(FinancialInstitutionRepo financialInstitutionRepo) {
        this.financialInstitutionRepo = financialInstitutionRepo;
    }

    @Override
    public List<FinancialInstitutionDTO> convertEntitiesToDTOs(Iterable<FinancialInstitution> financialInstitutions) {
        List<FinancialInstitutionDTO> financialInstitutionDTOList = new ArrayList<>();
        for (FinancialInstitution financialInstitution : financialInstitutions) {
            FinancialInstitutionDTO fiDTO = convertEntityToDTO(financialInstitution);
            financialInstitutionDTOList.add(fiDTO);
        }
        return financialInstitutionDTOList;
    }

    @Override
    public FinancialInstitutionDTO convertEntityToDTO(FinancialInstitution financialInstitution) {
        FinancialInstitutionDTO financialInstitutionDTO = new FinancialInstitutionDTO();
        financialInstitutionDTO.setId(financialInstitution.getId());
        financialInstitutionDTO.setVersion(financialInstitution.getVersion());
        financialInstitutionDTO.setInstitutionCode(financialInstitution.getInstitutionCode());
        financialInstitutionDTO.setInstitutionType(financialInstitution.getInstitutionType());
        financialInstitutionDTO.setInstitutionName(financialInstitution.getInstitutionName());
        financialInstitutionDTO.setSortCode(financialInstitution.getSortCode());
        return financialInstitutionDTO;
    }

    @Override
    public FinancialInstitution convertDTOToEntity(FinancialInstitutionDTO financialInstitutionDTO) {
        return modelMapper.map(financialInstitutionDTO, FinancialInstitution.class);
    }

    @Override
    @Verifiable(operation="ADD_FIN_INST",description="Adding a Financial Institution")
    public String addFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) throws InternetBankingException {

        FinancialInstitution financialInstitution;
        financialInstitution = financialInstitutionRepo.findByInstitutionCodeAndInstitutionType(financialInstitutionDTO.getInstitutionCode(), financialInstitutionDTO.getInstitutionType());
        if (financialInstitution != null) {
            throw new DuplicateObjectException(messageSource.getMessage("institution.exists", null, locale));
        }
        try {
            financialInstitution = new FinancialInstitution();
            financialInstitution.setInstitutionCode(financialInstitutionDTO.getInstitutionCode());
            financialInstitution.setInstitutionType(financialInstitutionDTO.getInstitutionType());
            financialInstitution.setInstitutionName(financialInstitutionDTO.getInstitutionName());
            financialInstitution.setSortCode(financialInstitutionDTO.getSortCode());
            this.financialInstitutionRepo.save(financialInstitution);
            logger.info("New financial institution: {} created", financialInstitution.getInstitutionName());
            return messageSource.getMessage("institution.add.success", null, locale);
        }

        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }

        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("institution.add.failure", null, locale),e);
        }
    }

    @Override
    @Verifiable(operation="UPDATE_FIN_INST",description="Updating a Financial Institution")
    public String updateFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) throws InternetBankingException {
        try {
            FinancialInstitution financialInstitution = new FinancialInstitution();
            financialInstitution.setId((financialInstitutionDTO.getId()));
            financialInstitution.setVersion(financialInstitutionDTO.getVersion());
            financialInstitution.setInstitutionCode(financialInstitutionDTO.getInstitutionCode());
            financialInstitution.setInstitutionType(financialInstitutionDTO.getInstitutionType());
            financialInstitution.setInstitutionName(financialInstitutionDTO.getInstitutionName());
            financialInstitution.setSortCode(financialInstitutionDTO.getSortCode());
            this.financialInstitutionRepo.save(financialInstitution);
            logger.info("Financial Institution {} updated", financialInstitution.getInstitutionName());
            return messageSource.getMessage("institution.update.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("institution.update.failure", null, locale));
        }

    }

    @Override
    public List<FinancialInstitutionDTO> getFinancialInstitutions() {
        Iterable<FinancialInstitution> fis = financialInstitutionRepo.findAll();
        return convertEntitiesToDTOs(fis);
    }

    @Override
    public List<FinancialInstitutionDTO> getFinancialInstitutionsByType(FinancialInstitutionType institutionType) {
        Iterable<FinancialInstitution> fis = financialInstitutionRepo.findByInstitutionTypeOrderByInstitutionNameAsc(institutionType);
        return convertEntitiesToDTOs(fis);
    }

    @Override
    public List<FinancialInstitutionDTO> getOtherLocalBanks( String bankCode) {
       List<FinancialInstitution>  fis = financialInstitutionRepo.findByInstitutionTypeAndInstitutionCodeIgnoreCaseNot(FinancialInstitutionType.LOCAL,bankCode);
        return convertEntitiesToDTOs(fis);
    }

    @Override
    public FinancialInstitutionDTO getFinancialInstitution(Long id) {
        return convertEntityToDTO(financialInstitutionRepo.findOne(id));
    }

    @Override
    @Verifiable(operation="DELETE_FIN_INST",description="Deleting a Financial Institution")
    public String deleteFinancialInstitution(Long id) throws InternetBankingException {
      try {
          FinancialInstitution finInst = financialInstitutionRepo.findOne(id);
          financialInstitutionRepo.delete(finInst);
          logger.info("Financial institution  with Id {} deleted ", id.toString());
          return messageSource.getMessage("institution.delete.success", null, locale);
      }
      catch (VerificationInterruptedException e) {
          return e.getMessage();
      }
      catch (InternetBankingException e){
          throw e;
      }
      catch (Exception e){
          throw new InternetBankingException(messageSource.getMessage("institution.delete.failure", null, locale));
      }
    }

    @Override
    public Page<FinancialInstitutionDTO> getFinancialInstitutions(Pageable pageDetails) {
        Page<FinancialInstitution> page = financialInstitutionRepo.findAll(pageDetails);
        List<FinancialInstitutionDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<FinancialInstitutionDTO> pageImpl = new PageImpl<FinancialInstitutionDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }


    @Override
    public Page<FinancialInstitutionDTO> getFinancialInstitutionsWithSortCode(Pageable pageDetails) {
        Page<FinancialInstitution> page = financialInstitutionRepo.findBySortCodeIsNotNull(pageDetails);
        List<FinancialInstitutionDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<FinancialInstitutionDTO> pageImpl = new PageImpl<FinancialInstitutionDTO>(dtOs, pageDetails, t);
        return pageImpl;    }

    @Override
    public FinancialInstitution getFinancialInstitutionByCode(String institutionCode) {
        return financialInstitutionRepo.findByInstitutionCode(institutionCode);
    }

    @Override
    public FinancialInstitution getFinancialInstitutionByName(String institutionName) {
        return financialInstitutionRepo.findFirstByInstitutionNameIgnoreCase(institutionName);
    }

    @Override
	public Page<FinancialInstitutionDTO> findFinancialInstitutions(String pattern, Pageable pageDetails) {
		 Page<FinancialInstitution> page = financialInstitutionRepo.findUsingPattern(pattern,pageDetails);
	        List<FinancialInstitutionDTO> dtOs = convertEntitiesToDTOs(page.getContent());
	        long t = page.getTotalElements();

	        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
	        Page<FinancialInstitutionDTO> pageImpl = new PageImpl<FinancialInstitutionDTO>(dtOs, pageDetails, t);
	        return pageImpl;
	}
}
