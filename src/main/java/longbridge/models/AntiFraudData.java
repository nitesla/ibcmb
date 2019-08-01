package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

//@Entity
@Audited(withModifiedFlag=true)
public class AntiFraudData {

           @Id
           @GeneratedValue(strategy = GenerationType.AUTO)
           private Long id;
           private String countryCode;
           private String deviceNumber;
           private String headerProxyAuthorization;
           private String headerUserAgent;
           private String ip;
           private String loginName;
           private String sessionKey;
           private String  sfactorAuthIndicator;
           private String tranLocation;
           private Date createdOn=new Date();
           @OneToOne
           @Transient
           private TransRequest transRequest;

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

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
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

    public TransRequest getTransRequest() {
        return transRequest;
    }

    public void setTransRequest(TransRequest transRequest) {
        this.transRequest = transRequest;
    }
}
