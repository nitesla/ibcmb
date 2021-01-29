package longbridge.dtos;

 import java.io.Serializable;

/**
 * Created by Longbridge on 7/7/2017.
 */
public class CustomEntityRevisionDTO implements Serializable {

    private String lastChangedBy;
    private String ipAddress;
    private String timeStamp;
    private String revisionId;

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }



    public String getLastChangedBy() {
        return lastChangedBy;
    }

    public void setLastChangedBy(String lastChangedBy) {
        this.lastChangedBy = lastChangedBy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    @Override
    public String toString() {
        return "CustomEntityRevisionDTO{" +
                "lastChangedBy='" + lastChangedBy + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", revisionId='" + revisionId + '\'' +
                '}';
    }
}
