package longbridge.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/7/2017.
 */
public interface Verifiable<T> {

    /** This adds a new request to create an instance of {@code T}
     * in the database
     */
    void add(T role, AdminUser initiator);

    /** This tries to modify an  existing record of {@code T}
     * in the database
     */
    void modify(T role, AdminUser initiator);

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

    /** This serializes a List of Entities. It ignores all information  besides the
     * Id
     * @param list
     * @return
     */
    default ArrayNode serializeEntityList(Collection< ? extends AbstractEntity> list, ArrayNode arrayNode){
        for(AbstractEntity entity: list){
            arrayNode.add(entity.getId());
        }
        return arrayNode;
    }

}
