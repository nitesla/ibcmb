package longbridge.services.implementations;

import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.OperationsUser;
import longbridge.models.RequestHistory;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.repositories.RequestHistoryRepo;
import longbridge.repositories.RetailUserRepo;
import longbridge.repositories.ServiceRequestRepo;
import longbridge.services.CodeService;
import longbridge.services.RequestService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/7/2017.
 */

@Service
public class RequestServiceImpl implements RequestService {

    private ServiceRequestRepo serviceRequestRepo;
    private RequestHistoryRepo requestHistoryRepo;

    @Autowired
    private RetailUserRepo retailUserRepo;

    @Autowired
    private CodeService codeService;
    
    @Autowired
    private ModelMapper modelMapper;

    private Logger logger= LoggerFactory.getLogger(this.getClass());




    @Autowired
    public RequestServiceImpl(ServiceRequestRepo serviceRequestRepo, RequestHistoryRepo requestHistoryRepo){
        this.serviceRequestRepo=serviceRequestRepo;
        this.requestHistoryRepo=requestHistoryRepo;
    }

    @Override
    public void addRequest(ServiceRequestDTO request) {
        ServiceRequest serviceRequest = convertDTOToEntity(request);
        serviceRequest.setUser(retailUserRepo.findOne(serviceRequest.getUser().getId()));
        serviceRequestRepo.save(serviceRequest);
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
    public void addRequestHistory(RequestHistoryDTO requestHistoryDTO) {
        RequestHistory requestHistory =convertRequestHistoryDTOToEntity(requestHistoryDTO);
        ServiceRequest serviceRequest = requestHistory.getServiceRequest();
        serviceRequest.setRequestStatus(requestHistory.getStatus());
        serviceRequestRepo.save(serviceRequest);
        requestHistoryRepo.save(requestHistory);
    }

    @Override
    @Transactional
    public Iterable<RequestHistory> getRequestHistories(ServiceRequest request) {
        return serviceRequestRepo.findOne(request.getId()).getRequestHistories();
    }

    @Override
    @Transactional
    public Iterable<RequestHistoryDTO> getRequestHistories(Long serviceRequestId) {
        Iterable<RequestHistory> requestHistories  = serviceRequestRepo.findOne(serviceRequestId).getRequestHistories();
        return convertRequestHistoryEntitiesToDTOs(requestHistories);
    }

    public Page<ServiceRequestDTO>getRequests(RetailUser user, Pageable pageDetails){
        Page<ServiceRequest> page = serviceRequestRepo.findAll(pageDetails);
        List<ServiceRequestDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<ServiceRequestDTO> pageImpl = new PageImpl<>(dtOs, pageDetails, t);
        return pageImpl;
    }


    @Override
	public Page<ServiceRequestDTO> getRequests(ServiceRequestDTO request, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<RequestHistory> getRequestHistories(ServiceRequest serviceRequest, Pageable pageDetails) {
        Page<RequestHistory> page = this.getRequestHistories(serviceRequest,pageDetails);
       return page;
    }

    @Override
    public Page<RequestHistoryDTO> getRequestHistories(Long serviceRequestId, Pageable pageDetails) {
        ServiceRequest serviceRequest = serviceRequestRepo.findOne(serviceRequestId);
        Page<RequestHistory> page = this.getRequestHistories(serviceRequest,pageDetails);
        List<RequestHistoryDTO> dtOs = convertRequestHistoryEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();
        Page<RequestHistoryDTO> pageImpl = new PageImpl<>(dtOs, pageDetails, t);
        return pageImpl;
    }


    private ServiceRequestDTO convertEntityToDTO(ServiceRequest serviceRequest){
        ServiceRequestDTO requestDTO = modelMapper.map(serviceRequest,ServiceRequestDTO.class);
        requestDTO.setUsername(serviceRequest.getUser().getUserName());
        return requestDTO;

    }

    private ServiceRequest convertDTOToEntity(ServiceRequestDTO ServiceRequestDTO){
        return  modelMapper.map(ServiceRequestDTO,ServiceRequest.class);
    }

    private List<ServiceRequestDTO> convertEntitiesToDTOs(Iterable<ServiceRequest> serviceRequests){
        List<ServiceRequestDTO> serviceRequestDTOList = new ArrayList<>();
        for(ServiceRequest serviceRequest: serviceRequests){
            ServiceRequestDTO requestDTO =  convertEntityToDTO(serviceRequest);
            requestDTO.setUsername(serviceRequest.getUser().getUserName());
            requestDTO.setDate(serviceRequest.getDateRequested().toString());
            String status = codeService.getByTypeAndCode("REQUEST_STATUS",serviceRequest.getRequestStatus()).getDescription();
            requestDTO.setRequestStatus(status);
            serviceRequestDTOList.add(requestDTO);
        }
        return serviceRequestDTOList;
    }


    private RequestHistoryDTO convertRequestHistoryEntityToDTO(RequestHistory requestHistory){
        return  modelMapper.map(requestHistory,RequestHistoryDTO.class);
    }

    private RequestHistory convertRequestHistoryDTOToEntity(RequestHistoryDTO requestHistoryDTO){
        RequestHistory requestHistory = new RequestHistory();
        requestHistory.setServiceRequest(serviceRequestRepo.findOne(Long.parseLong(requestHistoryDTO.getServiceRequestId())));
        requestHistory.setComment(requestHistoryDTO.getComment());
        requestHistory.setStatus(requestHistoryDTO.getStatus());
        OperationsUser user = new OperationsUser();
        user.setId(1L);
        requestHistory.setCreatedBy(user);
        requestHistory.setCreatedOn(new Date());
        return requestHistory;
    }

    private List<RequestHistoryDTO> convertRequestHistoryEntitiesToDTOs(Iterable<RequestHistory> requestHistories){
        List<RequestHistoryDTO> requestHistoryList = new ArrayList<>();
        for(RequestHistory requestHistory: requestHistories){
            RequestHistoryDTO requestDTO =  new RequestHistoryDTO();
            requestDTO.setId(requestHistory.getId());
            requestDTO.setStatus(requestHistory.getStatus());
            requestDTO.setComment(requestHistory.getComment());
            requestDTO.setCreatedBy(requestHistory.getCreatedBy().getUserName());
            requestDTO.setCreatedOn(requestHistory.getCreatedOn().toString());
            requestDTO.setServiceRequestId(requestHistory.getServiceRequest().getId().toString());
            requestHistoryList.add(requestDTO);
        }
        return requestHistoryList;
    }
}
