package longbridge.services;

import longbridge.dtos.GlobalLimitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.SettingDTO;
import longbridge.models.Setting;

/**
 *The {@code ConfigurationService} interface provides the methods for setting and managing system configurations
 *@author Fortunatus Ekenachi
 */

public interface ConfigurationService{

    void addSetting( SettingDTO setting);

    SettingDTO getSetting(Long id);

    SettingDTO getSettingByName(String name);

    Iterable<SettingDTO> getSettings();

    Page<SettingDTO> getSettings(Pageable pageDetails);

    void updateSetting(SettingDTO setting);

    void deleteSetting(Long id);







}
