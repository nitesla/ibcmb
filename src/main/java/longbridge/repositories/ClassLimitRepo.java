package longbridge.repositories;

import longbridge.models.ClassLimit;
import longbridge.models.GlobalLimit;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface ClassLimitRepo extends CommonRepo<ClassLimit, Long>{

    List<ClassLimit> findByCustomerType(String type);

    ClassLimit findByCustomerTypeAndAccountClassAndChannel(String customerType, String accountClass, String channel);

}


