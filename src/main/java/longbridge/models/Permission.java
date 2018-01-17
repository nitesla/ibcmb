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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Permission extends AbstractEntity  implements PrettySerializer{

    private Long id;
    private String name;
    private String description;
    private String code;
    private String category;
    private String userType;

    public Permission(){

    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

    @Override @JsonIgnore
    public JsonSerializer<Permission> getSerializer() {
        return new JsonSerializer<Permission>() {
            @Override
            public void serialize(Permission value,JsonGenerator gen,SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("Name",value.name);
                gen.writeStringField("Code",value.code);
                gen.writeStringField("Description",value.description);
                gen.writeStringField("User Type",value.userType);
                gen.writeEndObject();
            }
        };
    }
    @Override @JsonIgnore
    public JsonSerializer<Permission> getAuditSerializer() {
        return new JsonSerializer<Permission>() {
            @Override
            public void serialize(Permission value,JsonGenerator gen,SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("id",value.id.toString());
                if(value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                }else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("name",value.name);
                gen.writeStringField("code",value.code);
                gen.writeStringField("category",value.category);
                gen.writeStringField("description",value.description);
                gen.writeStringField("userType",value.userType);
                gen.writeEndObject();
            }
        };
    }

    @Override
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("name", "description","category");
    }

}
