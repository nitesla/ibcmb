package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.IOException;

/**
 * AdminUser is a Staff of the bank tasked with administration of the item system 
 * and it configurations. They don't take part directly in transaction impacting activities
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))

public class AdminUser extends User implements PrettySerializer{

	private String authenticateMethod;

	public String getAuthenticateMethod() {
		return authenticateMethod;
	}

	public void setAuthenticateMethod(String authenticateMethod) {
		this.authenticateMethod = authenticateMethod;
	}
	
	public AdminUser(){

		this.userType = (UserType.ADMIN);

	}


	@Override
	public String toString() {
		return "AdminUser{" +super.toString()+"," +
				"authenticateMethod='" + authenticateMethod + '\'' +
				'}';
	}

	@Override
	@JsonIgnore
	public JsonSerializer<User> getSerializer() {
		return new JsonSerializer<User>() {
			@Override
			public void serialize(User value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException, JsonProcessingException {

				gen.writeStartObject();
				gen.writeStringField("Username", value.userName);
				gen.writeStringField("First Name", value.firstName);
				gen.writeStringField("Last Name", value.lastName);
				gen.writeStringField("Email", value.email);
				gen.writeStringField("Phone", value.phoneNumber);
				String status =null;
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
}
