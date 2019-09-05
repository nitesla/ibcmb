package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Audited(withModifiedFlag=true)
public class AntiFraudData implements Serializable {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String countryCode;
    private String deviceNumber;
    private String headerProxyAuthorization;
    private String headerUserAgent;
    private String ip;
    private String loginName;
    private String sessionkey;
    private String  sfactorAuthIndicator;
    private String tranLocation;
    private Date createdOn=new Date();
    @Transient
    private String channel;
    @JsonIgnore
    private Long tranRequestId;



    public AntiFraudData() {
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getHeaderProxyAuthorization() {
        return headerProxyAuthorization;
    }

    public void setHeaderProxyAuthorization(String headerProxyAuthorization) {
        this.headerProxyAuthorization = headerProxyAuthorization;
    }

    public String getHeaderUserAgent() {
        return headerUserAgent;
    }

    public void setHeaderUserAgent(String headerUserAgent) {
        this.headerUserAgent = headerUserAgent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public String getSfactorAuthIndicator() {
        return sfactorAuthIndicator;
    }

    public void setSfactorAuthIndicator(String sfactorAuthIndicator) {
        this.sfactorAuthIndicator = sfactorAuthIndicator;
    }

    public String getTranLocation() {
        return tranLocation;
    }

    public void setTranLocation(String tranLocation) {
        this.tranLocation = tranLocation;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    public Long getTranRequestId() {
        return tranRequestId;
    }

    public void setTranRequestId(Long tranRequestId) {
        this.tranRequestId = tranRequestId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "AntiFraudData{" +
                "countryCode='" + countryCode + '\'' +
                ", deviceNumber='" + deviceNumber + '\'' +
                ", headerProxyAuthorization='" + headerProxyAuthorization + '\'' +
                ", headerUserAgent='" + headerUserAgent + '\'' +
                ", ip='" + ip + '\'' +
                ", loginName='" + loginName + '\'' +
                ", sessionkey='" + sessionkey + '\'' +
                ", sfactorAuthIndicator='" + sfactorAuthIndicator + '\'' +
                ", tranLocation='" + tranLocation + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }


}
