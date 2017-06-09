package longbridge.repositories;

<<<<<<< HEAD
import longbridge.models.CorporateRole;
=======
import longbridge.models.Corporate;
import longbridge.models.CorporateRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
>>>>>>> corpTransferRule

/**
 * Created by Fortune on 6/7/2017.
 */
public interface CorporateRoleRepo extends CommonRepo<CorporateRole,Long> {

<<<<<<< HEAD
=======
    Page<CorporateRole> findByCorporate(Corporate corporate, Pageable pageable);
>>>>>>> corpTransferRule
}
