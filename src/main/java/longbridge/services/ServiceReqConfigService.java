package longbridge.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.models.ServiceReqConfig;

import java.util.List;

/**
 * Created by Wunmi on 08/04/2017.
 */
public interface ServiceReqConfigService{

    void addServiceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    ServiceReqConfigDTO getServiceReqConfig(Long id);

    //List<ServiceReqConfigDTO> getServiceReqConfigs();

    Iterable<ServiceReqConfigDTO> getServiceReqConfigs();
    
    Page<ServiceReqConfigDTO> getServiceReqConfigs(Pageable pageDetails);

    List<ServiceReqConfig> getServiceReqConfs();

    Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize);

    void updateServiceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    void delServiceReqConfig(Long id);

    void addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    ServiceReqFormFieldDTO getServiceReqFormField(Long id);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields(Long serviceReqConfigId);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields();

    Page<ServiceReqFormFieldDTO> getServiceReqFormFields(Pageable pageDetails);
    
    void updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    void delServiceReqFormField(Long id);

}
