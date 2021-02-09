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

public interface SettingsService {

    @PreAuthorize("hasAuthority('ADD_SETTING')")
    String addSetting( SettingDTO setting) ;

    SettingDTO getSetting(Long id);

    SettingDTO getSettingByName(String name);

    Page<SettingDTO> findSetting(String pattern,Pageable pageDetails);
    
    Page<SettingDTO> getSettings(Pageable pageDetails);

    @PreAuthorize("hasAuthority('UPDATE_SETTING')")
    String updateSetting(SettingDTO setting) ;

    @PreAuthorize("hasAuthority('DELETE_SETTING')")
    String deleteSetting(Long id) ;

}
