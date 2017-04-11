package longbridge.services;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Showboy on 08/04/2017.
 */
public interface ServiceReqConfigService {

    void addSeviceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    ServiceReqConfigDTO getServiceReqConfig(Long id);

    //List<ServiceReqConfigDTO> getServiceReqConfigs();

    Iterable<ServiceReqConfigDTO> getServiceReqConfigs();
    
    Page<ServiceReqConfigDTO> getServiceReqConfigs(Pageable pageDetails);

    void updateServiceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    void delServiceReqConfig(Long id);

    void addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    ServiceReqFormFieldDTO getServiceReqFormField(Long id);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields();

    Page<ServiceReqFormFieldDTO> getServiceReqFormFields(Pageable pageDetails);
    
    void updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    void delServiceReqFormField(Long id);

}
