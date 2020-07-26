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
import java.util.List;

/**
 * The {@code Code} class model represents unique data that can be used for system configurations.
 * This can be used to set up a list of items that can be presented in drop-down menus
 * Example is a country eg Nigeria with code: NG, type: COUNTRY and description: the description if necessary
 * With this approach, new items can be added or removed from a list presented to the user
 * @author Fortunatus Ekenachi
 * Created on 3/29/2017.
 *
 */

@Entity
@Audited(withModifiedFlag=true)
@Table(
		name="code",
		uniqueConstraints=
		@UniqueConstraint(columnNames={"code", "type","deletedOn"})
)
@Where(clause ="del_Flag='N'" )
public class Code extends AbstractEntity implements PrettySerializer{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5786181085941056612L;

	private String code;
    private String type;
    private String description;
    private String extraInfo;

	@OneToMany
	@JsonIgnore
	private List<AccountCoverage> accountCoverage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public List<AccountCoverage> getAccountCoverage() {
		return accountCoverage;
	}

	public void setAccountCoverage(List<AccountCoverage> accountCoverage) {
		this.accountCoverage = accountCoverage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Code other = (Code) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Code{" +
				"code='" + code + '\'' +
				", type='" + type + '\'' +
				", description='" + description + '\'' +
				'}';
	}

	

	@Override @JsonIgnore
	public JsonSerializer<Code> getSerializer() {
		return new JsonSerializer<Code>() {
			@Override
			public void serialize(Code value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException, JsonProcessingException
			{
				gen.writeStartObject();
				gen.writeStringField("Code",value.code);
				gen.writeStringField("Description",value.description);
				gen.writeStringField("Type",value.type);
				gen.writeStringField("Extra Information",value.extraInfo);
				gen.writeEndObject();
			}
		};
	}

	@Override @JsonIgnore
	public JsonSerializer<Code> getAuditSerializer() {
		return new JsonSerializer<Code>() {
			@Override
			public void serialize(Code value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException, JsonProcessingException
			{
				gen.writeStartObject();
				if(value.id != null) {
					gen.writeStringField("id", value.id.toString());
				}else {
					gen.writeStringField("id", "");
				}
				gen.writeStringField("code",value.code);
				gen.writeStringField("description",value.description);
				gen.writeStringField("type",value.type);
				gen.writeStringField("extraInfo",value.extraInfo);
				gen.writeEndObject();
			}
		};
	}

	}
