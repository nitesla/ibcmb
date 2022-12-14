package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Showboy on 26/05/2017.
 */
public class CorpCorporateUserDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;
    private String corporateId;
    private String corporateType;
    private String corporateName;
    @NotEmpty(message = "User name Field Cannot be empty")
    private String userName;
    @NotEmpty(message = "First Name Field Cannot be empty")
    private String firstName;
    @NotEmpty(message = "Last Name Field Cannot be empty")
    private String lastName;
    @NotEmpty(message = "Email address Field Cannot be empty")
    private String email;
    @NotEmpty(message = "Phone Number Field Cannot be empty")
    private String  phoneNumber;
    private String roleId;
    private String role;
    private boolean ruleMember;
    private String password;
    private String status;
    private Date expiryDate;
    private Date lockedUntilDate;
    private Date lastLoginDate;
    private String lastLogin;
    private int noOfLoginAttempts;

    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRuleMember() {
        return ruleMember;
    }

    public void setRuleMember(boolean ruleMember) {
        this.ruleMember = ruleMember;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getCorporateType() {
        return corporateType;
    }

    public void setCorporateType(String corporateType) {
        this.corporateType = corporateType;
    }

    public Date getLockedUntilDate() {
        return lockedUntilDate;
    }

    public void setLockedUntilDate(Date lockedUntilDate) {
        this.lockedUntilDate = lockedUntilDate;
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

    @Override
    public String toString() {
        return "CorporateUserDTO{" +
                "id=" + id +
                ", corporateId='" + corporateId + '\'' +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", roleId='" + roleId + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", expiryDate=" + expiryDate +
                ", lockedUntilDate=" + lockedUntilDate +
                ", lastLoginDate=" + lastLoginDate +
                ", noOfLoginAttempts=" + noOfLoginAttempts +
                '}';
    }

}
