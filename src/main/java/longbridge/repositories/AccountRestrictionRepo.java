package longbridge.repositories;

import longbridge.models.AccountRestriction;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 4/27/2017.
 */
@Repository
public interface AccountRestrictionRepo extends CommonRepo<AccountRestriction,Long> {

    AccountRestriction findByRestrictionValue(String accountNumber);
    AccountRestriction findFirstByRestrictionValueAndRestrictedForIgnoreCase(String accountNumber ,String s);


}
