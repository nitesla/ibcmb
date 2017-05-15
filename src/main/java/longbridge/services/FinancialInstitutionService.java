package longbridge.services;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
public interface FinancialInstitutionService {

    String addFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) throws InternetBankingException, DuplicateObjectException;

    String updateFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) throws InternetBankingException;

    List<FinancialInstitutionDTO> getFinancialInstitutions();

    List<FinancialInstitutionDTO> getFinancialInstitutionsByType(FinancialInstitutionType institutionType);

    FinancialInstitutionDTO getFinancialInstitution(Long id);

    String deleteFinancialInstitution(Long id) throws InternetBankingException;

    Page<FinancialInstitutionDTO> getFinancialInstitutions(Pageable pageDetails);

    FinancialInstitutionDTO convertEntityToDTO(FinancialInstitution financialInstitution);

    FinancialInstitution convertDTOToEntity(FinancialInstitutionDTO financialInstitutionDTO);

    List<FinancialInstitutionDTO> convertEntitiesToDTOs(Iterable<FinancialInstitution> financialInstitutions);

    FinancialInstitution getFinancialInstitutionByCode(String institutionCode);
}
