package longbridge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.VerificationException;
import longbridge.models.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VerificationService {



    /**
     * This will decline a Verification request.
     *
     * @param verification  The {@link Verification} object
     */
    String decline(VerificationDTO verification) throws VerificationException;

    /**
     * This will verify/approve a Verification request.
     *
     * @param
     */
    int getTotalNumberForVerification();


    long getTotalNumberPending();


    Page<Verification> getVerificationsForUser(Pageable pageable);

    Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails);


    Page<VerificationDTO>getPendingForUser(Pageable pageable);

    String verify(VerificationDTO verification) throws VerificationException;

    String verify(Long id) throws VerificationException;

    String add(Object o, String operation, String description) throws JsonProcessingException;

    /**
     * This fetches the {@link Verification} object with the id {@code id}
     *
     * @param id the id of the record in the db
     * @return The {@link Verification} object identified by {@code id} or null if none is found
     */
    VerificationDTO getVerification(Long id);


    Page<VerificationDTO> getPendingOperations(String operation, Pageable pageable);

    Page<VerificationDTO> getVerifiedOPerations(Pageable pageable);
}
