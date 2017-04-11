package longbridge.services;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;

import java.util.List;

/**
 * Created by Showboy on 08/04/2017.
 */
public interface ServiceReqConfigService {

    void addSeviceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    ServiceReqConfigDTO getServiceReqConfig(Long id);

    List<ServiceReqConfigDTO> getServiceReqConfigs();

    void updateServiceReqConfig(ServiceReqConfigDTO serviceReqFormField);

    void delServiceReqConfig(Long id);




    void addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    ServiceReqFormFieldDTO getServiceReqFormField(Long id);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields();

    void updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField);

    void delServiceReqFormField(Long id);

}
