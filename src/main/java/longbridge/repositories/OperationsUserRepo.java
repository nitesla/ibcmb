package longbridge.repositories;

import longbridge.models.OperationsUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Fortune on 4/5/2017.
 */
public interface OperationsUserRepo extends CommonRepo<OperationsUser, Long> {

    OperationsUser findFirstByUserName(String s);
}