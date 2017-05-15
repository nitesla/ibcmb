package longbridge.services;

import longbridge.exception.InternetBankingException;
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

    String addServiceReqConfig(ServiceReqConfigDTO serviceReqFormField) throws InternetBankingException;

    ServiceReqConfigDTO getServiceReqConfig(Long id);

    //List<ServiceReqConfigDTO> getServiceReqConfigs();

    Iterable<ServiceReqConfigDTO> getServiceReqConfigs();
    
    Page<ServiceReqConfigDTO> getServiceReqConfigs(Pageable pageDetails);

    List<ServiceReqConfig> getServiceReqConfs();

    Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize);

    String updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfig) throws InternetBankingException;

    String delServiceReqConfig(Long id) throws InternetBankingException;

    String addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField) throws InternetBankingException;

    ServiceReqFormFieldDTO getServiceReqFormField(Long id);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields(Long serviceReqConfigId);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields();

    Page<ServiceReqFormFieldDTO> getServiceReqFormFields(Pageable pageDetails);
    
    String updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField) throws InternetBankingException;

    String delServiceReqFormField(Long id) throws InternetBankingException;

}
