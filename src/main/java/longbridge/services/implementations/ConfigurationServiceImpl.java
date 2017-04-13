package longbridge.services.implementations;

import longbridge.models.Setting;
import longbridge.repositories.SettingRepo;
import longbridge.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Fortune on 4/13/2017.
 */

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    SettingRepo settingRepo;

    @Override
    public void addSetting(Setting setting) {
      settingRepo.save(setting);
    }

    @Override
    public Setting getSetting(Long id) {
        return  settingRepo.findOne(id);
    }

    @Override
    public Setting getSettingByName(String name) {
        return settingRepo.findByName(name);
    }

    @Override
    public Iterable<Setting> getSettings() {
        return settingRepo.findAll();
    }

    @Override
    public void updateSetting(Setting setting) {
        settingRepo.save(setting);
    }

    @Override
    public void deleteSetting(Long id) {
        settingRepo.delete(id);
    }
}
