package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.repositories.CorporateRepo;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.util.*;

/**
 * Created by Fortune on 3/29/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Corporate extends AbstractEntity implements PrettySerializer{


    private String rcNumber;
    private String customerId;
    private String corporateType;
    private String name;
    private String email;
    private String address;
    private String status ;
    private Date createdOnDate;
    private String bvn;



    @OneToMany
    Set<CorporateRole> corporateRoles = new HashSet<CorporateRole>();


    @OneToMany(mappedBy = "corporate",cascade = CascadeType.ALL)
    private List<CorporateUser> users =  new ArrayList<CorporateUser>();

//    @OneToMany
//    private Collection<Beneficiary> beneficiaries;

    @OneToMany @JsonIgnore
    private Collection<CorpLimit> corpLimits;

    @OneToMany(mappedBy = "corporate")
    @JsonIgnore
    List<CorpTransRequest> corpTransferRequests;

    @OneToMany(cascade = CascadeType.ALL)
    List<CorpTransRule> corpTransRules;

    public Collection<CorpLimit> getCorpLimits() {
        return corpLimits;
    }

    public void setCorpLimits(Collection<CorpLimit> corpLimits) {
        this.corpLimits = corpLimits;
    }

    public List<CorpTransRequest> getCorpTransferRequests() {
        return corpTransferRequests;
    }

    public void setCorpTransferRequests(List<CorpTransRequest> corpTransferRequests) {
        this.corpTransferRequests = corpTransferRequests;
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

//	public Collection<Beneficiary> getBeneficiaries() {
//		return beneficiaries;
//	}
//
//	public void setBeneficiaries(Collection<Beneficiary> beneficiaries) {
//		this.beneficiaries = beneficiaries;
//	}


    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    @Override
    public String toString() {
        return "Corporate{" +
                "rcNumber='" + rcNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", corporateType='" + corporateType + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", createdOnDate=" + createdOnDate +
                ", bvn='" + bvn + '\'' +
                ", users=" + users +
                ", corpLimits=" + corpLimits +
                ", corpTransferRequests=" + corpTransferRequests +
                ", corpTransRules=" + corpTransRules +
                '}';
    }

    @Override @JsonIgnore
    public JsonSerializer<Corporate> getSerializer() {
        return new JsonSerializer<Corporate>() {
            @Override
            public void serialize(Corporate value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("Name",value.name);
                gen.writeStringField("Customer Id",value.customerId);
                gen.writeStringField("RC Number",value.rcNumber);
                gen.writeEndObject();
            }
        };
    }

}
