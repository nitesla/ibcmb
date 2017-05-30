package longbridge.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import longbridge.models.Person;
import longbridge.models.UserType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */
public class OperationsUserDTO implements Person{
    @JsonProperty("DT_RowId")
    private Long id;

    private int version;
    @NotEmpty(message = "userName")
    private String userName;
    @NotEmpty(message = "firstName")
    private String firstName;
    @NotEmpty(message = "lastName")
    private String lastName;
    @NotEmpty(message = "roleId")
    private String roleId;

    private String role;

    @NotEmpty(message = "email")
    @Email(message = "email")
    private String email;
    private String phoneNumber;
    @JsonIgnore
    private String password;
    private String status;
    private String createdOn;
    private Date expiryDate;
    private Date lockedUntilDate;
    private String lastLogin;
    private Date lastLoginDate;
    private int noOfLoginAttempts;
    private boolean external=false;

    private UserType userType;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

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

    public Date getExpiryDate() {
        return expiryDate;
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

    public void setExternal(boolean external) {
        this.external = external;
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
	public boolean isExternal() {
		return external;
	}
    
}
