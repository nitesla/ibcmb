package longbridge.services;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import org.springframework.data.domain.Page;

/**
 * Created by Wunmi on 08/04/2017.
 */
public interface ServiceReqConfigService {

    void addServiceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    ServiceReqConfigDTO getServiceReqConfig(Long id);

    Iterable<ServiceReqConfigDTO> getServiceReqConfigs();

    Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize);

    void updateServiceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    void delServiceReqConfig(Long id);

    void addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    ServiceReqFormFieldDTO getServiceReqFormField(Long id);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields(Long serviceReqConfigId);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields();

    void updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    void delServiceReqFormField(Long id);

}
