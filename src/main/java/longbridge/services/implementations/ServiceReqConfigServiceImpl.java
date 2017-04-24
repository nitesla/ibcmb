package longbridge.services.implementations;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.models.ServiceReqConfig;
import longbridge.models.ServiceReqFormField;
import longbridge.repositories.ServiceReqConfigRepo;
import longbridge.repositories.ServiceReqFormFieldRepo;
import longbridge.services.ServiceReqConfigService;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Service
public class ServiceReqConfigServiceImpl implements ServiceReqConfigService {

    private ServiceReqConfigRepo serviceReqConfigRepo;
    private ServiceReqFormFieldRepo serviceReqFormFieldRepo;
	private ModelMapper modelMapper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public ServiceReqConfigServiceImpl(ServiceReqConfigRepo serviceReqConfigRepo,
			ServiceReqFormFieldRepo serviceReqFormFieldRepo) {
		this.serviceReqConfigRepo = serviceReqConfigRepo;
		this.serviceReqFormFieldRepo = serviceReqFormFieldRepo;
	}

	@Override
	@Transactional
	public void addServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) {
		ServiceReqConfig serviceReqConfig = convertDTOToEntity(serviceReqConfigDTO);
		Iterator<ServiceReqFormField> serviceReqFormFieldIterator = serviceReqConfig.getFormFields().iterator();

		while (serviceReqFormFieldIterator.hasNext()) {
			ServiceReqFormField serviceReqFormField = serviceReqFormFieldIterator.next();
			if (serviceReqFormField.getFieldName() == null) {
				serviceReqFormFieldIterator.remove();
			} else {
				serviceReqFormField.setServiceReqConfig(serviceReqConfig);
			}
		}
		serviceReqConfigRepo.save(serviceReqConfig);
	}

	@Override
	public ServiceReqConfigDTO getServiceReqConfig(Long id) {
		ServiceReqConfig serviceReqConfig = serviceReqConfigRepo.findOne(id);
		modelMapper = new ModelMapper();
		return modelMapper.map(serviceReqConfig, ServiceReqConfigDTO.class);
	}

	@Override
	public List<ServiceReqConfigDTO> getServiceReqConfigs() {
		List<ServiceReqConfig> serviceReqConfigs = serviceReqConfigRepo.findAll();

		return convertEntitiesToDTOs(serviceReqConfigs);
	}

	@Override
	public Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize) {
		PageRequest pageRequest = new PageRequest(pageNum, pageSize);
		Iterable<ServiceReqConfig> serviceReqConfigs = serviceReqConfigRepo.findAll(pageRequest);
		return convertEntitiesToDTOs(serviceReqConfigs);
	}

	@Override
	@Transactional
	public void updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) {
		ServiceReqConfig serviceReqConfig = serviceReqConfigRepo.findOne(serviceReqConfigDTO.getId());
		ModelMapper mapper = new ModelMapper();
		List<ServiceReqFormField> fields = new ArrayList<ServiceReqFormField>();

		for (ServiceReqFormFieldDTO f : serviceReqConfigDTO.getFormFields()) {
			ServiceReqFormField onefield = null;
			if(f.getFieldName() == null)
				continue;
			if (f.getId() == null) {
				onefield = new ServiceReqFormField();
				onefield.setServiceReqConfig(serviceReqConfig);
			}else{
				 onefield = serviceReqFormFieldRepo.findOne(f.getId());
				
			}
			 mapper.map(f, onefield);
			fields.add(onefield);
			
		}
		serviceReqConfig.setId(serviceReqConfigDTO.getId());
		serviceReqConfig.setVersion(serviceReqConfigDTO.getVersion());
		serviceReqConfig.setRequestName(serviceReqConfigDTO.getRequestName());
		serviceReqConfig.setRequestType(serviceReqConfigDTO.getRequestType());
		serviceReqConfig.setRequestUnit(serviceReqConfigDTO.getRequestUnit());
		serviceReqConfig.setFormFields(fields);
		serviceReqConfigRepo.save(serviceReqConfig);
	}

	@Override
	public void delServiceReqConfig(Long id) {
		serviceReqConfigRepo.delete(id);
	}

	@Override
	public void addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormFieldDTO) {
		ServiceReqFormField serviceReqFormField = convertFormFieldDTOToEntity(serviceReqFormFieldDTO);
		serviceReqFormFieldRepo.save(serviceReqFormField);
	}

	@Override
	public ServiceReqFormFieldDTO getServiceReqFormField(Long id) {
		ServiceReqFormField serviceReqFormField = serviceReqFormFieldRepo.findOne(id);
		return convertFormFieldEntityToDTO(serviceReqFormField);
	}

	@Override
	public Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields(Long serviceReqConfigId) {
		Iterable<ServiceReqFormField> serviceReqFormFieldList = serviceReqConfigRepo.findOne(serviceReqConfigId)
				.getFormFields();
		return convertFormFieldEntitiesToDTOs(serviceReqFormFieldList);
	}

	@Override
	public Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields() {
		Iterable<ServiceReqFormField> serviceReqFormFields = serviceReqFormFieldRepo.findAll();
		return convertFormFieldEntitiesToDTOs(serviceReqFormFields);
	}

	@Override
	public void updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormFieldDTO) {
		ServiceReqFormField serviceReqFormField = convertFormFieldDTOToEntity(serviceReqFormFieldDTO);
		serviceReqFormFieldRepo.save(serviceReqFormField);
	}

	@Override
	public void delServiceReqFormField(Long id) {

		serviceReqFormFieldRepo.delete(id);
	}

	@Override

	public Page<ServiceReqConfigDTO> getServiceReqConfigs(Pageable pageDetails) {
		Page<ServiceReqConfig> page = serviceReqConfigRepo.findAll(pageDetails);
		List<ServiceReqConfigDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
		Page<ServiceReqConfigDTO> pageImpl = new PageImpl<ServiceReqConfigDTO>(dtOs, pageDetails, t);
		return pageImpl;
	}

	@Override
	public Page<ServiceReqFormFieldDTO> getServiceReqFormFields(Pageable pageDetails) {
		Page<ServiceReqFormField> page = serviceReqFormFieldRepo.findAll(pageDetails);
		List<ServiceReqFormFieldDTO> dtOs = convertFormFieldEntitiesToDTOs(page.getContent());
		return new PageImpl<ServiceReqFormFieldDTO>(dtOs, pageDetails, page.getTotalElements());
	}

	private ServiceReqConfigDTO convertEntityToDTO(ServiceReqConfig serviceReqConfig) {
		PropertyMap<ServiceReqConfig, ServiceReqConfigDTO> mapperConfig = new PropertyMap<ServiceReqConfig, ServiceReqConfigDTO>() {
			@Override
			protected void configure() {
				skip().setFormFields(null);
			}
		};
		modelMapper = new ModelMapper();
		modelMapper.addMappings(mapperConfig);
		return modelMapper.map(serviceReqConfig, ServiceReqConfigDTO.class);
	}

	private ServiceReqConfig convertDTOToEntity(ServiceReqConfigDTO serviceReqConfigDTO) {
		modelMapper = new ModelMapper();
		return modelMapper.map(serviceReqConfigDTO, ServiceReqConfig.class);
	}

	private List<ServiceReqConfigDTO> convertEntitiesToDTOs(Iterable<ServiceReqConfig> serviceReqConfigs) {
		List<ServiceReqConfigDTO> serviceReqConfigDTOList = new ArrayList<>();
		for (ServiceReqConfig serviceReqConfig : serviceReqConfigs) {
			ServiceReqConfigDTO dto = convertEntityToDTO(serviceReqConfig);
			serviceReqConfigDTOList.add(dto);
		}
		return serviceReqConfigDTOList;
	}

	private Iterable<ServiceReqConfigDTO> convertEntitiesToDTOs(Page<ServiceReqConfig> serviceReqConfigs) {
		List<ServiceReqConfigDTO> serviceReqConfigDTOList = new ArrayList<>();
		for (ServiceReqConfig serviceReqConfig : serviceReqConfigs) {
			ServiceReqConfigDTO dto = convertEntityToDTO(serviceReqConfig);
			serviceReqConfigDTOList.add(dto);
		}
		return serviceReqConfigDTOList;
	}

	private ServiceReqFormFieldDTO convertFormFieldEntityToDTO(ServiceReqFormField serviceReqFormField) {
		return modelMapper.map(serviceReqFormField, ServiceReqFormFieldDTO.class);
	}

	private ServiceReqFormField convertFormFieldDTOToEntity(ServiceReqFormFieldDTO serviceReqFormFieldDTO) {
		return modelMapper.map(serviceReqFormFieldDTO, ServiceReqFormField.class);
	}

	private List<ServiceReqFormFieldDTO> convertFormFieldEntitiesToDTOs(
			Iterable<ServiceReqFormField> serviceReqFormFields) {
		List<ServiceReqFormFieldDTO> serviceReqFormFieldDTOList = new ArrayList<>();
		for (ServiceReqFormField serviceReqFormField : serviceReqFormFields) {
			convertFormFieldEntityToDTO(serviceReqFormField);
		}
		return serviceReqFormFieldDTOList;
	}

}
