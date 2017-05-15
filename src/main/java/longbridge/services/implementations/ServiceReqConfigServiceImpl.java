package longbridge.services.implementations;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.ServiceReqConfig;
import longbridge.models.ServiceReqFormField;
import longbridge.repositories.ServiceReqConfigRepo;
import longbridge.repositories.ServiceReqFormFieldRepo;
import longbridge.services.ServiceReqConfigService;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Service
public class ServiceReqConfigServiceImpl implements ServiceReqConfigService {

    private ServiceReqConfigRepo serviceReqConfigRepo;
    private ServiceReqFormFieldRepo serviceReqFormFieldRepo;
	private ModelMapper modelMapper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Locale locale = LocaleContextHolder.getLocale();
	@Autowired
	private MessageSource messageSource;

	public ServiceReqConfigServiceImpl(ServiceReqConfigRepo serviceReqConfigRepo,
			ServiceReqFormFieldRepo serviceReqFormFieldRepo) {
		this.serviceReqConfigRepo = serviceReqConfigRepo;
		this.serviceReqFormFieldRepo = serviceReqFormFieldRepo;
	}

	@Override
	@Transactional
	public String addServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) throws InternetBankingException {
		try {
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
			logger.info("Added service request configuration {}", serviceReqConfigDTO.toString());
			return messageSource.getMessage("req.config.add.success", null, locale);
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("reg.config.add.failure",null,locale),e);
		}
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
	public List<ServiceReqConfig> getServiceReqConfs() {
		List<ServiceReqConfig> serviceReqConfigs = serviceReqConfigRepo.findAll();
		return serviceReqConfigs;
	}

	@Override
	public Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize) {
		PageRequest pageRequest = new PageRequest(pageNum, pageSize);
		Iterable<ServiceReqConfig> serviceReqConfigs = serviceReqConfigRepo.findAll(pageRequest);
		return convertEntitiesToDTOs(serviceReqConfigs);
	}

	@Override
	@Transactional
	public String updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) throws InternetBankingException {
		try {
			ServiceReqConfig serviceReqConfig = serviceReqConfigRepo.findOne(serviceReqConfigDTO.getId());
			ModelMapper mapper = new ModelMapper();
			List<ServiceReqFormField> fields = new ArrayList<ServiceReqFormField>();

			for (ServiceReqFormFieldDTO f : serviceReqConfigDTO.getFormFields()) {
				ServiceReqFormField onefield = null;
				if (f.getFieldName() == null)
					continue;
				if (f.getId() == null) {
					onefield = new ServiceReqFormField();
					onefield.setServiceReqConfig(serviceReqConfig);
				} else {
					onefield = serviceReqFormFieldRepo.findOne(f.getId());

				}
				mapper.map(f, onefield);
				fields.add(onefield);

			}
			serviceReqConfig.setId(serviceReqConfigDTO.getId());
			serviceReqConfig.setVersion(serviceReqConfigDTO.getVersion());
			serviceReqConfig.setRequestName(serviceReqConfigDTO.getRequestName());
			serviceReqConfig.setRequestType(serviceReqConfigDTO.getRequestType());
			serviceReqConfig.setGroupId(serviceReqConfigDTO.getGroupId());
			serviceReqConfig.setFormFields(fields);
			serviceReqConfigRepo.save(serviceReqConfig);
			logger.info("Updated service request configuration {}", serviceReqConfig.toString());
			return messageSource.getMessage("req.config.update.success", null, locale);

		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("req.config.update.failure",null,locale),e);
		}
	}

	@Override
	public String delServiceReqConfig(Long id) throws InternetBankingException {
		try {
			serviceReqConfigRepo.delete(id);
			logger.warn("Deleted service request configuration with Id {}", id);
			return messageSource.getMessage("req.config.delete.success", null, locale);
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("req.config.delete.failure",null,locale),e);
		}
	}

	@Override
	@Transactional
	public String addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormFieldDTO) throws InternetBankingException {
		try {
			ServiceReqFormField serviceReqFormField = convertFormFieldDTOToEntity(serviceReqFormFieldDTO);
			serviceReqFormFieldRepo.save(serviceReqFormField);
			logger.info("Added service request form fields {}", serviceReqFormField.toString());
			return messageSource.getMessage("req.config.add.success", null, locale);
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("req.config.add.failure",null,locale),e);
		}
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
	@Transactional
	public String updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormFieldDTO) throws InternetBankingException {
		try {
			ServiceReqFormField serviceReqFormField = convertFormFieldDTOToEntity(serviceReqFormFieldDTO);
			serviceReqFormFieldRepo.save(serviceReqFormField);
			logger.info("Updated service request form fields {}", serviceReqFormField.toString());
			return messageSource.getMessage("req.config.update.success", null, locale);
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("req.config.update.failure",null,locale),e);
		}
	}

	@Override
	public String delServiceReqFormField(Long id) throws InternetBankingException {
		try{
		serviceReqFormFieldRepo.delete(id);
		logger.info("Deleted service request form fields with Id {}",id);
		return  messageSource.getMessage("req.config.delete.success",null,locale);

	}
	catch (Exception e){
		throw new InternetBankingException(messageSource.getMessage("req.config.delete.failure",null,locale));
		}
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
