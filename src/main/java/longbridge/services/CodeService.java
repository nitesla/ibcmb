package longbridge.services;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.CodeTypeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * This {@code CodeService} interface provides the methods for managing system
 * codes. A {@code Code}can be used add new items to the system which can be
 * displayed in list or in drop down menus
 * 
 * @see Code
 * @author Wunmi
 */
public interface CodeService {

	@PreAuthorize("hasAuthority('ADD_CODE')")
	String addCode(CodeDTO code, AdminUser adminUser) throws InternetBankingException;

	/**
	 * Deletes a code from the system
	 * 
	 * @param codeId
	 *            the oode's id
	 */
	@PreAuthorize("hasAuthority('DELETE_CODE')")
	String deleteCode(Long codeId) throws InternetBankingException;

	/**
	 * Returns the specified code
	 * 
	 * @param codeId
	 *            the code's id
	 * @return the code
	 */
	@PreAuthorize("hasAuthority('GET_CODE')")
	CodeDTO getCode(Long codeId);

	@PreAuthorize("hasAuthority('GET_CODE')")
	Code getCodeById(Long codeId);

	/**
	 * Returns a list of codes specified by the given type
	 * 
	 * @param codeType
	 *            the code's type
	 * @return a list of codes
	 */
	@PreAuthorize("hasAuthority('GET_CODES')")
	List<CodeDTO> getCodesByType(String codeType);

	@PreAuthorize("hasAuthority('UPDATE_CODE')")
	public String updateCode(CodeDTO codeDTO, AdminUser adminUser) throws InternetBankingException;

	@PreAuthorize("hasAuthority('GET_CODES')")
    Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails);

	@PreAuthorize("hasAuthority('GET_CODES')")
    Page<CodeTypeDTO> getCodeTypes(Pageable pageDetails);

	@PreAuthorize("hasAuthority('GET_CODES')")
	public Code getByTypeAndCode(String type, String code);

	@PreAuthorize("hasAuthority('GET_CODES')")
	Page<CodeDTO> getCodes(Pageable pageDetails);

	/**
	 * Returns all the codes in the system
	 * 
	 * @return a list of the codes
	 */
	@PreAuthorize("hasAuthority('GET_CODES')")
	Iterable<CodeDTO> getCodes();

	CodeDTO convertEntityToDTO(Code code);

	Code convertDTOToEntity(CodeDTO codeDTO);

	List<CodeDTO> convertEntitiesToDTOs(Iterable<Code> codes);


}
