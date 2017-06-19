package longbridge.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import longbridge.exception.VerificationException;
import longbridge.models.SerializableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by Fortune on 4/5/2017.
 */
@NoRepositoryBean
public interface CommonRepo<T, ID extends Serializable> extends JpaRepository<T, ID> {

    <T extends SerializableEntity<T>> String makerCheckerSave(T originalEntity, T entity) throws JsonProcessingException, VerificationException;

}