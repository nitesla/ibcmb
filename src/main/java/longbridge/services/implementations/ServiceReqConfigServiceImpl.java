package longbridge.services.implementations;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.repositories.ServiceReqConfigRepo;
import longbridge.services.ServiceReqConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Showboy on 08/04/2017.
 */
@Service
public class ServiceReqConfigServiceImpl implements ServiceReqConfigService {

    @Autowired
    private ServiceReqConfigRepo serviceReqConfigRepo;

    @Override
    @Transactional
    public void addSeviceReqConfig(ServiceReqConfigDTO serviceReqConfig) {

        //this.serviceReqConfigRepo.save(serviceReqConfig);
        //System.out.println(serviceReqConfig.toString());
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
