package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Role extends AbstractEntity implements PrettySerializer{

    private String name;
    private String email;
    private String description;

    @Enumerated(value = EnumType.STRING)
    private UserType userType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns =
    @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "permission_id", referencedColumnName = "id") )
    private Collection<Permission> permissions;

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions +
                '}';
    }



	@Override @JsonIgnore
    public JsonSerializer<Role> getSerializer() {
        return new JsonSerializer<Role>() {
            @Override
            public void serialize(Role value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException {

                gen.writeStartObject();
                gen.writeStringField("Name", value.name);
                gen.writeStringField("Type",value.userType.name());
                gen.writeStringField("Email", value.email);
                gen.writeStringField("Description", value.description);

                // gen.writeArrayFieldStart("permissions");
                gen.writeObjectFieldStart("Permissions");
                for(Permission p : value.permissions){
                   if(p.getId()!=null) {
                       gen.writeObjectFieldStart(p.getId().toString());
                       //gen.writeStartObject();
                       gen.writeStringField("Name", p.getName());
                       gen.writeStringField("Code", p.getCode());
                       gen.writeStringField("Description", p.getDescription());
                       gen.writeEndObject();
                   }
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }

	@Override
	public List<String> getDefaultSearchFields() {
		return Arrays.asList("name", "description","email");
	}


}
