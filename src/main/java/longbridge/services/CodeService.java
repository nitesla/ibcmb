package longbridge.services;

import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 *This {@code CodeService} interface provides the methods for managing system codes
 * A {@code Code}can be used add new items to the system which can be displayed in list or in drop down menus
 * @see Code
 * @author Wunmi
 */
public interface CodeService {

    /**
     * Deletes a code from the system
     * @param codeId the oode's id
     */
    boolean deleteCode(Long codeId);

    /**
     * Returns the specified code
     * @param codeId the code's id
     * @return the code
     */
    CodeDTO getCode(Long codeId);

    /**
     * Returns a list of codes specified by the given type
     * @param codeType the code's type
     * @return a list of codes
     */
    Iterable<CodeDTO> getCodesByType(String codeType);

    public boolean updateCode(CodeDTO codeDTO, AdminUser adminUser);


    Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails);

    Page<CodeDTO> getCodes(Pageable pageDetails);

    /**
     * Returns all the codes in the system
     * @return a list of the codes
     */
    Iterable<CodeDTO> getCodes();

    CodeDTO convertEntityToDTO(Code code);

    Code convertDTOToEntity(CodeDTO codeDTO);

    List<CodeDTO> convertEntitiesToDTOs(Iterable<Code> codes);
    
}
