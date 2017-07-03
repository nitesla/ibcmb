package longbridge.models;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;

import javax.persistence.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * Created by Wunmi on 29/03/2017.
 */
@MappedSuperclass
public class User extends AbstractEntity implements PrettySerializer {

    protected String userName;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String password;
    protected String status;
    protected Date createdOnDate;
    protected Date expiryDate;
    protected Date lockedUntilDate;
    protected Date lastLoginDate;
    protected int noOfLoginAttempts;


    //@Enumerated(value = EnumType.STRING)
    @Enumerated(EnumType.ORDINAL)
    protected UserType userType;

    @ManyToOne
    protected Code alertPreference;

    @ManyToOne
    protected Role role;
    protected String entrustId;



    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override @JsonIgnore
	public List<String> getDefaultSearchFields() {
		return Arrays.asList("userName", "firstName","lastName");
	}

    @Override
    @JsonIgnore
    public JsonSerializer<User> getSerializer() {
        return new JsonSerializer<User>() {
            @Override
            public void serialize(User value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException {

                gen.writeStartObject();
                gen.writeStringField("User Name", value.userName);
                gen.writeStringField("First Name", value.firstName);
                gen.writeStringField("Last Name", value.lastName);
                gen.writeStringField("Email", value.email);
                gen.writeStringField("Phone", value.phoneNumber);
                String status =null;
                if ("A".equals(value.status))
                    status = "Active";
                else if ("I".equals(value.status))
                    status = "Inactive";
                else if ("L".equals(value.status))
                    status = "Locked";
                gen.writeStringField("Status", status);
                gen.writeStringField("Role", value.role.getName());
                gen.writeEndObject();
            }
        };
    }
}