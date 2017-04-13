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
public interface CodeService extends Verifiable<CodeDTO> {

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

    Page<CodeDTO> getCodes(Pageable pageDetails);

    /**
     * Returns all the codes in the system
     * @return a list of the codes
     */
    Iterable<CodeDTO> getCodes();

}
