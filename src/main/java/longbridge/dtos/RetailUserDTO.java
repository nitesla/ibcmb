package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.Code;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */
public class RetailUserDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty(message = "userName")
    private String userName;
    private String entrustId;
    private String customerId;
    private String version;
    @NotEmpty(message = "firstName")
    private String firstName;
    @NotEmpty(message = "lastName")
    private String lastName;
    @NotEmpty(message = "email")
    private String email;
    @NotEmpty(message = "phoneNumber")
    private String phoneNumber;
    private Date birthDate;
    private String password;
    private String status;
    private String bvn;
    private Date createdOnDate;
    private Date expiryDate;
    private Date lockedUntilDate;
    private Date lastLoginDate;
    private int noOfLoginAttempts;
    private List<String> securityQuestion;
    private List<String> securityAnswer;
    private String phishingSec;
    private String captionSec;
    @NotNull(message = "roleId")
    private Long roleId;
    private Code alertPreference;
    private String feedBackStatus;
    private List<String> coverageCodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getLockedUntilDate() {
        return lockedUntilDate;
    }

    public void setLockedUntilDate(Date lockedUntilDate) {
        this.lockedUntilDate = lockedUntilDate;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public int getNoOfLoginAttempts() {
        return noOfLoginAttempts;
    }

    public void setNoOfLoginAttempts(int noOfLoginAttempts) {
        this.noOfLoginAttempts = noOfLoginAttempts;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public Code getAlertPreference() {
        return alertPreference;
    }

    public void setAlertPreference(Code alertPreference) {
        this.alertPreference = alertPreference;
    }

    public List<String> getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(List<String> securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityAnswer(List<String> securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public List<String> getSecurityAnswer() {
        return securityAnswer;
    }

    public String getPhishingSec() {
        return phishingSec;
    }

    public void setPhishingSec(String phishingSec) {
        this.phishingSec = phishingSec;
    }

    public String getCaptionSec() {
        return captionSec;
    }

    public void setCaptionSec(String captionSec) {
        this.captionSec = captionSec;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFeedBackStatus() {
        return feedBackStatus;
    }

    public void setFeedBackStatus(String feedBackStatus) {
        this.feedBackStatus = feedBackStatus;
    }

    public List<String> getCoverageCodes() {
        return coverageCodes;
    }

    public void setCoverageCodes(List<String> coverageCodes) {
        this.coverageCodes = coverageCodes;
    }

    @Override
    public String toString() {
        return "RetailUserDTO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", entrustId='" + entrustId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", version='" + version + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate=" + birthDate +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", bvn='" + bvn + '\'' +
                ", createdOnDate='" + createdOnDate + '\'' +
                ", expiryDate=" + expiryDate +
                ", lockedUntilDate=" + lockedUntilDate +
                ", lastLoginDate='" + lastLoginDate + '\'' +
                ", noOfLoginAttempts=" + noOfLoginAttempts +
                ", securityQuestion=" + securityQuestion +
                ", securityAnswer=" + securityAnswer +
                ", phishingSec='" + phishingSec + '\'' +
                ", captionSec='" + captionSec + '\'' +
                ", alertPreference=" + alertPreference +
                ", feedBackStatus='" + feedBackStatus + '\'' +
                '}';
    }
}
