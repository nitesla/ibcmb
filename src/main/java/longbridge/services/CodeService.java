package longbridge.services;

import longbridge.models.Code;

/**
 *This {@code CodeService} interface provides the methods for managing system codes
 * A {@code Code}can be used add new items to the system which can be displayed in list or in drop down menus
 * @see Code
 * @author Wunmi
 */
public interface CodeService {

    /**
     * Adds a new code to the syste
     * @param code the code
     */
    void  addCode(Code code);

    /**
     * Deletes a code from the system
     * @param codeId the oode's id
     */
    void deleteCode(Long codeId);

    /**
     * Returns the specified code
     * @param codeId the code's id
     * @return the code
     */
    Code getCode(Long codeId);

    /**
     * Returns a list of codes specified by the given type
     * @param codeType the code's type
     * @return a list of codes
     */
    Iterable<Code> getCodesofType(String codeType);

    /**
     * Returns all the codes in the system
     * @return a list of the codes
     */
    Iterable<Code> getCodes();
}
