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

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.IOException;

/**
 * Created by Wunmi on 27/03/2017. CorporateUser is a bank customer. Typically
 * with a multiple identities representing an organization operating a set of
 * accounts.
 */
@Entity
@Audited(withModifiedFlag=true)
@AuditOverride(forClass=User.class)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName"}))
public class CorporateUser extends User implements PrettySerializer{

	protected String isFirstTimeLogon = "Y";

	@Enumerated(EnumType.ORDINAL)
	protected CorpUserType corpUserType;

	@Nullable
	private boolean admin ;

	@ManyToOne
	private Corporate corporate;
	private String tempPassword;
	private String resetSecurityQuestion;
	private String feedBackStatus;

	public String getIsFirstTimeLogon() {
        return isFirstTimeLogon;
    }

    public void setIsFirstTimeLogon(String isFirstTimeLogon) {
        this.isFirstTimeLogon = isFirstTimeLogon;
    }

    public CorporateUser(){
		this.userType = (UserType.CORPORATE);
	}

	public Corporate getCorporate() {
		return corporate;
	}

	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public CorpUserType getCorpUserType() {
		return corpUserType;
	}

	public void setCorpUserType(CorpUserType corpUserType) {
		this.corpUserType = corpUserType;
	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o){
		return super.equals(o);
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public String getFeedBackStatus() {
		return feedBackStatus;
	}
	public void setFeedBackStatus(String feedBackStatus) {
		this.feedBackStatus = feedBackStatus;
	}



	@Override
	@JsonIgnore
	public JsonSerializer<CorporateUser> getSerializer() {
		return new JsonSerializer<CorporateUser>() {
			@Override
			public void serialize(CorporateUser value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException, JsonProcessingException {

				gen.writeStartObject();
				gen.writeStringField("Corporate Name", value.corporate.getName());
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
				if("MULTI".equals(corporate.getCorporateType())) {
					if(value.corpUserType!=null) {
						gen.writeStringField("User Type", value.corpUserType.name());
					}
				}
//				if("MULTI".equals(corporate.getCorporateType())) {
//					gen.writeBooleanField("Is Admin", value.admin);
//				}
				gen.writeEndObject();
			}
		};
	}
	@Override
	@JsonIgnore
	public JsonSerializer<CorporateUser> getAuditSerializer() {
		return new JsonSerializer<CorporateUser>() {
			@Override
			public void serialize(CorporateUser value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException, JsonProcessingException {

				gen.writeStartObject();
//				gen.writeStringField("Corporate Name", value.corporate.getName());
				if(value.id != null) {
					gen.writeStringField("id", value.id.toString());
				}else {
					gen.writeStringField("id", "");
				}
				gen.writeStringField("userName", value.userName);
				if(String.valueOf(value.admin) != null) {
					gen.writeStringField("admin", String.valueOf(value.admin));
				}else {
					gen.writeStringField("admin", "");
				}
				if(value.corpUserType != null) {
					gen.writeStringField("corpUserType", value.corpUserType.toString());
				}else {
					gen.writeStringField("corpUserType", "");
				}
				gen.writeStringField("isFirstTimeLogon", value.isFirstTimeLogon);
				gen.writeStringField("firstName", value.firstName);
				gen.writeStringField("lastName", value.lastName);
				gen.writeStringField("email", value.email);
				gen.writeStringField("phoneNumber", value.phoneNumber);
				String status =null;
				if ("A".equals(value.status))
					status = "Active";
				else if ("I".equals(value.status))
					status = "Inactive";
				else if ("L".equals(value.status))
					status = "Locked";
				gen.writeStringField("status", status);
//				gen.writeStringField("Role", value.role.getName());
//				if("MULTI".equals(corporate.getCorporateType())) {
//					if(value.corpUserType!=null) {
//						gen.writeStringField("User Type", value.corpUserType.name());
//					}
//				}
//				if("MULTI".equals(corporate.getCorporateType())) {
//					gen.writeBooleanField("Is Admin", value.admin);
//				}
				gen.writeEndObject();
			}
		};
	}

	public String getResetSecurityQuestion() {
		return resetSecurityQuestion;
	}

	public void setResetSecurityQuestion(String resetSecurityQuestion) {
		this.resetSecurityQuestion = resetSecurityQuestion;
	}
}
