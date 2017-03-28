package longbridge.services;

import longbridge.models.RequestHistory;

/**
 * Created by Fortune on 3/28/2017.
 */
public interface RequestService {

    void addRequest();

    void getRequest();

    void cancelRequest();

    void addRequestHistory(RequestHistory requestHistory);

}
