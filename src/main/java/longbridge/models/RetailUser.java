package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Wunmi on 3/28/2017.
 * RetailUser is a bank customer. Typically with a single identity operating a set of accounts.
 */


@Entity
@Audited(withModifiedFlag = true)
@AuditOverride(forClass = User.class)
@Where(clause = "del_Flag='N'")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userName"}))
public class RetailUser extends User implements PrettySerializer {

    private String bvn;
    private String customerId;
    private Date birthDate;
    private String resetSecurityQuestion;
    private String feedBackStatus;

    @OneToMany
    @JsonIgnore
    private Collection<RetailCustLimit> rtlCustLmt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<LocalBeneficiary> beneficiaries;

    private String tempPassword;

    @OneToMany(mappedBy = "retailUser")
    @JsonIgnore
    private List<BulkTransfer> transfers;

    @OneToMany
    @JsonIgnore
    private List<Coverage> coverages;


    public RetailUser() {
        this.userType = (UserType.RETAIL);
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getResetSecurityQuestion() {
        return resetSecurityQuestion;
    }

    public void setResetSecurityQuestion(String resetSecurityQuestion) {
        this.resetSecurityQuestion = resetSecurityQuestion;
    }

    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    public Collection<RetailCustLimit> getRtlCustLmt() {
        return rtlCustLmt;
    }

    public void setRtlCustLmt(Collection<RetailCustLimit> rtlCustLmt) {
        this.rtlCustLmt = rtlCustLmt;
    }

    public Collection<LocalBeneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(Collection<LocalBeneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public String getFeedBackStatus() {
        return feedBackStatus;
    }

    public void setFeedBackStatus(String feedBackStatus) {
        this.feedBackStatus = feedBackStatus;
    }

    @Override
    @JsonIgnore
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("customerId", "userName", "firstName", "lastName");
    }

    public List<BulkTransfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<BulkTransfer> transfers) {
        this.transfers = transfers;
    }

    public List<Coverage> getCoverages() {
        return coverages;
    }

    public void setCoverages(List<Coverage> coverages) {
        this.coverages = coverages;
    }

    @Override
    @JsonIgnore
    public JsonSerializer<User> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(User value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {

                gen.writeStartObject();
                gen.writeStringField("Username", value.userName);
                gen.writeStringField("First Name", value.firstName);
                gen.writeStringField("Last Name", value.lastName);
                gen.writeStringField("Email", value.email);
                gen.writeStringField("Phone", value.phoneNumber);
                String status = null;
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

    @Override
    @JsonIgnore
    public JsonSerializer<User> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(User value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {

                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("userName", value.firstName);
                gen.writeStringField("firstName", value.firstName);
                gen.writeStringField("lastName", value.lastName);
                gen.writeStringField("email", value.email);
                gen.writeStringField("phoneNumber", value.phoneNumber);
                String status = null;
                if ("A".equals(value.status))
                    status = "Active";
                else if ("I".equals(value.status))
                    status = "Inactive";
                else if ("L".equals(value.status))
                    status = "Locked";
                gen.writeStringField("status", status);
                gen.writeStringField("role", value.role.getName());
                gen.writeEndObject();
            }
        };
    }

}
