package longbridge.services.implementations;

import longbridge.dtos.SettingDTO;
import longbridge.models.Setting;
import longbridge.repositories.SettingRepo;
import longbridge.services.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Fortune on 4/13/2017.
 */

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	SettingRepo settingRepo;

	@Autowired
	ModelMapper modelMapper;

	@Transactional
	@Override
	public void addSetting(SettingDTO dto) {
		ModelMapper mapper = new ModelMapper();
		Setting setting = mapper.map(dto, Setting.class);
		settingRepo.save(setting);
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
	public void updateSetting(SettingDTO dto) {
		Setting setting = settingRepo.findOne(dto.getId());
		ModelMapper mapper = new ModelMapper();
		mapper.map(dto, setting);
		settingRepo.save(setting);
	}

	@Override
	public void deleteSetting(Long id) {
		settingRepo.delete(id);
	}

	private SettingDTO convertEntityToDTO(Setting setting) {
		return modelMapper.map(setting, SettingDTO.class);
	}

}
