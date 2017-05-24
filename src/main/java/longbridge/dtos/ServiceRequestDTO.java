package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 *  * Created by Fortune on 4/5/2017.
 */

public class ServiceRequestDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private Long userId;
    private String username;
    private String requestName;
    private String requestStatus;
    private String body;
    private Date dateRequested;
    private String date;
    private Long serviceReqConfigId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public Long getServiceReqConfigId() {
        return serviceReqConfigId;
    }

    public void setServiceReqConfigId(Long serviceReqConfig) {
        this.serviceReqConfigId = serviceReqConfig;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ServiceRequestDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", requestName='" + requestName + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", body='" + body + '\'' +
                ", dateRequested=" + dateRequested +
                ", date='" + date + '\'' +
                '}';
    }
}
