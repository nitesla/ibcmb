package longbridge.services;

import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceReqFormFieldDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.SRConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by Wunmi on 08/04/2017.
 */
public interface ServiceReqConfigService{

    @PreAuthorize("hasAuthority('ADD_SERV_REQ_CONFIG')")
    String addServiceReqConfig(ServiceReqConfigDTO serviceReqFormField) throws InternetBankingException;

    @PreAuthorize("hasAuthority('GET_SERV_REQ_CONFIG')")
    ServiceReqConfigDTO getServiceReqConfig(Long id);

    @PreAuthorize("hasAuthority('GET_SERV_REQ_CONFIG')")
    Iterable<ServiceReqConfigDTO> getServiceReqConfigs();

    @PreAuthorize("hasAuthority('GET_SERV_REQ_CONFIG')")
    Page<ServiceReqConfigDTO> getServiceReqConfigs(Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_SERV_REQ_CONFIG')")
    List<SRConfig> getServiceReqConfs();

    Iterable<ServiceReqConfigDTO> gerServiceReqConfigsPage(Integer pageNum, Integer pageSize);

    @PreAuthorize("hasAuthority('UPDATE_SERV_REQ_CONFIG')")
    String updateServiceReqConfig(ServiceReqConfigDTO serviceReqConfig) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_SERV_REQ_CONFIG')")
    String delServiceReqConfig(Long id) throws InternetBankingException;

    String addServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField) throws InternetBankingException;

    ServiceReqFormFieldDTO getServiceReqFormField(Long id);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields(Long serviceReqConfigId);

    Iterable<ServiceReqFormFieldDTO> getServiceReqFormFields();

    Page<ServiceReqFormFieldDTO> getServiceReqFormFields(Pageable pageDetails);
    
    String updateServiceReqFormField(ServiceReqFormFieldDTO serviceReqFormField) throws InternetBankingException;

    String delServiceReqFormField(Long id) throws InternetBankingException;

}
