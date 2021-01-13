package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;

/**
 * Created by Fortune on 3/29/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"corporateId"}))
public class Corporate extends AbstractEntity implements PrettySerializer{

    private String rcNumber;
    private String customerId;
    private String corporateType;
    private String corporateId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String status ;
    private Date createdOnDate;
    private String bvn;
    private String taxId;

    private String coverage;

    @ElementCollection
    private Set<String> cifids;

    @OneToMany(mappedBy = "corporate")
    @JsonIgnore
    private List<BulkTransfer> transfers;


    @OneToMany
    @JsonIgnore
    private Set<CorporateRole> corporateRoles;

    @OneToMany(mappedBy = "corporate")
    @JsonIgnore
    private List<CorporateUser> users;

    @OneToMany
    @JsonIgnore
    private Collection<CorpLimit> corpLimits;

    @OneToMany
    @JsonIgnore
    private List<CorpTransRule> corpTransRules;



    @ManyToMany
    @JoinTable(name = "corporate_account", joinColumns =
    @JoinColumn(name = "corporate_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "account_id", referencedColumnName = "id") )
    private List<Account> accounts;



    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<BulkTransfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<BulkTransfer> transfers) {
        this.transfers = transfers;
    }

    public Collection<CorpLimit> getCorpLimits() {
        return corpLimits;
    }

    public void setCorpLimits(Collection<CorpLimit> corpLimits) {
        this.corpLimits = corpLimits;
    }

    public Set<CorporateRole> getCorporateRoles() {
        return corporateRoles;
    }

    public void setCorporateRoles(Set<CorporateRole> corporateRoles) {
        this.corporateRoles = corporateRoles;
    }

    public List<CorpTransRule> getCorpTransRules() {
        return corpTransRules;
    }

    public void setCorpTransRules(List<CorpTransRule> corpTransRules) {
        this.corpTransRules = corpTransRules;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public String getCorporateType() {
        return corporateType;
    }

    public void setCorporateType(String corporateType) {
        this.corporateType = corporateType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

	public List<CorporateUser> getUsers() {
		return users;
	}

	public void setUsers(List<CorporateUser> users) {
		this.users = users;
	}

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public Set<String> getCifids() {
        return cifids;
    }

    public void setCifids(Set<String> cifids) {
        this.cifids = cifids;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    @Override
	public String toString() {
		return "Corporate [rcNumber=" + rcNumber + ", customerId=" + customerId + ", corporateType=" + corporateType
				+ ", name=" + name + ", email=" + email + ", address=" + address + ", status=" + status
				+ ", createdOnDate=" + createdOnDate + ", bvn=" + bvn + ", coverage=" + coverage + "]";
	}

	@Override @JsonIgnore
   	public List<String> getDefaultSearchFields() {
   		return Arrays.asList("name","customerId","corporateId","corporateType");
   	}

    @Override @JsonIgnore
    public JsonSerializer<Corporate> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Corporate value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Name", value.name);
                gen.writeStringField("Type", value.corporateType);
                gen.writeStringField("CIF ID", value.customerId);
                gen.writeStringField("Corporate ID", value.corporateId);
                gen.writeStringField("RC Number", value.rcNumber);
                gen.writeEndObject();
            }
        };
    }
    @Override @JsonIgnore
    public JsonSerializer<Corporate> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Corporate value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("name", value.name);
                gen.writeStringField("corporateType", value.corporateType);
                gen.writeStringField("customerId", value.customerId);
                gen.writeStringField("corporateID", value.corporateId);
                gen.writeStringField("RcNumber", value.rcNumber);
                gen.writeStringField("email", value.email);
                gen.writeStringField("address", value.address);
                gen.writeStringField("status", value.status);
                gen.writeStringField("createdOnDate", value.createdOnDate.toString());
                gen.writeStringField("bvn", value.bvn);
                gen.writeStringField("coverage", value.coverage);
                gen.writeEndObject();
            }
        };
    }


}
