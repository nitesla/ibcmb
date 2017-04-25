package longbridge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.models.AbstractEntity;
import longbridge.models.SerializableEntity;
import longbridge.models.Verification;

public interface VerificationService {

     /** This will decline a Verification request.
      * 
      * @param verification The {@link Verification} object
      * @param declineReason The reason for declining the verification request
      */
	 void decline(Verification verification, String declineReason); 

	 /** This will verify/approve a Verification request.
	  * 
	  * @param verification The {@link Verification} object
	  */
     void verify(Verification verification);
     
     /** This fetches the {@link Verification} object with the id {@code id}
      * 
      * @param id the id of the record in the db
      * @return The {@link Verification} object identified by {@code id} or null if none is found
      */
     Verification getVerification(Long id);
     
     /** Creates a new <b>add</b> {@link Verification} request
      * 
      * @param entity The entity to verify
      * @throws JsonProcessingException if there is an error in serializing the entity
      */

    <T extends SerializableEntity<T>> String addNewVerificationRequest(T entity) throws JsonProcessingException;

    /** Create a new <b>modify</b> {@link Verification} request
      * 
      * @param originalEntity The existing entity
      * @param entity The modified entity
     * @throws JsonProcessingException if there is an error in serializing the entity
      */
      void addModifyVerificationRequest(AbstractEntity originalEntity, AbstractEntity entity) throws JsonProcessingException;
}
