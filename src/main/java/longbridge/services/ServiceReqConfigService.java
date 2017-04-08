package longbridge.services;

import longbridge.dtos.ServiceReqConfigDTO;

/**
 * Created by Showboy on 08/04/2017.
 */
public interface ServiceReqConfigService {

    void addSeviceReqConfig(ServiceReqConfigDTO serviceReqConfig);

    ServiceReqConfigDTO getServiceReqConfig(Long id);

    Iterable<ServiceReqConfigDTO> getServiceReqConfigs();

    void updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfig);

    void delServiceReqConfig(Long id);

}
