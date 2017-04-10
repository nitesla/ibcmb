package longbridge.services.implementations;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.models.ServiceReqConfig;
import longbridge.models.ServiceReqFormField;
import longbridge.repositories.ServiceReqConfigRepo;
import longbridge.repositories.ServiceReqFormFieldRepo;
import longbridge.services.ServiceReqConfigService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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


    @Autowired
    private ModelMapper modelMapper;

    public ServiceReqConfigServiceImpl(ServiceReqConfigRepo serviceReqConfigRepo, ServiceReqFormFieldRepo serviceReqFormFieldRepo) {
        this.serviceReqConfigRepo = serviceReqConfigRepo;
        this.serviceReqFormFieldRepo = serviceReqFormFieldRepo;
    }

    @Override
    @Transactional
    public void addSeviceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) {
        ServiceReqConfig serviceReqConfig = convertDTOToEntity(serviceReqConfigDTO);
        Iterator<ServiceReqFormField> serviceReqFormFieldIterator =  serviceReqConfig.getFormFields().iterator();

        while (serviceReqFormFieldIterator.hasNext()){
            ServiceReqFormField serviceReqFormField = serviceReqFormFieldIterator.next();
            if(serviceReqFormField.getFieldName()==null){
                serviceReqFormFieldIterator.remove();
            }
            else{
                serviceReqFormField.setServiceReqConfig(serviceReqConfig);
            }
        }
        serviceReqConfigRepo.save(serviceReqConfig);

    }


    @Override
    public ServiceReqConfigDTO getServiceReqConfig(Long id) {
        ServiceReqConfig serviceReqConfig =serviceReqConfigRepo.findOne(id);

        return  convertEntityToDTO(serviceReqConfig);
    }

    @Override
    public Iterable<ServiceReqConfigDTO> getServiceReqConfigs() {
        Iterable<ServiceReqConfig> serviceReqConfigs = serviceReqConfigRepo.findAll();
        return convertEntitiesToDTOs(serviceReqConfigs);
    }

    @Override
    public void updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfigDTO) {
        ServiceReqConfig serviceReqConfig = convertDTOToEntity(serviceReqConfigDTO);
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


    private ServiceReqConfigDTO convertEntityToDTO(ServiceReqConfig serviceReqConfig){
        return  modelMapper.map(serviceReqConfig,ServiceReqConfigDTO.class);
    }

    private ServiceReqConfig convertDTOToEntity(ServiceReqConfigDTO serviceReqConfigDTO){
        return  modelMapper.map(serviceReqConfigDTO,ServiceReqConfig.class);
    }

    private Iterable<ServiceReqConfigDTO> convertEntitiesToDTOs(Iterable<ServiceReqConfig> serviceReqConfigs){
        List<ServiceReqConfigDTO> serviceReqConfigDTOs = new ArrayList<>();
        for(ServiceReqConfig serviceReqConfig: serviceReqConfigs){
            ServiceReqConfigDTO serviceReqConfigDTO =  modelMapper.map(serviceReqConfig,ServiceReqConfigDTO.class);
        }
        return serviceReqConfigDTOs;
    }


    private ServiceReqFormFieldDTO convertFormFieldEntityToDTO(ServiceReqFormField serviceReqFormField){
        return  modelMapper.map(serviceReqFormField,ServiceReqFormFieldDTO.class);
    }

    private ServiceReqFormField convertFormFieldDTOToEntity(ServiceReqFormFieldDTO serviceReqFormFieldDTO){
        return  modelMapper.map(serviceReqFormFieldDTO,ServiceReqFormField.class);
    }

    private Iterable<ServiceReqFormFieldDTO> convertFormFieldEntitiesToDTOs(Iterable<ServiceReqFormField> serviceReqFormFields){
        List<ServiceReqFormFieldDTO> serviceReqFormFieldDTOs = new ArrayList<>();
        for(ServiceReqFormField serviceReqFormField: serviceReqFormFields){
            convertFormFieldEntityToDTO(serviceReqFormField);
        }
        return serviceReqFormFieldDTOs;
    }


}
