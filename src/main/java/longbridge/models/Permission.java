package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.io.IOException;

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
                gen.writeStringField("name",value.name);
                gen.writeStringField("description",value.description);
                gen.writeStringField("userType",value.userType);
                gen.writeStringField("category",value.category);
                gen.writeEndObject();
            }
        };
    }



}
