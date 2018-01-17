package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import longbridge.utils.PrettySerializer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Fortune on 6/7/2017.
 */

@Entity
@Audited(withModifiedFlag = true)
@Where(clause = "del_Flag='N'")
public class CorporateRole extends AbstractEntity implements PrettySerializer{

    String name;
    Integer rank;
    String roleType;

    @ManyToOne
    Corporate corporate;

    @OneToMany(cascade={CascadeType.ALL})
    @JoinTable(name="corp_role_user")
    @JoinColumn(name="user_id")
    Set<CorporateUser> users = new HashSet<CorporateUser>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public Set<CorporateUser> getUsers() {
        return users;
    }

    public void setUsers(Set<CorporateUser> users) {
        this.users = users;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }


	@Override
    @JsonIgnore
	public JsonSerializer<CorporateRole> getSerializer() {
		return new JsonSerializer<CorporateRole>() {

			@Override
			public void serialize(CorporateRole value, JsonGenerator gen, SerializerProvider arg2)
					throws IOException, JsonProcessingException {
				  gen.writeStartObject();
	                gen.writeStringField("Name", value.name);
	                gen.writeNumberField("Rank",value.rank);
	                gen.writeStringField("Corporate", value.corporate.getName());
	                // gen.writeArrayFieldStart("permissions");
	                gen.writeObjectFieldStart("Members");
	                for(CorporateUser user : value.users){
	                    gen.writeObjectFieldStart(user.getId().toString());
	                    //gen.writeStartObject();
                        gen.writeStringField("Username",user.userName);
                        gen.writeStringField("First Name",user.firstName);
	                    gen.writeStringField("Last Name",user.lastName);
	                    gen.writeEndObject();
	                }
	                gen.writeEndObject();
	                //gen.writeEndArray();
	                gen.writeEndObject();
			}
		};
	}
    @Override
    @JsonIgnore
	public JsonSerializer<CorporateRole> getAuditSerializer() {
		return new JsonSerializer<CorporateRole>() {

			@Override
			public void serialize(CorporateRole value, JsonGenerator gen, SerializerProvider arg2)
					throws IOException, JsonProcessingException {
				  gen.writeStartObject();
                if(value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                }else {
                    gen.writeStringField("id", "");
                }
	                gen.writeStringField("name", value.name);
	                gen.writeNumberField("rank",value.rank);
//	                gen.writeStringField("Corporate", value.corporate.getName());
	                // gen.writeArrayFieldStart("permissions");
	                gen.writeObjectFieldStart("members");
	                for(CorporateUser user : value.users){
	                    gen.writeObjectFieldStart(user.getId().toString());
	                    //gen.writeStartObject();
                        gen.writeStringField("username",user.userName);
                        gen.writeStringField("firstName",user.firstName);
	                    gen.writeStringField("lastName",user.lastName);
	                    gen.writeEndObject();
	                }
	                gen.writeEndObject();
	                //gen.writeEndArray();
	                gen.writeEndObject();
			}
		};
	}
}
