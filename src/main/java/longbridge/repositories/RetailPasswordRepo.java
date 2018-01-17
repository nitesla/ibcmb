package longbridge.repositories;

import longbridge.models.RetailPassword;
import longbridge.models.RetailUser;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 6/5/2017.
 */
@Repository
public interface RetailPasswordRepo extends CommonRepo<RetailPassword,Long> {

    List<RetailPassword> findByUsername(String username);

    RetailPassword findFirstByUsername(String username);

    int countByUsername(String username);
}
