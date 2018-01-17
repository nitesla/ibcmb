package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class UserGroup extends AbstractEntity implements PrettySerializer{

    private String name;
    private String description;
    private Date dateCreated;


    @ManyToMany(cascade = CascadeType.ALL)
    private List<OperationsUser> users;

    @JsonProperty
    @OneToMany(cascade = CascadeType.ALL)
    private List<Contact> contacts;


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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }



    public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}


	public List<OperationsUser> getUsers() {
		return users;
	}

	public void setUsers(List<OperationsUser> users) {
		this.users = users;
	}



    @Override
    @JsonIgnore
    public JsonSerializer<UserGroup> getSerializer()
    {
        return new JsonSerializer<UserGroup>() {

            @Override
            public void serialize(UserGroup value, JsonGenerator gen, SerializerProvider arg2)
                    throws IOException, JsonProcessingException {
                gen.writeStartObject();
                gen.writeStringField("Group Name", value.name);
//                gen.writeStringField("Description",value.description);



                gen.writeObjectFieldStart("Internal Users");
                Integer count = 0;
                for(OperationsUser user : value.users){
                    gen.writeObjectFieldStart((++count).toString());
                    //gen.writeStartObject();
                    gen.writeStringField("First Name",user.firstName);
                    gen.writeStringField("Last Name",user.lastName);
                    gen.writeStringField("Email",user.email);
                    gen.writeEndObject();
                }
                gen.writeEndObject();

                gen.writeObjectFieldStart("External Users");
                for(Contact contact : value.contacts){
                    gen.writeObjectFieldStart((++count).toString());
                    //gen.writeStartObject();
                    gen.writeStringField("First Name",contact.firstName);
                    gen.writeStringField("Last Name",contact.lastName);
                    gen.writeStringField("Email",contact.email);
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
    public JsonSerializer<UserGroup> getAuditSerializer()
    {
        return new JsonSerializer<UserGroup>() {

            @Override
            public void serialize(UserGroup value, JsonGenerator gen, SerializerProvider arg2)
                    throws IOException, JsonProcessingException {
                gen.writeStartObject();
                if(value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                }else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("groupName", value.name);
//                gen.writeStringField("Description",value.description);



                gen.writeObjectFieldStart("internalUsers");
                Integer count = 0;
                for(OperationsUser user : value.users){
                    gen.writeObjectFieldStart((++count).toString());
                    //gen.writeStartObject();
                    gen.writeStringField("firstName",user.firstName);
                    gen.writeStringField("lastName",user.lastName);
                    gen.writeStringField("email",user.email);
                    gen.writeEndObject();
                }
                gen.writeEndObject();

                gen.writeObjectFieldStart("externalUsers");
                for(Contact contact : value.contacts){
                    gen.writeObjectFieldStart((++count).toString());
                    //gen.writeStartObject();
                    gen.writeStringField("firstName",contact.firstName);
                    gen.writeStringField("lastName",contact.lastName);
                    gen.writeStringField("email",contact.email);
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }


}
