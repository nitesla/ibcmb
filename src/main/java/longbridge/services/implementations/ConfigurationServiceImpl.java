package longbridge.services.implementations;

import longbridge.dtos.SettingDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.Setting;
import longbridge.repositories.SettingRepo;
import longbridge.services.ConfigurationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Created by Fortune on 4/13/2017.
 */

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	SettingRepo settingRepo;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EntityManager entityManager;

	Locale locale = LocaleContextHolder.getLocale();

	@Transactional
	@Override
	@Verifiable(operation="ADD_SETTING",description="Add Settings")
	public String addSetting(SettingDTO dto) throws InternetBankingException {
		try {
			ModelMapper mapper = new ModelMapper();
			Setting setting = mapper.map(dto, Setting.class);
			settingRepo.save(setting);
			return messageSource.getMessage("setting.add.success", null, locale);
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("setting.add.failure",null,locale),e);
		}
	}

	@Override
	public SettingDTO getSetting(Long id) {
		Setting setting = settingRepo.findOne(id);
		ModelMapper mapper = new ModelMapper();
		return mapper.map(setting, SettingDTO.class);
	}

	@Override
	public SettingDTO getSettingByName(String name) {
		return convertEntityToDTO(settingRepo.findByName(name));
	}

	@Override
	public Iterable<SettingDTO> getSettings() {
		List<Setting> all = settingRepo.findAll();
		return convertEntitiesToDTOs(all);
	}

	
	@Override
	public Page<SettingDTO> getSettings(Pageable pageDetails) {
		Page<Setting> page = settingRepo.findAll(pageDetails);
		List<SettingDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
		Page<SettingDTO> pageImpl = new PageImpl<SettingDTO>(dtOs, pageDetails, t);
		return pageImpl;
	}

	private List<SettingDTO> convertEntitiesToDTOs(List<Setting> content) {
		ModelMapper mapper = new ModelMapper();
		List<SettingDTO> allDto = new ArrayList<>();
		for (Setting s : content) {
			SettingDTO dto = mapper.map(s, SettingDTO.class);
			allDto.add(dto);
		}
		return allDto;
	}

	
	@Transactional
	@Override
	@Verifiable(operation="UPDATE_SETTING",description="Update Settings")
	public String updateSetting(SettingDTO dto) throws InternetBankingException {
		try {
			Setting setting = settingRepo.findOne(dto.getId());
			entityManager.detach(setting);
			ModelMapper mapper = new ModelMapper();
			mapper.map(dto, setting);
			settingRepo.save(setting);
			return messageSource.getMessage("setting.update.success", null, locale);
		}
		catch (DuplicateObjectException e) {
			throw new DuplicateObjectException(e.getMessage());
		}
		catch (Exception e) {
			throw new InternetBankingException(messageSource.getMessage("setting.update.failure",null,locale),e);
		}
	}

	@Override
	@Verifiable(operation="DELETE_SETTING",description="Delete Settings")
	public String deleteSetting(Long id) throws InternetBankingException {
		try {
			settingRepo.delete(id);
			return messageSource.getMessage("setting.delete.success", null, locale);
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
		Page<Setting> page = settingRepo.findUsingPattern(pattern, pageDetails);
		List<SettingDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
		Page<SettingDTO> pageImpl = new PageImpl<SettingDTO>(dtOs, pageDetails, t);
		return pageImpl;
	}

}
