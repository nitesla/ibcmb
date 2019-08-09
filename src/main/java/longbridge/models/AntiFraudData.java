package longbridge.models;

import java.io.Serializable;

//@Entity
//@Audited(withModifiedFlag=true)
//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public class AntiFraudData implements Serializable {


          // @Id
         //  @GeneratedValue(strategy = GenerationType.AUTO)
         //  private Long id;
           private String countryCode;
           private String deviceNumber;
           private String headerProxyAuthorization;
           private String headerUserAgent;
           private String ip;
           private String loginName;
           private String sessionkey;
           private String  sfactorAuthIndicator;
           private String tranLocation;
          // private  Date createdOn=new Date();
         //  @OneToOne
           //@Transient
          // private TransRequest transRequest;
/*
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }*/

    public AntiFraudData() {
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

   /* @JsonIgnore
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @JsonIgnore
    public TransRequest getTransRequest() {
        return transRequest;
    }

    public void setTransRequest(TransRequest transRequest) {
        this.transRequest = transRequest;
    }*/

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
                '}';
    }
}
