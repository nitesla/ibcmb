package longbridge.services.implementations;

import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.models.RequestHistory;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.models.ServiceRequest;
import longbridge.repositories.RequestHistoryRepo;
import longbridge.repositories.ServiceRequestRepo;
import longbridge.services.RequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 4/7/2017.
 */

@Service
public class RequestServiceImpl implements RequestService {

    private ServiceRequestRepo serviceRequestRepo;
    private RequestHistoryRepo requestHistoryRepo;
    
    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    public RequestServiceImpl(ServiceRequestRepo serviceRequestRepo, RequestHistoryRepo requestHistoryRepo){
        this.serviceRequestRepo=serviceRequestRepo;
        this.requestHistoryRepo=requestHistoryRepo;
    }

    @Override
    public void addRequest(ServiceRequestDTO request) {
        ServiceRequest serviceRequest = convertDTOToEntity(request);
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
         requestHistoryRepo.save(requestHistory);
    }

    @Override
    @Transactional
    public Iterable<RequestHistory> getRequestHistories(ServiceRequest request) {
        return serviceRequestRepo.findOne(request.getId()).getRequestHistories();
    }

    public Page<ServiceRequestDTO>getRequests(RetailUser user, Pageable pageDetails){
        // TODO Auto-generated method stub
        return null;
    }


    @Override
	public Page<ServiceRequestDTO> getRequests(ServiceRequestDTO request, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<RequestHistory> getRequestHistories(ServiceRequest request, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

    private ServiceRequestDTO convertEntityToDTO(ServiceRequest ServiceRequest){
        return  modelMapper.map(ServiceRequest,ServiceRequestDTO.class);
    }

    private ServiceRequest convertDTOToEntity(ServiceRequestDTO ServiceRequestDTO){
        return  modelMapper.map(ServiceRequestDTO,ServiceRequest.class);
    }

    private List<ServiceRequestDTO> convertEntitiesToDTOs(Iterable<ServiceRequest> serviceRequests){
        List<ServiceRequestDTO> serviceRequestDTOList = new ArrayList<>();
        for(ServiceRequest retailUser: serviceRequests){
            ServiceRequestDTO requestDTO =  convertEntityToDTO(retailUser);
            serviceRequestDTOList.add(requestDTO);
        }
        return serviceRequestDTOList;
    }


    private RequestHistoryDTO convertRequestHistoryEntityToDTO(RequestHistory requestHistory){
        return  modelMapper.map(requestHistory,RequestHistoryDTO.class);
    }

    private RequestHistory convertRequestHistoryDTOToEntity(RequestHistoryDTO requestHistoryDTO){
        return  modelMapper.map(requestHistoryDTO,RequestHistory.class);
    }

    private List<RequestHistoryDTO> convertRequestHistoryEntitiesToDTOs(Iterable<RequestHistory> requestHistories){
        List<RequestHistoryDTO> requestHistoryList = new ArrayList<>();
        for(RequestHistory requestHistory: requestHistories){
            RequestHistoryDTO requestDTO =  convertRequestHistoryEntityToDTO(requestHistory);
            requestHistoryList.add(requestDTO);
        }
        return requestHistoryList;
    }
}
