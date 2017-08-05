package longbridge.services;

import longbridge.dtos.CorpUserVerificationDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationException;
import longbridge.models.CorpUserVerification;
import longbridge.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Showboy on 28/07/2017.
 */
public interface CorpUserVerificationService {

//    String addUserFromCorporateAdmin(CorporateUserDTO user) throws InternetBankingException;

    String changeStatusFromCorporateAdmin(Long id) throws InternetBankingException;

    void saveInitiator(CorporateUserDTO user, String operation, String description) throws VerificationException;

    void saveAuthorizer(CorporateUserDTO user, String operation, String description) throws VerificationException;

    String decline(CorpUserVerificationDTO dto) throws VerificationException;

    String verify(Long id) throws VerificationException;

    String verify(CorpUserVerificationDTO dto) throws VerificationException;

    CorpUserVerificationDTO getVerification(Long id);

    CorpUserVerificationDTO convertEntityToDTO(CorpUserVerification corpUserVerification);

    List<CorpUserVerificationDTO> convertEntitiesToDTOs(Iterable<CorpUserVerification> corpUserVerifications);

    long getTotalNumberPending();

    int getTotalNumberForVerification();

    Page<CorpUserVerificationDTO> getAllRequests(Pageable pageable);

    Page<CorpUserVerificationDTO> getRequestsByCorpId(Long corpId, Pageable pageable);

    List<String> getPermissionCodes(Role role);

}
