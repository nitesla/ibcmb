package longbridge.repositories;

import longbridge.models.GlobalLimit;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface GlobalLimitRepo extends CommonRepo<GlobalLimit, Long>{

    List<GlobalLimit> findByCustomerType(String type);

    GlobalLimit findByChannel(String channel);
}
