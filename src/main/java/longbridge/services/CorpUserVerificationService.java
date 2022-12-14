package longbridge.services;

import longbridge.dtos.CorpUserVerificationDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.VerificationException;
import longbridge.models.CorpUserVerification;
import longbridge.models.Role;
import longbridge.models.UserType;
import longbridge.utils.Verifiable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Showboy on 28/07/2017.
 */
public interface CorpUserVerificationService {


    @Verifiable(operation = "UPDATE_ACCOUNT_PERMISSION_FROM_CORPORATE_ADMIN", description = "Update corporate user account permission",type = UserType.CORPORATE)
    String updateAccountPermissionsFromCorporateAdmin(CorporateUserDTO corporateUserDTO);

    String changeStatusFromCorporateAdmin(Long id) ;

    void addInitiator(CorporateUserDTO user, String operation, String description) throws VerificationException;

    void saveInitiator(CorporateUserDTO userDTO, String operation, String description) throws VerificationException;

    void addAuthorizer(CorporateUserDTO user, String operation, String description) throws VerificationException;

    void saveAuthorizer(CorporateUserDTO userDTO, String operation, String description) throws VerificationException;

    String decline(CorpUserVerificationDTO dto) throws VerificationException;

    String verify(Long id) throws VerificationException;

    boolean isPendingVerification(Long entityId, String entityName);

    String verify(CorpUserVerificationDTO dto) throws VerificationException;

    CorpUserVerificationDTO getVerification(Long id);

    CorpUserVerificationDTO convertEntityToDTO(CorpUserVerification corpUserVerification);

    List<CorpUserVerificationDTO> convertEntitiesToDTOs(Iterable<CorpUserVerification> corpUserVerifications);

    int getTotalNumberPending();

    int getTotalNumberForVerification();

    Page<CorpUserVerificationDTO> getAllRequests(Pageable pageable);

    Page<CorpUserVerificationDTO> getRequestsByCorpId(Long corpId, Pageable pageable);

    List<String> getPermissionCodes(Role role);

}
