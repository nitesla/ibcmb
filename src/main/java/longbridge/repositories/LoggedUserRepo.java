package longbridge.repositories;

import longbridge.models.LoggedUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ayoade_farooq@yahoo.com on 8/30/2017.
 */
public interface LoggedUserRepo  extends JpaRepository<LoggedUser,Long>{
  boolean existsFirstBySessionId(String s);





}
