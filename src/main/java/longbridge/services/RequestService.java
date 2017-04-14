package longbridge.services;

import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import longbridge.models.RequestHistory;
import longbridge.models.RetailUser;
import longbridge.models.ServiceRequest;
import longbridge.models.User;

/**
 * The {@code RequestService} interface provides the methods that manages customer's requests.
 * These requests include cheque request, draft request, Debit/Credit card request and token request.
 * @author Fortunatus Ekenaci
 * Created by 3/28/2017.
 */
public interface RequestService {

    /**
     * Adds a new request to the system
     * @param request the request
     */
    void addRequest(ServiceRequestDTO request);


    /**
     * Returns a request identified by the id
     * @param id the request's id
     * @return a service request
     */
    ServiceRequestDTO getRequest(Long id);

    /**
     *Returns a list of requests made by the specified user
     * @param user the user
     */
    Iterable<ServiceRequestDTO>getRequests(RetailUser user);
    
    Page<ServiceRequestDTO>getRequests(RetailUser user, Pageable pageDetails);


    public Page<ServiceRequestDTO> getRequests(ServiceRequestDTO request, Pageable pageDetails);

        /**
         * Creates and adds a new history for a request
         * @param requestHistory the request history
         */
    void addRequestHistory(RequestHistoryDTO requestHistory);


    /**
     *Returns a list of request histories for the specified service request
     * @param request the service request
     */
    Iterable<RequestHistory>getRequestHistories(ServiceRequest request);
    
    Page<RequestHistory>getRequestHistories(ServiceRequest request,Pageable pageDetails);

}
