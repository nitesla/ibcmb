package longbridge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.dtos.PendingVerification;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.VerificationException;
import longbridge.models.SerializableEntity;
import longbridge.models.User;
import longbridge.models.Verification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface VerificationService {

    /**
     * This will decline a Verification request.
     *
     * @param verification  The {@link Verification} object
     * @param declineReason The reason for declining the verification request
     */
    String decline(Verification verification, String declineReason) throws VerificationException;

    /**
     * This will verify/approve a Verification request.
     *
     * @param
     */


    int getTotalNumberForVerification(User user);


    long getTotalNumberPending(User user);


    List<VerificationDTO> getVerificationsForUser(User user);

    Page<VerificationDTO> getMakerCheckerPending(Pageable pageDetails, User createdBy);


    List<VerificationDTO>getPendingForUser(User user);

    String verify(Long verId) throws VerificationException;

    /**
     * This fetches the {@link Verification} object with the id {@code id}
     *
     * @param id the id of the record in the db
     * @return The {@link Verification} object identified by {@code id} or null if none is found
     */
    VerificationDTO getVerification(Long id);

    /**
     * Creates a new <b>add</b> {@link Verification} request
     *
     * @param entity The entity to verify
     * @throws JsonProcessingException if there is an error in serializing the entity
     */


    <T extends SerializableEntity<T>> String addNewVerificationRequest(T entity, User createdBy, String operation) throws JsonProcessingException, VerificationException;


    /**
     * Create a new <b>modify</b> {@link Verification} request
     *
     * @param entity         The modified entity
     * @throws JsonProcessingException if there is an error in serializing the entity
     */
    <T extends SerializableEntity<T>> String addModifyVerificationRequest(T entity, User doneBy, String operation) throws JsonProcessingException;


    Page<PendingVerification> getPendingVerifications(User user, Pageable pageable);

    Page<VerificationDTO> getPendingOperations(String operation, User user, Pageable pageable);

    <T extends SerializableEntity<T>> String save(T originalEntity, T entity, User createdBy, String operation) throws JsonProcessingException, VerificationException;
}
