package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *The {@code ConfigurationService} interface provides the methods for setting and managing system configurations
 *@author Fortunatus Ekenachi
 */

public interface ConfigurationService{

    String addSetting( SettingDTO setting) throws InternetBankingException;

    SettingDTO getSetting(Long id);

    SettingDTO getSettingByName(String name);

    Iterable<SettingDTO> getSettings();

    Page<SettingDTO> getSettings(Pageable pageDetails);

    String updateSetting(SettingDTO setting) throws InternetBankingException;

    String deleteSetting(Long id) throws InternetBankingException;

}
