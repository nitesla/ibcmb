package longbridge.dtos;

import longbridge.models.RetailUser;
import longbridge.models.UserGroup;

import java.util.Date;

/**
 *  * Created by Fortune on 4/5/2017.
 */

public class ServiceRequestDTO {


    private String serviceRequestType;
    private String subject;
    private String body;
    private String recepient;
    private String status;
    //private UserGroup userGroup;
    private Date requestTime;


    public String getServiceRequestType() {
        return serviceRequestType;
    }

    public void setServiceRequestType(String serviceRequestType) {
        this.serviceRequestType = serviceRequestType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecepient() {
        return recepient;
    }

    public void setRecepient(String recepient) {
        this.recepient = recepient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }




    public ServiceRequestDTO() {
    }

    public ServiceRequestDTO(String serviceRequestType, String subject, String body, String recepient, UserGroup userGroup, Date date) {

        this.serviceRequestType = serviceRequestType;
        this.subject = subject;
        this.body = body;
        this.recepient = recepient;
        //this.userGroup = userGroup;
        this.requestTime = date;
    }


    @Override
    public String toString() {
        return "ServiceRequest{" +
                ", serviceRequestType='" + serviceRequestType + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recepient='" + recepient + '\'' +
                //", userGroup=" + userGroup +
                ", requestTime=" + requestTime +
                '}';
    }
}
