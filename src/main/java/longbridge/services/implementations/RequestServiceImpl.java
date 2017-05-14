package longbridge.services.implementations;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.Email;
import longbridge.models.RequestHistory;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.repositories.RequestHistoryRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.ServiceRequestRepo;
import longbridge.services.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/7/2017.
 */

@Service
public class RequestServiceImpl implements RequestService {

    private ServiceRequestRepo serviceRequestRepo;
    private RequestHistoryRepo requestHistoryRepo;

    @Autowired
    private ServiceReqConfigService reqConfigService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    UserGroupMessageService groupMessageService;

    @Autowired
    private RetailUserRepo retailUserRepo;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    MessageSource messageSource;

    @Autowired
    OperationsUserService operationsUserService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    public RequestServiceImpl(ServiceRequestRepo serviceRequestRepo, RequestHistoryRepo requestHistoryRepo) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.requestHistoryRepo = requestHistoryRepo;
    }

    private String getFullName(ServiceRequest serviceRequest) {
        String firstName = serviceRequest.getUser().getFirstName();
        String lastName = "";
        if (serviceRequest.getUser().getLastName() == null) {
            lastName = "";
        } else {
            lastName = serviceRequest.getUser().getLastName();
        }
        String name = firstName + ' ' + lastName;
        return name;
    }

    @Override
    @Transactional
    public String addRequest(ServiceRequestDTO request) throws InternetBankingException {
       try {
           ServiceRequest serviceRequest = convertDTOToEntity(request);
           serviceRequest.setUser(retailUserRepo.findOne(serviceRequest.getUser().getId()));

           String name = getFullName(serviceRequest);
           ServiceReqConfigDTO config = reqConfigService.getServiceReqConfig(serviceRequest.getServiceReqConfigId());
           String body = serviceRequest.getBody();//TODO format body
           serviceRequestRepo.save(serviceRequest);

           Email email = new Email.Builder().setSender("info@ibanking.coronationmb.com")
                   .setSubject("Service Request from " + name)
                   .setBody(body)
                   .build();
           groupMessageService.send(config.getGroupId(), email);
           return messageSource.getMessage("request.add.success", null, locale);
       }
       catch (Exception e){
           throw new InternetBankingException(messageSource.getMessage("req.add.failure",null,locale),e);
       }
    }


    @Override
    public ServiceRequestDTO getRequest(Long id) {
        ServiceRequest serviceRequest = serviceRequestRepo.findOne(id);
        return convertEntityToDTO(serviceRequest);
    }

    @Override
    public Iterable<ServiceRequestDTO> getRequests(RetailUser user) {
        Iterable<ServiceRequest> requests = serviceRequestRepo.findByUser(user);
        return convertEntitiesToDTOs(requests);
    }

    @Override
    @Transactional
    public String addRequestHistory(RequestHistoryDTO requestHistoryDTO) throws InternetBankingException {
        try {
            RequestHistory requestHistory = convertRequestHistoryDTOToEntity(requestHistoryDTO);
            ServiceRequest serviceRequest = requestHistory.getServiceRequest();
            serviceRequest.setRequestStatus(requestHistory.getStatus());
            serviceRequestRepo.save(serviceRequest);
            requestHistoryRepo.save(requestHistory);
            return messageSource.getMessage("req.hist.update.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("req.hist.update.failure",null,locale),e);
        }
    }

    @Override
    @Transactional
    public Iterable<RequestHistory> getRequestHistories(ServiceRequest request) {
        return serviceRequestRepo.findOne(request.getId()).getRequestHistories();
    }

    @Override
    @Transactional
    public Iterable<RequestHistoryDTO> getRequestHistories(Long serviceRequestId) {
        Iterable<RequestHistory> requestHistories = serviceRequestRepo.findOne(serviceRequestId).getRequestHistories();
        return convertRequestHistoryEntitiesToDTOs(requestHistories);
    }

    public Page<ServiceRequestDTO> getRequests(Pageable pageDetails) {
        Page<ServiceRequest> page = serviceRequestRepo.findAll(pageDetails);
        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<ServiceRequestDTO> pageImpl = new PageImpl<>(dtOs, pageDetails, t);
        return pageImpl;
    }


//    @Override
//	public Page<ServiceRequestDTO> getRequests(ServiceRequestDTO request, Pageable pageDetails) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//    @Override
//    public Page<RequestHistoryDTO> getRequestHistories(Long serviceRequestId, Pageable pageDetails) {
//      //TODO will be implemented later
//        return
//    }


    private ServiceRequestDTO convertEntityToDTO(ServiceRequest serviceRequest) {
        ServiceRequestDTO requestDTO = modelMapper.map(serviceRequest, ServiceRequestDTO.class);
        requestDTO.setUsername(serviceRequest.getUser().getUserName());
        return requestDTO;

    }

    private ServiceRequest convertDTOToEntity(ServiceRequestDTO ServiceRequestDTO) {
        return modelMapper.map(ServiceRequestDTO, ServiceRequest.class);
    }

    private List<ServiceRequestDTO> convertEntitiesToDTOs(Iterable<ServiceRequest> serviceRequests) {
        List<ServiceRequestDTO> serviceRequestDTOList = new ArrayList<>();
        for (ServiceRequest serviceRequest : serviceRequests) {
            ServiceRequestDTO requestDTO = convertEntityToDTO(serviceRequest);
            requestDTO.setUsername(serviceRequest.getUser().getUserName());
            requestDTO.setDate(serviceRequest.getDateRequested().toString());
            String status = codeService.getByTypeAndCode("REQUEST_STATUS", serviceRequest.getRequestStatus()).getDescription();
            requestDTO.setRequestStatus(status);
            serviceRequestDTOList.add(requestDTO);
        }
        return serviceRequestDTOList;
    }


    private RequestHistoryDTO convertRequestHistoryEntityToDTO(RequestHistory requestHistory) {
        return modelMapper.map(requestHistory, RequestHistoryDTO.class);
    }

    private RequestHistory convertRequestHistoryDTOToEntity(RequestHistoryDTO requestHistoryDTO) {
        RequestHistory requestHistory = new RequestHistory();
        requestHistory.setServiceRequest(serviceRequestRepo.findOne(Long.parseLong(requestHistoryDTO.getServiceRequestId())));
        requestHistory.setComment(requestHistoryDTO.getComment());
        requestHistory.setStatus(requestHistoryDTO.getStatus());
        requestHistory.setCreatedBy(operationsUserService.getUserByName(requestHistoryDTO.getCreatedBy()));
        requestHistory.setCreatedOn(new Date());
        return requestHistory;
    }

    private List<RequestHistoryDTO> convertRequestHistoryEntitiesToDTOs(Iterable<RequestHistory> requestHistories) {
        List<RequestHistoryDTO> requestHistoryList = new ArrayList<>();
        for (RequestHistory requestHistory : requestHistories) {
            RequestHistoryDTO requestDTO = new RequestHistoryDTO();
            requestDTO.setId(requestHistory.getId());
            String status = codeService.getByTypeAndCode("REQUEST_STATUS", requestHistory.getStatus()).getDescription();
            requestDTO.setStatus(status);
            requestDTO.setComment(requestHistory.getComment());
            requestDTO.setCreatedBy(requestHistory.getCreatedBy().getUserName());
            requestDTO.setCreatedOn(requestHistory.getCreatedOn().toString());
            requestDTO.setServiceRequestId(requestHistory.getServiceRequest().getId().toString());
            requestHistoryList.add(requestDTO);
        }
        return requestHistoryList;
    }
}
