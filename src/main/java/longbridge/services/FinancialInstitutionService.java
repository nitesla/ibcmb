package longbridge.services;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
public interface FinancialInstitutionService {

    @PreAuthorize("hasAuthority('ADD_FIN_INST')")
    String addFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) throws InternetBankingException, DuplicateObjectException;

    @PreAuthorize("hasAuthority('UPDATE_FIN_INST')")
    String updateFinancialInstitution(FinancialInstitutionDTO financialInstitutionDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('GET_FIN_INST')")
    List<FinancialInstitutionDTO> getFinancialInstitutions();

    @PreAuthorize("hasAuthority('GET_FIN_INST')")
    List<FinancialInstitutionDTO> getFinancialInstitutionsByType(FinancialInstitutionType institutionType);
    List<FinancialInstitutionDTO> getOtherLocalBanks(String bankCode);

    @PreAuthorize("hasAuthority('GET_FIN_INST')")
    FinancialInstitutionDTO getFinancialInstitution(Long id);

    @PreAuthorize("hasAuthority('DELETE_FIN_INST')")
    String deleteFinancialInstitution(Long id) throws InternetBankingException;

    @PreAuthorize("hasAuthority('GET_FIN_INST')")
    Page<FinancialInstitutionDTO> getFinancialInstitutions(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_FIN_INST')")
    FinancialInstitution getFinancialInstitutionByCode(String institutionCode);


    FinancialInstitutionDTO convertEntityToDTO(FinancialInstitution financialInstitution);

    FinancialInstitution convertDTOToEntity(FinancialInstitutionDTO financialInstitutionDTO);

    List<FinancialInstitutionDTO> convertEntitiesToDTOs(Iterable<FinancialInstitution> financialInstitutions);

}
