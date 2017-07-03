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
import java.util.List;

/**
 * Created by LB-PRJ-020 on 4/5/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"name","deletedOn"}))

public class Setting extends AbstractEntity implements PrettySerializer{

    private String name;
    private String type;
    private String description;
    private String value;
    private boolean enabled ;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "Setting{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Override
   	public List<String> getDefaultSearchFields() {
   		return Arrays.asList("name", "type","description");
   	}

    @Override @JsonIgnore
    public JsonSerializer<Setting> getSerializer() {
        return new JsonSerializer<Setting>() {
            @Override
            public void serialize(Setting value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("Name",value.name);
                gen.writeStringField("Description",value.description);
                gen.writeStringField("Value",value.value);
                gen.writeBooleanField("Enabled",value.enabled);
                gen.writeStringField("Type",value.type);
                gen.writeEndObject();
            }
        };
    }

}
