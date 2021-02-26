package longbridge.servicerequests.client;

import java.io.Serializable;
import java.util.Map;

public class AddRequestCmd implements Serializable {
    private Map<String,Object> body;
    private Long serviceReqConfigId;

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Long getServiceReqConfigId() {
        return serviceReqConfigId;
    }

    public void setServiceReqConfigId(Long serviceReqConfigId) {
        this.serviceReqConfigId = serviceReqConfigId;
    }
}
