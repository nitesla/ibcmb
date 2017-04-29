package longbridge.repositories;

import longbridge.models.AccountClassRestriction;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 4/27/2017.
 */

@Repository
public interface AccountClassRestrictionRepo extends CommonRepo<AccountClassRestriction,Long> {

    AccountClassRestriction findByAccountClass(String accountClass);

}
