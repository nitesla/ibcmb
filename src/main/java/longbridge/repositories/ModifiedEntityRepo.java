package longbridge.repositories;

import longbridge.config.audits.ModifiedEntityTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Longbridge on 10/26/2017.
 */
@Repository
public interface ModifiedEntityRepo extends JpaRepository<ModifiedEntityTypeEntity,Long> {

}
