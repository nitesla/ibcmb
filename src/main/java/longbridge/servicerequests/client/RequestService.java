package longbridge.servicerequests.client;

import longbridge.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 */
public interface RequestService {

    /**
     * Adds a new request to the system
     * @param request the request
     */
    @PreAuthorize("hasAuthority('ADD_SERVICE_REQUEST')")
    void addRequest(AddRequestCmd request) ;
// TODO :Add back
//    @PreAuthorize("hasAuthority('ALTER_SERVICE_REQUESTS')")
    ServiceRequestDTO.CommentDTO addRequestComment(AddCommentCmd commentCmd);


    /**
     * Returns a request identified by the id
     * @param id the request's id
     * @return a service request
     */
    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    ServiceRequestDTO getRequest(Long id);
    

    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    Page<ServiceRequestDTO>getUserRequests(Pageable pageDetails);


    @PreAuthorize("hasAuthority('GET_SERVICE_REQUEST')")
    Page<ServiceRequestDTO>getOpRequests(OperationsUser opsUser, Pageable pageDetails);


}
