package longbridge.repositories;

import longbridge.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface ProfileRepo extends JpaRepository<Profile, Long>{

}
