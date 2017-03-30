package longbridge.services;

import longbridge.models.RequestHistory;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface RequestService {

    /**
     * the addRequest method creates a particular request
     */
    void addRequest();

    /**
     *
     */
    void getRequest();

    /**
     *
     */
    void cancelRequest();

    /**
     *
     * @param requestHistory the request history
     */
    void addRequestHistory(RequestHistory requestHistory);

}
