package longbridge.services.implementations;

import longbridge.models.RequestHistory;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.repositories.RequestHistoryRepo;
import longbridge.repositories.ServiceRequestRepo;
import longbridge.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Fortune on 4/7/2017.
 */

@Service
public class RequestServiceImpl implements RequestService {

    private ServiceRequestRepo serviceRequestRepo;
    private RequestHistoryRepo requestHistoryRepo;


    @Autowired
    public RequestServiceImpl(ServiceRequestRepo serviceRequestRepo, RequestHistoryRepo requestHistoryRepo){
        this.serviceRequestRepo=serviceRequestRepo;
        this.requestHistoryRepo=requestHistoryRepo;
    }

    @Override
    public void addRequest(ServiceRequest request) {
        serviceRequestRepo.save(request);
    }

    @Override
    public ServiceRequest getRequest(Long id) {
        return serviceRequestRepo.findOne(id);
    }

    @Override
    public Iterable<ServiceRequest> getRequests(RetailUser user) {
        return serviceRequestRepo.findByUser(user);
    }

    @Override
    public void addRequestHistory(RequestHistory requestHistory) {
         requestHistoryRepo.save(requestHistory);
    }

    @Override
    public Iterable<RequestHistory> getRequestHistories(ServiceRequest request) {
        return serviceRequestRepo.findOne(request.getId()).getRequestHistories();
    }

	@Override
	public Page<ServiceRequest> getRequests(RetailUser user, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<RequestHistory> getRequestHistories(ServiceRequest request, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}
}
