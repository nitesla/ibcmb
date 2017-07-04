package longbridge.repositories;

import longbridge.models.CorporateRole;
import longbridge.models.Corporate;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Fortune on 6/7/2017.
 */
public interface CorporateRoleRepo extends CommonRepo<CorporateRole,Long> {


    Page<CorporateRole> findByCorporate(Corporate corporate, Pageable pageable);
    Set<CorporateRole> findByCorporate(Corporate corporate);

}
