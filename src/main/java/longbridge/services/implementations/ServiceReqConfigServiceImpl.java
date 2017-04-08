package longbridge.services.implementations;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.services.ServiceReqConfigService;
import org.springframework.stereotype.Service;

/**
 * Created by Showboy on 08/04/2017.
 */
@Service
public class ServiceReqConfigServiceImpl implements ServiceReqConfigService {
    @Override
    public void addSeviceReqConfig(ServiceReqConfigDTO serviceReqConfig) {

    }

    @Override
    public ServiceReqConfigDTO getServiceReqConfig(Long id) {
        return null;
    }

    @Override
    public Iterable<ServiceReqConfigDTO> getServiceReqConfigs() {
        return null;
    }

    @Override
    public void updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfig) {

    }

    @Override
    public void delServiceReqConfig(Long id) {

    }
}
