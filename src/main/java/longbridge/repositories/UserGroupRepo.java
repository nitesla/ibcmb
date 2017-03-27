package longbridge.repositories;

import longbridge.models.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface UserGroupRepo extends JpaRepository<UserGroup, Long> {
}
