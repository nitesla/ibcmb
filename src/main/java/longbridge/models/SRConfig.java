package longbridge.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.codehaus.jackson.annotate.*;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Wunmi on 08/04/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class SRConfig extends AbstractEntity implements PrettySerializer{

    private String requestName;
    private String requestType;
    private boolean authenticate;
    private Long groupId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ServiceReqFormField> formFields;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<ServiceReqFormField> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<ServiceReqFormField> formFields) {
        this.formFields = formFields;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }



    @Override
    @JsonIgnore
    public JsonSerializer<SRConfig> getSerializer() {
        return new JsonSerializer<SRConfig>() {
            @Override
            public void serialize(SRConfig value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("Service Request Name",value.requestName);
                gen.writeStringField("Request Type",value.requestType);
                gen.writeBooleanField("Authentication",value.authenticate);
                gen.writeObjectFieldStart("Form Fields");

                Integer count =0;
                for(ServiceReqFormField reqFormField: formFields){

                    gen.writeObjectFieldStart((++count).toString());
                    gen.writeStringField("Field Name",reqFormField.getFieldName());
                    gen.writeStringField("Field Type",reqFormField.getFieldType());
                    gen.writeStringField("Field Label",reqFormField.getFieldLabel());
                    gen.writeStringField("Field Data",reqFormField.getTypeData());
                    gen.writeEndObject();
                }
                gen.writeEndObject();
            }
        };
    }
}
