package longbridge.services.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.NameValue;
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

import java.util.*;

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
    private CorporateRepo corporateRepo;

    @Autowired
    private CodeService codeService;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private OperationsUserService operationsUserService;

    @Autowired
    private UserGroupRepo userGroupRepo;

    @Autowired
    private ServiceReqConfigRepo reqConfigRepo;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Locale locale = LocaleContextHolder.getLocale();


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
    public String addRequest(ServiceRequestDTO request) throws InternetBankingException {
        try {
            ServiceRequest serviceRequest = convertDTOToEntity(request);
            serviceRequest.setUser(retailUserRepo.findOne(request.getUserId()));
            String name = getFullName(serviceRequest);
            ServiceReqConfigDTO config = reqConfigService.getServiceReqConfig(request.getServiceReqConfigId());

            //***///
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(serviceRequest.getBody(), new TypeReference<ArrayList<NameValue>>() {
            });

            StringBuilder messageBody = new StringBuilder();
            for(NameValue nameValue : myFormObjects){
                messageBody.append(nameValue.getName() + " : " + nameValue.getValue() + "\n");
            }

            String message = messageBody.toString();
            serviceRequestRepo.save(serviceRequest);

            Email email = new Email.Builder()
                    .setSubject(String.format(messageSource.getMessage("request.subject",null,locale),name))
                    .setBody(message)
                    .build();
            if(config.getGroupId()!=null) {
                groupMessageService.send(config.getGroupId(), email);
            }
            return messageSource.getMessage("req.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("req.add.failure", null, locale), e);
        }
    }

    @Override
    public String addCorpRequest(ServiceRequestDTO request) throws InternetBankingException {
        try {
            ServiceRequest serviceRequest = convertDTOToEntity(request);
            serviceRequest.setCorporate(corporateRepo.findOne(request.getCorporate().getId()));
            String name = request.getCorporate().getName();
            ServiceReqConfigDTO config = reqConfigService.getServiceReqConfig(serviceRequest.getServiceReqConfigId());

            //***///
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(serviceRequest.getBody(), new TypeReference<ArrayList<NameValue>>() {
            });

            StringBuilder messageBody = new StringBuilder();
            for(NameValue nameValue : myFormObjects){
                messageBody.append(nameValue.getName() + " : " + nameValue.getValue() + "\n");
            }

            String message = messageBody.toString();
            serviceRequestRepo.save(serviceRequest);

            Email email = new Email.Builder().setSender("info@ibanking.coronationmb.com")
                    .setSubject("Service Request from " + name)
                    .setBody(message)
                    .build();
            groupMessageService.send(config.getGroupId(), email);
            return messageSource.getMessage("request.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("req.add.failure", null, locale), e);
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
            return messageSource.getMessage("req.hist.update.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("req.hist.update.failure", null, locale), e);
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

    public Page<ServiceRequestDTO> getRequests(RetailUser user, Pageable pageDetails) {
        Page<ServiceRequest> page = serviceRequestRepo.findAllByUserOrderByDateRequestedDesc(user, pageDetails);
        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<ServiceRequestDTO> pageImpl = new PageImpl<>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public Page<ServiceRequestDTO> getRequests(Corporate corporate, Pageable pageDetails) {
        Page<ServiceRequest> page = serviceRequestRepo.findAllByCorporateOrderByDateRequestedDesc(corporate, pageDetails);
        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<ServiceRequestDTO> pageImpl = new PageImpl<>(dtOs, pageDetails, t);
        return pageImpl;
    }

    public Page<ServiceRequestDTO> getRequests(OperationsUser opsUser, Pageable pageDetails) {
        Page<ServiceRequest> page = serviceRequestRepo.findAllByOrderByDateRequestedDesc(pageDetails);
        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        List<ServiceRequestDTO> requestsForOpsUser = new ArrayList<ServiceRequestDTO>();
        List<UserGroup> opsUserGroups = opsUser.getGroups();
        for (ServiceRequestDTO request : dtOs) {
            SRConfig reqConfig = reqConfigRepo.findOne(request.getServiceReqConfigId());
            for (UserGroup group : opsUserGroups) {
                if (group.equals(userGroupRepo.findOne(reqConfig.getGroupId()))) {
                    requestsForOpsUser.add(request);
                }
            }
        }
        long t = page.getTotalElements();
        Page<ServiceRequestDTO> pageImpl = new PageImpl<>(requestsForOpsUser, pageDetails, t);
        return pageImpl;
    }

    public int getNumOfUnattendedRequests2(OperationsUser opsUser) {
        int count=0;
        List<ServiceRequest> serviceRequests = serviceRequestRepo.findByRequestStatus("S");
        List<UserGroup> opsUserGroups = opsUser.getGroups();
        for (ServiceRequest request : serviceRequests) {
            SRConfig reqConfig = reqConfigRepo.findOne(request.getServiceReqConfigId());

            if (reqConfig != null) {
                for (UserGroup group : opsUserGroups) {
                    if (group.getId().equals(reqConfig.getGroupId())) {
                        count += 1;
                    }
                }
            }
        }

        return count;
    }

    @Override
    public int getNumOfUnattendedRequests(OperationsUser opsUser) {
        int count=0;
        for(UserGroup ug : opsUser.getGroups()){
        	Integer cnt = serviceRequestRepo.countRequestForStatus("S", ug.getId());
        	count += cnt ;
        }
        return count;
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
        if (serviceRequest.getUser() != null){
            requestDTO.setUsername(serviceRequest.getUser().getUserName());
        }else if (serviceRequest.getCorporate() != null){
            requestDTO.setCorpName(serviceRequest.getCorporate().getName());
        }
        requestDTO.setDate(DateFormatter.format(serviceRequest.getDateRequested()));
        Code code = codeService.getByTypeAndCode("REQUEST_STATUS", serviceRequest.getRequestStatus());
        if (code != null) {
            String status = code.getDescription();
            requestDTO.setRequestStatus(status);
        }
        return requestDTO;

    }

    private ServiceRequest convertDTOToEntity(ServiceRequestDTO ServiceRequestDTO) {
        return modelMapper.map(ServiceRequestDTO, ServiceRequest.class);
    }

    private List<ServiceRequestDTO> convertEntitiesToDTOs(Iterable<ServiceRequest> serviceRequests) {
        List<ServiceRequestDTO> serviceRequestDTOList = new ArrayList<>();
        for (ServiceRequest serviceRequest : serviceRequests) {
            ServiceRequestDTO requestDTO = convertEntityToDTO(serviceRequest);
            serviceRequestDTOList.add(requestDTO);
        }
        return serviceRequestDTOList;
    }


    private RequestHistoryDTO convertRequestHistoryEntityToDTO(RequestHistory requestHistory) {
        RequestHistoryDTO requestDTO = new RequestHistoryDTO();
        requestDTO.setId(requestHistory.getId());
        String status = codeService.getByTypeAndCode("REQUEST_STATUS", requestHistory.getStatus()).getDescription();
        requestDTO.setStatus(status);
        requestDTO.setComment(requestHistory.getComments());
        requestDTO.setCreatedBy(requestHistory.getCreatedBy().getUserName());
        requestDTO.setCreatedOn(DateFormatter.format(requestHistory.getCreatedOn()));
        requestDTO.setServiceRequestId(requestHistory.getServiceRequest().getId().toString());
        return requestDTO;
    }

    private RequestHistory convertRequestHistoryDTOToEntity(RequestHistoryDTO requestHistoryDTO) {
        RequestHistory requestHistory = new RequestHistory();
        requestHistory.setServiceRequest(serviceRequestRepo.findOne(Long.parseLong(requestHistoryDTO.getServiceRequestId())));
        requestHistory.setComments(requestHistoryDTO.getComment());
        requestHistory.setStatus(requestHistoryDTO.getStatus());
        requestHistory.setCreatedBy(operationsUserService.getUserByName(requestHistoryDTO.getCreatedBy()));
        requestHistory.setCreatedOn(new Date());
        return requestHistory;
    }

    private List<RequestHistoryDTO> convertRequestHistoryEntitiesToDTOs(Iterable<RequestHistory> requestHistories) {
        List<RequestHistoryDTO> requestHistoryList = new ArrayList<>();
        for (RequestHistory requestHistory : requestHistories) {
            RequestHistoryDTO requestDTO = convertRequestHistoryEntityToDTO(requestHistory);
            requestHistoryList.add(requestDTO);
        }
        return requestHistoryList;
    }
}
