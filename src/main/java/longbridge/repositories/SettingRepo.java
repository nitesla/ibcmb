package longbridge.repositories;

import longbridge.models.Setting;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 4/13/2017.
 */

@Repository
public interface SettingRepo extends CommonRepo<Setting,Long>{

    Setting findByName(String name);
    
}
