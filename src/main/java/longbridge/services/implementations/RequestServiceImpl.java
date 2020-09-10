package longbridge.services.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceReqConfigDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.NameValue;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UserGroupMessageService groupMessageService;

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
            serviceRequest.setUser(retailUserRepo.findById(request.getUserId()).get());
            String name = getFullName(serviceRequest);
            ServiceReqConfigDTO config = reqConfigService.getServiceReqConfig(request.getServiceReqConfigId());

            //***///
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(serviceRequest.getBody(), new TypeReference<ArrayList<NameValue>>() {
            });

            StringBuilder messageBody = new StringBuilder();
            messageBody.append("From : " + name + "\n");
            messageBody.append("Date : " + DateFormatter.format(serviceRequest.getDateRequested()) + "\n");


            for(NameValue nameValue : myFormObjects){
                messageBody.append(nameValue.getName() + " : " + nameValue.getValue() + "\n");
            }

            String message = messageBody.toString();
            serviceRequestRepo.save(serviceRequest);

            Email email = new Email.Builder()
                    .setSubject(serviceRequest.getRequestName())
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
            Corporate corporate = corporateRepo.findById(request.getCorpId()).get();
            serviceRequest.setCorporate(corporate);
            String name = corporate.getName();
            ServiceReqConfigDTO config = reqConfigService.getServiceReqConfig(serviceRequest.getServiceReqConfigId());

            //***///
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<NameValue> myFormObjects = objectMapper.readValue(serviceRequest.getBody(), new TypeReference<ArrayList<NameValue>>() {
            });

            StringBuilder messageBody = new StringBuilder();

            messageBody.append("From : " + name + "\n");
            messageBody.append("Date : " + DateFormatter.format(serviceRequest.getDateRequested()) + "\n");


            for(NameValue nameValue : myFormObjects){
                messageBody.append(nameValue.getName() + " : " + nameValue.getValue() + "\n");
            }

            String message = messageBody.toString();
            serviceRequestRepo.save(serviceRequest);

            Email email = new Email.Builder()
                    .setSubject(serviceRequest.getRequestName())
                    .setBody(message)
                    .build();
            groupMessageService.send(config.getGroupId(), email);
            return messageSource.getMessage("req.add.success", null, locale);
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("req.add.failure", null, locale), e);
        }
    }

    @Override
    public ServiceRequestDTO getRequest(Long id) {
        ServiceRequest serviceRequest = serviceRequestRepo.findById(id).get();
        return convertEntityToDTO(serviceRequest);
    }

    @Override
    public Iterable<ServiceRequestDTO> getRequests(RetailUser user) {
        Iterable<ServiceRequest> requests = serviceRequestRepo.findByUser(user);
        logger.info("DEBUG SERVICE LAYER {}", requests);
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
        return serviceRequestRepo.findById(request.getId()).get().getRequestHistories();
    }

    @Override
    @Transactional
    public Iterable<RequestHistoryDTO> getRequestHistories(Long serviceRequestId) {
        Iterable<RequestHistory> requestHistories = serviceRequestRepo.findById(serviceRequestId).get().getRequestHistories();
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

//    public Page<ServiceRequestDTO> getRequests(OperationsUser opsUser, Pageable pageDetails) {
//        Page<ServiceRequest> page = serviceRequestRepo.findAllByOrderByDateRequestedDesc(pageDetails);
//        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//        List<ServiceRequestDTO> requestsForOpsUser = new ArrayList<ServiceRequestDTO>();
//        List<UserGroup> opsUserGroups = opsUser.getGroups();
//        for (ServiceRequestDTO request : dtOs) {
//            SRConfig reqConfig = reqConfigRepo.findById(request.getServiceReqConfigId()).get();
//            if(reqConfig!=null) {
//                for (UserGroup group : opsUserGroups) {
//                    if (group != null) {
//                        logger.info("USER GROUP {}", group.equals(userGroupRepo.findById(reqConfig.getGroupId()).get()));
//                        if (group.equals(userGroupRepo.findById(reqConfig.getGroupId()).get())) {
//
//                            requestsForOpsUser.add(request);
//                        }
//                    }
//                }
//            }
//        }
//        long t = page.getTotalElements();
//        Page<ServiceRequestDTO> pageImpl = new PageImpl<>(requestsForOpsUser, pageDetails, t);
//        return pageImpl;
//    }


    public Page<ServiceRequestDTO> getRequests(OperationsUser opsUser, Pageable pageDetails) {
        Page<ServiceRequest> page = serviceRequestRepo.findAllByOrderByDateRequestedDesc(pageDetails);

//        opsUser =  operationsUserService.getUser(opsUser.getId());

        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        List<ServiceRequestDTO> requestsForOpsUser = new ArrayList<>();
        List<UserGroup> opsUserGroups = opsUser.getGroups();

        for (ServiceRequestDTO request : dtOs) {

            Optional<SRConfig> reqOpt = reqConfigRepo.findById(request.getServiceReqConfigId());
            if(reqOpt.isPresent()) {
                SRConfig reqConfig = reqOpt.get();
                requestsForOpsUser.add(request);
            }
        }

        long t = page.getTotalElements();
        return new PageImpl<>(requestsForOpsUser, pageDetails, t);
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



    private ServiceRequestDTO convertEntityToDTO(ServiceRequest serviceRequest) {
        ServiceRequestDTO requestDTO = modelMapper.map(serviceRequest, ServiceRequestDTO.class);
        if (serviceRequest.getUser() != null){
            String fullName = serviceRequest.getUser().getFirstName()+" "+serviceRequest.getUser().getLastName();
            requestDTO.setUsername(serviceRequest.getUser().getUserName());
            requestDTO.setUserType(serviceRequest.getUser().getUserType().toString());
            requestDTO.setFullName(fullName);
        }else if (serviceRequest.getCorporate() != null){
            requestDTO.setCorpName(serviceRequest.getCorporate().getName());
            requestDTO.setFullName(serviceRequest.getCorporate().getName());
            requestDTO.setUserType(UserType.CORPORATE.toString());

        }
        requestDTO.setDate(DateFormatter.format(serviceRequest.getDateRequested()));
        return requestDTO;
    }

    private ServiceRequest convertDTOToEntity(ServiceRequestDTO ServiceRequestDTO) {
        return modelMapper.map(ServiceRequestDTO, ServiceRequest.class);
    }

    private List<ServiceRequestDTO> convertEntitiesToDTOs(Iterable<ServiceRequest> serviceRequests) {
        List<ServiceRequestDTO> serviceRequestDTOList = new ArrayList<>();
        for (ServiceRequest serviceRequest : serviceRequests) {
            ServiceRequestDTO requestDTO = convertEntityToDTO(serviceRequest);
            Code code = codeService.getByTypeAndCode("REQUEST_STATUS", requestDTO.getRequestStatus());
            if (code != null) {
                String status = code.getDescription();
                requestDTO.setRequestStatus(status);
            }
            serviceRequestDTOList.add(requestDTO);
        }
        return serviceRequestDTOList;
    }


    private RequestHistoryDTO convertRequestHistoryEntityToDTO(RequestHistory requestHistory) {
        RequestHistoryDTO requestDTO = new RequestHistoryDTO();
        requestDTO.setId(requestHistory.getId());
        String status = codeService.getByTypeAndCode("REQUEST_STATUS", requestHistory.getStatus()).getDescription();
        requestDTO.setStatus(status);
        requestDTO.setComments(requestHistory.getComments());
        requestDTO.setCreatedBy(requestHistory.getCreatedBy().getUserName());
        requestDTO.setCreatedOn(DateFormatter.format(requestHistory.getCreatedOn()));
        requestDTO.setServiceRequestId(requestHistory.getServiceRequest().getId().toString());
        return requestDTO;
    }

    private RequestHistory convertRequestHistoryDTOToEntity(RequestHistoryDTO requestHistoryDTO) {
        RequestHistory requestHistory = new RequestHistory();
        requestHistory.setServiceRequest(serviceRequestRepo.findById(Long.parseLong(requestHistoryDTO.getServiceRequestId())).get());
        requestHistory.setComments(requestHistoryDTO.getComments());
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
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }

        return null;
    }

}
