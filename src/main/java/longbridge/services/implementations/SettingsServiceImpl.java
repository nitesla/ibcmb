package longbridge.services.implementations;

import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Setting;
import longbridge.repositories.SettingRepo;
import longbridge.services.SettingsService;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Locale;

/**
 * Created by Fortune on 4/13/2017.
 */

@Service
public class SettingsServiceImpl implements SettingsService {

	@Autowired
	private SettingRepo settingRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private EntityManager entityManager;

	private final Locale locale = LocaleContextHolder.getLocale();

	@Transactional
	@Override
	@Verifiable(operation="ADD_SETTING",description="Add Settings")
	public String addSetting(SettingDTO dto)  {
		try {
			ModelMapper mapper = new ModelMapper();
			Setting setting = mapper.map(dto, Setting.class);
			settingRepo.save(setting);
			return messageSource.getMessage("setting.add.success", null, locale);
		}
		catch (VerificationInterruptedException e){
			return e.getMessage();
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("setting.add.failure",null,locale),e);
		}
	}

	@Override
	public SettingDTO getSetting(Long id) {
		Setting setting = settingRepo.findById(id).get();
		ModelMapper mapper = new ModelMapper();
		return mapper.map(setting, SettingDTO.class);
	}

	@Override
	@Cacheable(value = "settings", unless = "#result == null")
	public SettingDTO getSettingByName(String name) {
		return convertEntityToDTO(settingRepo.findByName(name));
	}



	@Override
	public Page<SettingDTO> getSettings(Pageable pageDetails) {
		return settingRepo.findAll(pageDetails).map(this::convertEntityToDTO);
	}

	@Transactional
	@Override
	@Verifiable(operation="UPDATE_SETTING",description="Update Settings")
	@CacheEvict(value = "settings", key = "#dto.name")
	public String updateSetting(SettingDTO dto)  {
		try {
			Setting setting = settingRepo.findById(dto.getId()).get();
			entityManager.detach(setting);
			ModelMapper mapper = new ModelMapper();
			mapper.map(dto, setting);
			settingRepo.save(setting);
			return messageSource.getMessage("setting.update.success", null, locale);
		}
		catch (VerificationInterruptedException e){
			return e.getMessage();
		}
		catch (InternetBankingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new InternetBankingException(messageSource.getMessage("setting.update.failure",null,locale),e);
		}
	}

	@Override
	@Verifiable(operation="DELETE_SETTING",description="Delete Settings")
	public String deleteSetting(Long id)  {
		try {
			Setting setting = settingRepo.findById(id).get();
			settingRepo.delete(setting);
			return messageSource.getMessage("setting.delete.success", null, locale);
		}
		catch (VerificationInterruptedException e){
			return e.getMessage();
		}
		catch (InternetBankingException e){
			throw e;
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("setting.delete.failure", null, locale),e);
		}
	}

	private SettingDTO convertEntityToDTO(Setting setting) {
		if(setting!=null) {
			return modelMapper.map(setting, SettingDTO.class);
		}
		return null;
	}

	@Override
	public Page<SettingDTO> findSetting(String pattern, Pageable pageDetails) {
		return settingRepo.findUsingPattern(pattern, pageDetails).map(this::convertEntityToDTO);
	}

}
