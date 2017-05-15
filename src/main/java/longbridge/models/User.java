package longbridge.models;



import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.Date;
/**
 * Created by Wunmi on 29/03/2017.
 */
@MappedSuperclass
public class User extends AbstractEntity{

    protected String userName;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    @Column(columnDefinition = "TEXT")
    protected String usedPasswords;
    protected String status;
    protected Date createdOnDate;
    protected Date expiryDate;
    protected Date lockedUntilDate;
    protected Date lastLoginDate;
    protected int noOfLoginAttempts;

    //@Enumerated(value = EnumType.STRING)
    protected UserType userType;

    @ManyToOne
    protected Code alertPreference;

    @ManyToOne
    protected Role role;

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

    public String getUsedPasswords() {
        return usedPasswords;
    }

    public void setUsedPasswords(String usedPasswords) {
        this.usedPasswords = usedPasswords;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Role getRole() {
        return role;
    }


    public void setRole(Role role) {
        this.role = role;
    }


    public Code getAlertPreference() {
        return alertPreference;
    }

    public void setAlertPreference(Code alertPreference) {
        this.alertPreference = alertPreference;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public Date getExpiryDate() {return expiryDate;}

    public void setExpiryDate(Date expiryDate) {this.expiryDate = expiryDate;}

    public Date getLockedUntilDate() {return lockedUntilDate;}

    public void setLockedUntilDate(Date lockedUntilDate) {this.lockedUntilDate = lockedUntilDate;}

    public Date getLastLoginDate() {return lastLoginDate;}

    public void setLastLoginDate(Date lastLoginDate) {this.lastLoginDate = lastLoginDate;}

    public int getNoOfLoginAttempts() {
		return noOfLoginAttempts;
	}

	public void setNoOfLoginAttempts(int noOfLoginAttempts) {
		this.noOfLoginAttempts = noOfLoginAttempts;
	}

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", createdOnDate=" + createdOnDate +
                ", expiryDate=" + expiryDate +
                ", lockedUntilDate=" + lockedUntilDate +
                ", lastLoginDate=" + lastLoginDate +
                ", noOfLoginAttempts=" + noOfLoginAttempts +
                ", userType=" + userType +
                ", role=" + role +
                '}';
    }

	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}


}