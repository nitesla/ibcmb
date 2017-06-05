package longbridge.repositories;

import longbridge.models.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by Fortune on 6/5/2017.
 */
@Repository
public interface CorporatePasswordRepo extends CommonRepo<CorporatePassword,Long>{

    List<CorporatePassword> findByCorporateUser(CorporateUser corporateUser);

    int countByCorporateUser(CorporateUser user);

    CorporatePassword findFirstByCorporateUser(CorporateUser corporateUser);

}
