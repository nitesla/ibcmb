package longbridge.repositories;

import longbridge.models.CorpTransRole;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
@Transactional
public interface CorpTransRoleRepo extends CommonRepo<CorpTransRole,Long> {

}
