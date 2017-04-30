package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.RetailUser;

import java.util.Date;

/**
 *  * Created by Fortune on 4/5/2017.
 */

public class ServiceRequestDTO {

    @JsonProperty("DT_RowId")
    private  Long id;
    private RetailUser user;
    private String username;
    private String requestName;
    private  String requestStatus;
    private String body;
    private Date dateRequested;
    private String date;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
                "user=" + user +
                ", username='" + username + '\'' +
                ", requestName='" + requestName + '\'' +
                ", requestStatus='" + requestStatus + '\'' +
                ", body='" + body + '\'' +
                ", dateRequested=" + dateRequested +
                ", date='" + date + '\'' +
                '}';
    }
}
