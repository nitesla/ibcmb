package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.models.Setting;

/**
 *The {@code ConfigurationService} interface provides the methods for setting and managing system configurations
 *@author Fortunatus Ekenachi
 */

public interface ConfigurationService {

    void addSetting( Setting setting);

    Setting getSetting(Long id);

    SettingDTO getSettingByName(String name);

    Iterable<Setting> getSettings();

    void updateSetting(Setting setting);

    void deleteSetting(Long id);

}
