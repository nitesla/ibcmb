package longbridge.services;

import longbridge.dtos.VerificationDTO;
import longbridge.exception.VerificationException;
import longbridge.models.CorpUserVerification;
import longbridge.models.CorporateUser;
import longbridge.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Showboy on 28/07/2017.
 */
public interface CorpUserVerificationService {

    void save(CorporateUser user, String operation, String description) throws VerificationException;

    String decline(VerificationDTO dto) throws VerificationException;

    String verify(Long id) throws VerificationException;

    String verify(VerificationDTO dto) throws VerificationException;

    VerificationDTO getVerification(Long id);

    VerificationDTO convertEntityToDTO(CorpUserVerification corpUserVerification);

    List<VerificationDTO> convertEntitiesToDTOs(Iterable<CorpUserVerification> corpUserVerifications);

    Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails);

    Page<VerificationDTO> getPendingOperations(String operation, Pageable pageable);

    long getTotalNumberPending();

    int getTotalNumberForVerification();

    Page<CorpUserVerification> getVerificationsForUser(Pageable pageable);

    List<String> getPermissionCodes(Role role);

    Page<VerificationDTO> getPendingForUser(Pageable pageable);

    Page<VerificationDTO> getVerifiedOPerations(Pageable pageable);
}
