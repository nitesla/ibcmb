package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import longbridge.models.Verifiable;

/**
 *This {@code CodeService} interface provides the methods for managing system codes
 * A {@code Code}can be used add new items to the system which can be displayed in list or in drop down menus
 * @see Code
 * @author Wunmi
 */
public interface CodeService extends Verifiable<Code> {

    /**
     * Adds a new code to the syste
     * @param code the code
     */
    boolean addCode(CodeDTO code, AdminUser adminUser);

    /**
     * Modifies an existing code
     * @param codeDTO the code
     */
    boolean updateCode(CodeDTO codeDTO, AdminUser adminUser);

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
    
    Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails);

    /**
     * Returns all the codes in the system
     * @return a list of the codes
     */
    Iterable<CodeDTO> getCodes();

}
