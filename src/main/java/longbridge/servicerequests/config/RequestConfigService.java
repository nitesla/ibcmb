package longbridge.servicerequests.config;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestConfigService {

    RequestConfig add(AddRequestConfigCmd config);

    RequestConfig getRequestConfig(Long id);

    RequestConfig getRequestConfigByName(String name);


    Page<RequestConfig> getRequestConfigs(Pageable pageDetails);

    List<RequestConfig> getRequestConfigs();

    Page<RequestConfig> getRequestConfigs(String pattern, Pageable pageDetails);

    RequestConfig updateRequestConfig(UpdateRequestConfigCmd config);


}
