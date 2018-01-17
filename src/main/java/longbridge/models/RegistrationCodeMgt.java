package longbridge.models;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Longbridge on 7/14/2017.
 */
@Entity
public class RegistrationCodeMgt extends AbstractEntity {
    private String code;
    private String cifId;
    private Date initiatedTime;
    private Date expiryTime;
    private String status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
    }

    public Date getInitiatedTime() {
        return initiatedTime;
    }

    public void setInitiatedTime(Date initiatedTime) {
        this.initiatedTime = initiatedTime;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RegistrationCodeMgt{" +
                "code='" + code + '\'' +
                ", cifId='" + cifId + '\'' +
                ", initiatedTime=" + initiatedTime +
                ", expiryTime=" + expiryTime +
                ", status='" + status + '\'' +
                '}';
    }

}
