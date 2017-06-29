package longbridge.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import longbridge.dtos.RoleDTO;

import javax.persistence.*;

import java.io.IOException;
import java.util.Collection;

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

    //    public Collection<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(Collection<User> users) {
//        this.users = users;
//    }


    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + permissions +
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


	@Override @JsonIgnore
    public JsonSerializer<Role> getSerializer() {
        return new JsonSerializer<Role>() {
            @Override
            public void serialize(Role value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException {

                gen.writeStartObject();
                gen.writeStringField("name", value.name);
                gen.writeStringField("email", value.email);
                // gen.writeArrayFieldStart("permissions");
                gen.writeObjectFieldStart("permissions");
                for(Permission p : value.permissions){
                    gen.writeObjectFieldStart(p.getId().toString());
                    //gen.writeStartObject();
                    gen.writeStringField("Name", p.getName());
                    gen.writeStringField("Category", p.getCategory());
                    gen.writeStringField("Description", p.getDescription());
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }



}
