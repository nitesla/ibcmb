package longbridge.repositories;

<<<<<<< HEAD
<<<<<<< HEAD:src/main/java/longbridge/repositories/UserRepo.java
=======

>>>>>>> wunmi
import longbridge.models.User;
=======
>>>>>>> OLUGINGIN:src/main/java/longbridge/repositories/UserRepo.java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository
public interface UserRepo<T,ID  extends Serializable> extends JpaRepository<T, ID> {

<<<<<<< HEAD:src/main/java/longbridge/repositories/UserRepo.java
    User findByEmail(String email);
=======
    //User findByEmail(String email);
>>>>>>> OLUGINGIN:src/main/java/longbridge/repositories/UserRepo.java

}
