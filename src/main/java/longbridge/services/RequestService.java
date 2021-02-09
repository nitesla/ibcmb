package longbridge.services;

import longbridge.dtos.RequestHistoryDTO;
import longbridge.dtos.ServiceRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The {@code RequestService} interface provides the methods that manages customer's requests.
 * These requests include cheque request, draft request, Debit/Credit card request and token request.
 * @author Fortunatus Ekenachi
 * Created by 3/28/2017.
 */
public interface RequestService {

    /**
     * Adds a new request to the system
     * @param request the request
     */
    @PreAuthorize("hasAuthority('ADD_SERVICE_REQUEST')")
    String addRequest(ServiceRequestDTO request) ;

    @PreAuthorize("hasAuthority('ADD_SERVICE_REQUEST')")
    String addCorpRequest(ServiceRequestDTO request) ;

    /**
     * Returns a request identified by the id
     * @param id the request's id
     * @return a service request
     */
    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    ServiceRequestDTO getRequest(Long id);

    /**
     *Returns a list of requests made by the specified user
     * @param user the user
     */
    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    Iterable<ServiceRequestDTO>getRequests(RetailUser user);

    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    Page<ServiceRequestDTO>getRequests(RetailUser user, Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    Page<ServiceRequestDTO>getRequests(Corporate corporate, Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    Page<ServiceRequestDTO>getRequests(OperationsUser opsUser,Pageable pageDetails);

    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    int getNumOfUnattendedRequests(OperationsUser opsUser);

    /**
     * Creates and adds a new history for a request
     * @param requestHistory the request history
     */
    @PreAuthorize("hasAuthority('REQUEST_HISTORY')")
    String addRequestHistory(RequestHistoryDTO requestHistory) ;


    /**
     *Returns a list of request histories for the specified service request
     * @param request the service request
     */
    @PreAuthorize("hasAuthority('REQUEST_HISTORY')")
    Iterable<RequestHistory>getRequestHistories(ServiceRequest request);

    @PreAuthorize("hasAuthority('REQUEST_HISTORY')")
    Iterable<RequestHistoryDTO>getRequestHistories(Long requestId);




}
