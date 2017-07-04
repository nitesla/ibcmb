package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.Code;
import longbridge.models.UserType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */
public class AdminUserDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private  int version;

    private String cifId;
    @NotEmpty(message = "userName")
    private String userName;
    @NotEmpty(message = "firstName")
    private String firstName;
    @NotEmpty(message = "lastName")
    private String lastName;
    @NotEmpty(message = "email")
    @Email(message = "email")
    private String email;
    @NotEmpty(message = "phoneNumber")
    private String phoneNumber;
    private String password;
    private String createdOn;
    private Date expiryDate;
    private String lastLogin;
    private String status;
    @NotEmpty(message = "roleId")
    private String roleId;
    private String role;
    private UserType userType;
    private Code alertPreference;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
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

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }


    public Code getAlertPreference() {
        return alertPreference;
    }

    public void setAlertPreference(Code alertPreference) {
        this.alertPreference = alertPreference;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

	@Override
	public String toString() {
		return "AdminUserDTO [id=" + id + ", version=" + version + ", cifId=" + cifId + ", userName=" + userName
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", status=" + status
				+ ", role=" + role + ", userType=" + userType + "]";
	}
    
    
}
