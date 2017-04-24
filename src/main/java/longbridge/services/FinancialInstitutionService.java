package longbridge.services;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.models.FinancialInstitution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Showboy on 24/04/2017.
 */
public interface FinancialInstitutionService {

    boolean addFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO);

    boolean updateFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO);

    Iterable<FinancialInstitutionDTO> getFinancialInstitutions();

    FinancialInstitutionDTO getFinancialInstitution(Long id);

    boolean deleteFi(Long id);

    Page<FinancialInstitutionDTO> getFinancialInstitutions(Pageable pageDetails);

    FinancialInstitutionDTO convertEntityToDTO(FinancialInstitution financialInstitution);

    FinancialInstitution convertDTOToEntity(FinancialInstitutionDTO financialInstitutionDTO);

    List<FinancialInstitutionDTO> convertEntitiesToDTOs(Iterable<FinancialInstitution> financialInstitutions);
}
