package longbridge.repositories;

import longbridge.models.AccountLimit;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface AccountLimitRepo extends CommonRepo<AccountLimit, Long>{

    List<AccountLimit> findByCustomerType(String type);

    AccountLimit findByCustomerTypeAndAccountNumberAndChannel(String customerType, String accountNumber,String channel);
}
