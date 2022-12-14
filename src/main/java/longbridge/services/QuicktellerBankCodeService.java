package longbridge.services;

import longbridge.dtos.QuicktellerBankCodeDTO;
import longbridge.models.QuicktellerBankCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface QuicktellerBankCodeService {

    String addQuicktellerBankCode(QuicktellerBankCodeDTO quicktellerBankCodeDTO) ;

    String updateQuicktellerBankCode(QuicktellerBankCodeDTO quicktellerBankCodeDTO) ;

    List<QuicktellerBankCodeDTO> getQuicktellerBankCodes();

    QuicktellerBankCodeDTO getQuicktellerBankCode(Long id);

    String deleteQuicktellerBankCode(Long id) ;

    Page<QuicktellerBankCode> getQuicktellerBankCodes(Pageable pageDetails);

    Page<QuicktellerBankCode> findQuicktellerBankCodes(String pattern,Pageable pageDetails);

    QuicktellerBankCode getQuicktellerBankCodeByBankCode(String BankCode);

    QuicktellerBankCode getQuicktellerBankCodeByBankCodeId(String bankCodeId);

    QuicktellerBankCode getQuicktellerBankCodeByName(String bankName);

    List<QuicktellerBankCodeDTO> getOtherLocalBanks();

    QuicktellerBankCodeDTO convertEntityToDTO(QuicktellerBankCode financialInstitution);

    QuicktellerBankCode convertDTOToEntity(QuicktellerBankCodeDTO quicktellerBankCodeDTO);

    List<QuicktellerBankCodeDTO> convertEntitiesToDTOs(Iterable<QuicktellerBankCode> financialInstitutions);

    void refreshBankCodes();
}
