package longbridge.services.implementations;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.SRConfig;
import longbridge.models.ServiceReqFormField;
import longbridge.repositories.ServiceReqConfigRepo;
import longbridge.repositories.ServiceReqFormFieldRepo;
import longbridge.services.ServiceReqConfigService;
import longbridge.utils.Verifiable;
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

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Service
public class ServiceReqConfigServiceImpl implements ServiceReqConfigService {

    private final ServiceReqConfigRepo serviceReqConfigRepo;
    private final ServiceReqFormFieldRepo serviceReqFormFieldRepo;
	private ModelMapper modelMapper;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Locale locale = LocaleContextHolder.getLocale();

	@Autowired
	private MessageSource messageSource;


	@Autowired
	private EntityManager entityManager;

	public ServiceReqConfigServiceImpl(ServiceReqConfigRepo serviceReqConfigRepo,
			ServiceReqFormFieldRepo serviceReqFormFieldRepo) {
		this.serviceReqConfigRepo = serviceReqConfigRepo;
		this.serviceReqFormFieldRepo = serviceReqFormFieldRepo;
	}

	@Override
	@Transactional
	@Verifiable(operation="ADD_SERV_REQ_CONFIG",description="Adding a Service Request Configuration")
	public String addServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) throws InternetBankingException {
		try {
			SRConfig SRConfig = convertDTOToEntity(serviceReqConfigDTO);
			Iterator<ServiceReqFormField> serviceReqFormFieldIterator = SRConfig.getFormFields().iterator();

			while (serviceReqFormFieldIterator.hasNext()) {
				ServiceReqFormField serviceReqFormField = serviceReqFormFieldIterator.next();
				if (serviceReqFormField.getFieldName() == null) {
					serviceReqFormFieldIterator.remove();
				} else {
					serviceReqFormField.setSRConfig(SRConfig);
				}
			}
			serviceReqConfigRepo.save(SRConfig);
			logger.info("Added service request configuration");
			return messageSource.getMessage("req.config.add.success", null, locale);
		}
		catch (VerificationInterruptedException e) {
			return e.getMessage();
		}

		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("reg.config.add.failure",null,locale),e);
		}
	}

//	@Override
//	public ServiceReqConfigDTO getServiceReqConfig(Long id) {
//		SRConfig SRConfig = serviceReqConfigRepo.findById(id).get();
//		modelMapper = new ModelMapper();
//		return modelMapper.map(SRConfig, ServiceReqConfigDTO.class);
//	}

    @Override
    public ServiceReqConfigDTO getServiceReqConfig(Long id) {
        logger.info("Passed id {}", id);
        Optional<SRConfig> sRConfig = serviceReqConfigRepo.findById(id);
        if(!sRConfig.isPresent()) throw new InternetBankingException("No service request for id: "+id);
        modelMapper = new ModelMapper();
        return modelMapper.map(sRConfig.get(), ServiceReqConfigDTO.class);
    }

	@Override
	public List<ServiceReqConfigDTO> getServiceReqConfigs() {
		List<SRConfig> SRConfigs = serviceReqConfigRepo.findAll();
		return convertEntitiesToDTOs(SRConfigs);
	}

	@Override
	public List<SRConfig> getServiceReqConfs() {
        return serviceReqConfigRepo.findAll();
	}

	@Override
	public Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize) {
//		PageRequest pageRequest = new PageRequest(pageNum, pageSize);
		PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
		Iterable<SRConfig> serviceReqConfigs = serviceReqConfigRepo.findAll(pageRequest);
		return convertEntitiesToDTOs(serviceReqConfigs);
	}

	@Override
	@Transactional
	@Verifiable(operation="UPDATE_SERV_REQ_CONFIG",description="Updating a Service Request Configuration")
	public String updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) throws InternetBankingException {
		try {
			SRConfig SRConfig = serviceReqConfigRepo.findById(serviceReqConfigDTO.getId()).get();
			ModelMapper mapper = new ModelMapper();
			List<ServiceReqFormField> fields = new ArrayList<>();



			for (ServiceReqFormFieldDTO f : serviceReqConfigDTO.getFormFields()) {
				ServiceReqFormField onefield = null;
				if (f.getFieldName() == null)
					continue;
				if (f.getId() == null) {
					onefield = new ServiceReqFormField();
					onefield.setSRConfig(SRConfig);

				} else {
					onefield = serviceReqFormFieldRepo.findById(f.getId()).get();
					entityManager.detach(onefield);
				}
				mapper.map(f, onefield);
				fields.add(onefield);

			}

			entityManager.detach(SRConfig);
			SRConfig.setId(serviceReqConfigDTO.getId());
			SRConfig.setVersion(serviceReqConfigDTO.getVersion());
			SRConfig.setRequestName(serviceReqConfigDTO.getRequestName());
			SRConfig.setAuthenticate(serviceReqConfigDTO.isAuthenticate());
			SRConfig.setRequestType(serviceReqConfigDTO.getRequestType());
			SRConfig.setGroupId(serviceReqConfigDTO.getGroupId());
			SRConfig.setFormFields(fields);
			serviceReqConfigRepo.save(SRConfig);
			logger.info("Updated service request configuration {}", SRConfig.toString());
			return messageSource.getMessage("req.config.update.success", null, locale);

		}
		catch (VerificationInterruptedException e) {
			return e.getMessage();
		}
		catch (InternetBankingException e){
			throw e;
		}
		catch (Exception e){
			throw new InternetBankingException(messageSource.getMessage("req.config.update.failure",null,locale),e);
		}
	}

	@Override
	@Verifiable(operation="DELETE_SERV_REQ_CONFIG",description="Deleting a Service Request Configuration")
	public String delServiceReqConfig(Long id) throws InternetBankingException {
		try {
			SRConfig srConfig = serviceReqConfigRepo.findById(id).get();
			SRConfig config = new SRConfig();
			modelMapper.map(srConfig,config);
			serviceReqConfigRepo.delete(config);
			logger.warn("Deleted service request configuration with Id {}", id);
			return messageSource.getMessage("req.config.delete.success", null, locale);
		}
		catch (VerificationInterruptedException e) {
			return e.getMessage();
		}
		catch (InternetBankingException e){
			throw e;
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
		ServiceReqFormField serviceReqFormField = serviceReqFormFieldRepo.findById(id).get();
		return convertFormFieldEntityToDTO(serviceReqFormField);
	}

	@Override
	public Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields(Long serviceReqConfigId) {
		Iterable<ServiceReqFormField> serviceReqFormFieldList = serviceReqConfigRepo.findById(serviceReqConfigId).get()
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
		serviceReqFormFieldRepo.deleteById(id);
		logger.info("Deleted service request form fields with Id {}",id);
		return  messageSource.getMessage("req.config.delete.success",null,locale);

	}
	catch (Exception e){
		throw new InternetBankingException(messageSource.getMessage("req.config.delete.failure",null,locale));
		}
	}

	@Override
	public Page<ServiceReqConfigDTO> getServiceReqConfigs(Pageable pageDetails) {
		Page<SRConfig> page = serviceReqConfigRepo.findAll(pageDetails);
		System.out.println("srrrr"+page.getContent());
		List<ServiceReqConfigDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
        return new PageImpl<ServiceReqConfigDTO>(dtOs, pageDetails, t);
	}

	@Override
	public Page<ServiceReqFormFieldDTO> getServiceReqFormFields(Pageable pageDetails) {
		Page<ServiceReqFormField> page = serviceReqFormFieldRepo.findAll(pageDetails);
		List<ServiceReqFormFieldDTO> dtOs = convertFormFieldEntitiesToDTOs(page.getContent());
		return new PageImpl<>(dtOs, pageDetails, page.getTotalElements());
	}

	private ServiceReqConfigDTO convertEntityToDTO(SRConfig SRConfig) {
		PropertyMap<SRConfig, ServiceReqConfigDTO> mapperConfig = new PropertyMap<>() {
			@Override
			protected void configure() {
				skip().setFormFields(null);
			}
		};
		modelMapper = new ModelMapper();
//		modelMapper.addMappings(mapperConfig);
		return modelMapper.map(SRConfig, ServiceReqConfigDTO.class);
	}

	private SRConfig convertDTOToEntity(ServiceReqConfigDTO serviceReqConfigDTO) {
		modelMapper = new ModelMapper();
		return modelMapper.map(serviceReqConfigDTO, SRConfig.class);
	}

	private List<ServiceReqConfigDTO> convertEntitiesToDTOs(Iterable<SRConfig> serviceReqConfigs) {
		List<ServiceReqConfigDTO> serviceReqConfigDTOList = new ArrayList<>();
		for (SRConfig SRConfig : serviceReqConfigs) {
			ServiceReqConfigDTO dto = convertEntityToDTO(SRConfig);
			serviceReqConfigDTOList.add(dto);
		}
		return serviceReqConfigDTOList;
	}

	private Iterable<ServiceReqConfigDTO> convertEntitiesToDTOs(Page<SRConfig> serviceReqConfigs) {
		List<ServiceReqConfigDTO> serviceReqConfigDTOList = new ArrayList<>();
		for (SRConfig SRConfig : serviceReqConfigs) {
			ServiceReqConfigDTO dto = convertEntityToDTO(SRConfig);
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

	@Override
	public ServiceReqConfigDTO getServiceReqConfigRequestName(String requestName) {
		SRConfig srconfig = serviceReqConfigRepo.findByRequestName(requestName);
		if(srconfig != null) {
			logger.info("the service requestName is{}",srconfig.getFormFields());
			modelMapper = new ModelMapper();
			return modelMapper.map(srconfig, ServiceReqConfigDTO.class);
		}else {
			return new ServiceReqConfigDTO();
		}
	}





}
