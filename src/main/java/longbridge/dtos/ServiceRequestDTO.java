package longbridge.dtos;

import longbridge.models.RetailUser;

import java.util.Date;

/**
 *  * Created by Fortune on 4/5/2017.
 */

public class ServiceRequestDTO {

    private RetailUser user;
    private String requestName;
    private String body;
    private Date requestTime;

    public RetailUser getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return "ServiceRequestDTO{" +
                "requestName='" + requestName + '\'' +
                ", body='" + body + '\'' +
                ", requestTime=" + requestTime +
                '}';
    }
}
