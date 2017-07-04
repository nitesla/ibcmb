package longbridge.services;

import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 *The {@code ConfigurationService} interface provides the methods for setting and managing system configurations
 *@author Fortunatus Ekenachi
 */

public interface ConfigurationService{

    @PreAuthorize("hasAuthority('ADD_SETTING')")
    String addSetting( SettingDTO setting) throws InternetBankingException;

    SettingDTO getSetting(Long id);

    SettingDTO getSettingByName(String name);

    Iterable<SettingDTO> getSettings();

    Page<SettingDTO> findSetting(String pattern,Pageable pageDetails);
    
    Page<SettingDTO> getSettings(Pageable pageDetails);

    @PreAuthorize("hasAuthority('UPDATE_SETTING')")
    String updateSetting(SettingDTO setting) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_SETTING')")
    String deleteSetting(Long id) throws InternetBankingException;

}
