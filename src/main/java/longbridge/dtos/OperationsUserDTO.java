package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.Person;
import longbridge.models.UserType;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */
public class OperationsUserDTO implements Person{
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
    private String email;
    @NotEmpty(message = "phoneNumber")
    private String phoneNumber;
    @JsonIgnore
    private String password;
    private String status;
    private String createdOnDate;
    private Date expiryDate;
    private Date lockedUntilDate;
    private String lastLoginDate;
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

    public String getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(String createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
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

    @Override
    public String toString() {
        return "OperationsUserDTO{" +
                "id=" + id +
                ", version=" + version +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roleId='" + roleId + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", createdOnDate='" + createdOnDate + '\'' +
                ", expiryDate=" + expiryDate +
                ", lockedUntilDate=" + lockedUntilDate +
                ", lastLoginDate='" + lastLoginDate + '\'' +
                ", noOfLoginAttempts=" + noOfLoginAttempts +
                ", external=" + external +
                ", userType=" + userType +
                '}';
    }
}
