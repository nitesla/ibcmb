package longbridge.services;

import longbridge.models.RequestHistory;
import longbridge.models.ServiceRequest;
import longbridge.models.User;

/**
 * The {@code RequestService} interface provides the methods that manages customer's requests.
 * These requests include cheque request, draft request, Debit/Credit card request and token request.
 * Other type of requests are customer's complaint's on issues such as lost token, dispense error, transfer error, etc
 * @author Fortunatus Ekenaci
 * Created by 3/28/2017.
 */
public interface RequestService {

    /**
     * Adds a new request to the system
     * @param request the request
     */
    void addRequest(ServiceRequest request);

    /**
     *Returns a list of requests made by the specified user
     * @param user the user
     */
    Iterable<ServiceRequest>getRequests(User user);


    /**
     * Creates and adds a new history for a request
     * @param requestHistory the request history
     */
    void addRequestHistory(RequestHistory requestHistory);

}
