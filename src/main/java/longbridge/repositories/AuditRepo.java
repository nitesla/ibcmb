package longbridge.repositories;

import longbridge.config.audits.ModifiedEntityTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Longbridge on 10/26/2017.
 */
@NoRepositoryBean
public interface AuditRepo<T, ID extends Serializable> extends JpaRepository<T, ID> {
    List<ModifiedEntityTypeEntity> searchModifiedEntity(String entityName, Class<?> clazz, String search);
}
