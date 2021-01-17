package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.models.Code;
import longbridge.models.CorpUserType;
import longbridge.utils.PrettySerializer;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */

public class CorporateUserDTO extends AbstractDTO implements PrettySerializer {


    private int version;
    private String corporateId;
    private String corporateType;
    private String corporateName;
    @NotEmpty(message = "userName")
    private String userName;
    private String entrustId;
    private String entrustGroup;
    @NotEmpty(message = "firstName")
    private String firstName;
    @NotEmpty(message = "lastName")
    private String lastName;
    @NotEmpty(message = "email")
    private String email;
    @NotEmpty(message = "phoneNumber")
    private String phoneNumber;
    private boolean admin;
    private boolean authorizer;
    private String authorizerLevel;
    private String userType;
    private CorpUserType corpUserType;
    private String designation;
    private String roleId;
    private String role;
    private boolean ruleMember;
    private String password;
    private String status;
    private Date expiryDate;
    private Date lockedUntilDate;
    private String lastLoginDate;
    private int noOfLoginAttempts;
    private Code alertPreference;
    private String createdOnDate;
    private Long corporateRoleId;
    private String corporateRole;
    private List<String> securityQuestion;
    private List<String> securityAnswer;
    private String phishingSec;
    private String captionSec;
    private String isFirstTimeLogon;
    private List<AccountPermissionDTO> accountPermissions = new ArrayList<>();

    public CorporateUserDTO() {
    }

    public CorporateUserDTO(String userName) {
        this.userName = userName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getEntrustGroup() {
        return entrustGroup;
    }

    public void setEntrustGroup(String entrustGroup) {
        this.entrustGroup = entrustGroup;
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


    public int getNoOfLoginAttempts() {
        return noOfLoginAttempts;
    }

    public void setNoOfLoginAttempts(int noOfLoginAttempts) {
        this.noOfLoginAttempts = noOfLoginAttempts;
    }

    public Code getAlertPreference() {
        return alertPreference;
    }

    public void setAlertPreference(Code alertPreference) {
        this.alertPreference = alertPreference;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(String createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public Long getCorporateRoleId() {
        return corporateRoleId;
    }

    public void setCorporateRoleId(Long corporateRoleId) {
        this.corporateRoleId = corporateRoleId;
    }

    public String getCorporateRole() {
        return corporateRole;
    }

    public void setCorporateRole(String corporateRole) {
        this.corporateRole = corporateRole;
    }

    public List<String> getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(List<String> securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public List<String> getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(List<String> securityAnswer) {
        this.securityAnswer = securityAnswer;
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

    public String getIsFirstTimeLogon() {
        return isFirstTimeLogon;
    }

    public void setIsFirstTimeLogon(String isFirstTimeLogon) {
        this.isFirstTimeLogon = isFirstTimeLogon;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAuthorizer() {
        return authorizer;
    }

    public CorpUserType getCorpUserType() {
        return corpUserType;
    }

    public void setCorpUserType(CorpUserType corpUserType) {
        this.corpUserType = corpUserType;
    }

    public void setAuthorizer(boolean authorizer) {
        this.authorizer = authorizer;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getAuthorizerLevel() {
        return authorizerLevel;
    }

    public void setAuthorizerLevel(String authorizerLevel) {
        this.authorizerLevel = authorizerLevel;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<AccountPermissionDTO> getAccountPermissions() {
        return accountPermissions;
    }

    public void setAccountPermissions(List<AccountPermissionDTO> accountPermissions) {
        this.accountPermissions = accountPermissions;
    }

    @Override
    @JsonIgnore
    public JsonSerializer<CorporateUserDTO> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorporateUserDTO value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {

                gen.writeStartObject();
                gen.writeStringField("Corporate Name", value.corporateName);
                gen.writeStringField("Username", value.userName);
                gen.writeStringField("First Name", value.firstName);
                gen.writeStringField("Last Name", value.lastName);
                gen.writeStringField("Email", value.email);
                gen.writeStringField("Phone", value.phoneNumber);
                String status = null;
                if ("A".equals(value.status)) {
                    status = "Active";
                } else if ("I".equals(value.status)) {
                    status = "Inactive";
                } else if ("L".equals(value.status)) {
                    status = "Locked";
                }
                if (status != null) {
                    gen.writeStringField("Status", status);
                }
                if (value.role != null) {
                    gen.writeStringField("Role", value.role);
                }
                if (value.corpUserType != null) {
                    gen.writeStringField("User Type", value.corpUserType.name());
                }
                if (CorpUserType.AUTHORIZER.equals(value.corpUserType)) {
                    gen.writeStringField("Authorizer Level", value.corporateRole);
                }
                if (!accountPermissions.isEmpty()) {
                    gen.writeObjectFieldStart("Account Permissions");

                    Integer count = 0;
                    for (AccountPermissionDTO accountPermission : accountPermissions) {
                        gen.writeObjectFieldStart((++count).toString());

                        gen.writeStringField("Account Number", accountPermission.getAccountNumber());
                        gen.writeStringField("Account Name", accountPermission.getAccountName());
                        gen.writeStringField("Account Permission", accountPermission.getPermission().name());

                        gen.writeEndObject();
                    }
                    gen.writeEndObject();
                }


                gen.writeEndObject();
            }
        };
    }

    @Override
    public String toString() {
        return "CorporateUserDTO{" +
                "id=" + super.getId() +
                ", version=" + version +
                ", corporateId='" + corporateId + '\'' +
                ", corporateType='" + corporateType + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", userName='" + userName + '\'' +
                ", entrustId='" + entrustId + '\'' +
                ", entrustGroup='" + entrustGroup + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", admin=" + admin +
                ", authorizer=" + authorizer +
                ", authorizerLevel='" + authorizerLevel + '\'' +
                ", userType='" + userType + '\'' +
                ", corpUserType=" + corpUserType +
                ", designation='" + designation + '\'' +
                ", roleId='" + roleId + '\'' +
                ", role='" + role + '\'' +
                ", ruleMember=" + ruleMember +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", expiryDate=" + expiryDate +
                ", lockedUntilDate=" + lockedUntilDate +
                ", lastLoginDate=" + lastLoginDate +
                ", lastLogin='" + lastLoginDate + '\'' +
                ", noOfLoginAttempts=" + noOfLoginAttempts +
                ", alertPreference=" + alertPreference +
                ", createdOn='" + createdOnDate + '\'' +
                ", corporateRoleId=" + corporateRoleId +
                ", corporateRole='" + corporateRole + '\'' +
                ", securityQuestion=" + securityQuestion +
                ", securityAnswer=" + securityAnswer +
                ", phishingSec='" + phishingSec + '\'' +
                ", captionSec='" + captionSec + '\'' +
                ", isFirstTimeLogon='" + isFirstTimeLogon + '\'' +
                ", accountPermissions='" + accountPermissions + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((super.getId() == null) ? 0 : super.getId().hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CorporateUserDTO other = (CorporateUserDTO) obj;
        if (super.getId() == null) {
            if (other.getId() != null) return false;
        } else if (!super.getId().equals(other.getId())) return false;
        if (userName == null) {
            return other.userName == null;
        } else return userName.equals(other.userName);
    }

    @JsonIgnore
    @Override
    public <T> JsonSerializer<T> getAuditSerializer() {
        return null;
    }
}
