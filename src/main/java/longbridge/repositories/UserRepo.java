package longbridge.repositories;

import longbridge.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository

public interface UserRepo<T extends User,ID extends Serializable> extends CommonRepo<T, ID>
{
}
