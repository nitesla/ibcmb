package longbridge.repositories;

import longbridge.dtos.SettingDTO;
import longbridge.models.Setting;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Fortune on 4/13/2017.
 */

@Repository
public interface SettingRepo extends CommonRepo<Setting,Long>{

    Setting findByName(String name);
    
}
