package longbridge.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import javax.persistence.FetchType;
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
    @JsonIgnore
    Set<CorporateRole> corporateRoles = new HashSet<CorporateRole>();

    @OneToMany(mappedBy = "corporate")
    @JsonIgnore
    private List<CorporateUser> users =  new ArrayList<CorporateUser>();

    @OneToMany
    @JsonIgnore
    private Collection<CorpLimit> corpLimits;


    @OneToMany
    @JsonIgnore
    List<CorpTransRule> corpTransRules;

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

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }



    @Override
	public String toString() {
		return "Corporate [rcNumber=" + rcNumber + ", customerId=" + customerId + ", corporateType=" + corporateType
				+ ", name=" + name + ", email=" + email + ", address=" + address + ", status=" + status
				+ ", createdOnDate=" + createdOnDate + ", bvn=" + bvn + "]";
	}

	@Override @JsonIgnore
   	public List<String> getDefaultSearchFields() {
   		return Arrays.asList("name", "rcNumber","customerId");
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
                gen.writeStringField("Type",value.corporateType);
                gen.writeStringField("RC Number",value.rcNumber);
                gen.writeStringField("Status",getStatusDescription(value.status));

                gen.writeObjectFieldStart("User");

                for(CorporateUser user: users){
                    if(user.getId()!=null) {
                        gen.writeObjectFieldStart(user.getId().toString());
                    }

                    gen.writeStringField("Username", user.userName);
                    gen.writeStringField("First Name", user.firstName);
                    gen.writeStringField("Last Name", user.lastName);
                    gen.writeStringField("Email", user.email);
                    gen.writeStringField("Phone", user.phoneNumber);
                    gen.writeStringField("Status",getStatusDescription(user.status));

                    if(user.getId()!=null) {
                        gen.writeEndObject();
                    }
                }
                gen.writeEndObject();
                gen.writeEndObject();
            }
        };
    }

    private  String getStatusDescription(String status){
        String description =null;
        if ("A".equals(status))
            description = "Active";
        else if ("I".equals(status))
            description = "Inactive";
        else if ("L".equals(status))
            description = "Locked";
        return description;
    }
}
