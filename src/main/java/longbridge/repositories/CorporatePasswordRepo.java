package longbridge.repositories;

import longbridge.models.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by Fortune on 6/5/2017.
 */
@Repository
public interface CorporatePasswordRepo extends CommonRepo<CorporatePassword,Long>{

    List<CorporatePassword> findByUsername(String username);

    int countByUsername(String username);

    CorporatePassword findFirstByUsername(String username);

}
