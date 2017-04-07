package longbridge.models;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
public interface Verifiable<T> {
    /** Deserializes a string into the Type T
     *
     * @param data  data
     * @return Object equivalent
     */
    T deserialize(String data) throws IOException;

    /** Serializes an object of type T into json
     *
     * @param t object to be serialized
     * @return data
     */
    String serialize(T t) throws JsonProcessingException;

    /** Saves the object t to the database
     *
     * @param t verification object
     * @param verifier the AdminUser verifying this request
     */
    void verify(Verification t, AdminUser verifier) throws IOException;

    /** This declines a verification request
     *
     * @param verification the verification object
     * @param decliner the AdminUser declining this request
     * @param declineReason reason for declining the request
     */
    void decline(Verification verification, AdminUser decliner, String declineReason);
}
