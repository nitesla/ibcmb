package longbridge.repositories;

import longbridge.models.AccountLimit;
import longbridge.models.ClassLimit;
import longbridge.models.GlobalLimit;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface AccountLimitRepo extends CommonRepo<AccountLimit, Long>{

    List<AccountLimit> findByCustomerType(String type);
}
