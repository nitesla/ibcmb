package longbridge.dtos;

import longbridge.models.Corporate;

import javax.validation.constraints.NotNull;
import java.util.Date;
/**
 * Created by Fortune on 4/5/2017.
 */
public class CorporateUserDTO {

    private Long id;
    private Corporate corporate;
    @NotNull
    private String userName;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String email;
    private String password;
    private String status;
    private Date expiryDate;
    private Date lockedUntilDate;
    private Date lastLoginDate;
    private int noOfLoginAttempts;

    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
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
}
