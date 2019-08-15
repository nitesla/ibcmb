package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited(withModifiedFlag=true)
public class NapsAntiFraudData implements Serializable {


          @Id
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
           private  Date createdOn=new Date();


    @OneToMany(mappedBy = "napsAntiFraudData",cascade = {CascadeType.ALL})
    private List<CreditRequest> crRequestList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NapsAntiFraudData() {
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

    public List<CreditRequest> getCrRequestList() {
        return crRequestList;
    }

    public void setCrRequestList(List<CreditRequest> crRequestList) {
        this.crRequestList = crRequestList;
    }

    @Override
    public String toString() {
        return "NapsAntiFraudData{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", deviceNumber='" + deviceNumber + '\'' +
                ", headerProxyAuthorization='" + headerProxyAuthorization + '\'' +
                ", headerUserAgent='" + headerUserAgent + '\'' +
                ", ip='" + ip + '\'' +
                ", loginName='" + loginName + '\'' +
                ", sessionkey='" + sessionkey + '\'' +
                ", sfactorAuthIndicator='" + sfactorAuthIndicator + '\'' +
                ", tranLocation='" + tranLocation + '\'' +
                ", createdOn=" + createdOn +
                ", crRequestList=" + crRequestList +
                '}';
    }
}
