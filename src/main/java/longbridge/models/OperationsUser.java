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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.IOException;
import java.util.List;

/**
 * Created by Wunmi on 3/28/2017.
 *  OperationsUser is a Staff of the bank tasked with managing customers. Tellers and Customer relationship
 *  manager fall into this group.
 */

@Entity
@Audited(withModifiedFlag=true)
@AuditOverride(forClass=User.class)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))
public class OperationsUser extends User implements Person,PrettySerializer {
	public OperationsUser(){
		this.userType = (UserType.OPERATIONS);
	}

	@JsonIgnore
	@ManyToMany(mappedBy = "users")
	private List<UserGroup> groups;


	@JsonIgnore
	public List<UserGroup> getGroups() {
		return groups;
	}


	@JsonIgnore
	public void setGroups(List<UserGroup> groups) {
		this.groups = groups;
	}


	@Override
	public String toString() {
		return "OperationsUser{"+super.toString()+"}";
	}



	@Override
	@JsonIgnore
	public boolean isExternal() {
		return false;
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
				gen.writeStringField("username", value.userName);
				gen.writeStringField("firstName", value.firstName);
				gen.writeStringField("lastName", value.lastName);
				gen.writeStringField("email", value.email);
				gen.writeStringField("phone", value.phoneNumber);
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
