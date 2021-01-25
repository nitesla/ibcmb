package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.IOException;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class TransferSetLimit extends AbstractEntity implements PrettySerializer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String channel;

    private String customerType;

    private String delFlag;

    private String description;

    private String lowerLimit;

    private String upperLimit;


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }



    @Override @JsonIgnore
    public JsonSerializer<TransferSetLimit> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(TransferSetLimit value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("channel", value.channel);
                gen.writeStringField("customerType", value.customerType);
                gen.writeStringField("description", value.description);
                gen.writeStringField("lowerLimit", value.lowerLimit);
                gen.writeStringField("upperLimit", value.upperLimit);
                gen.writeStringField("delFlag", value.delFlag);
                gen.writeEndObject();
            }
        };
    }


    @Override @JsonIgnore
    public JsonSerializer<TransferSetLimit> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(TransferSetLimit value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("channel", value.channel);
                gen.writeStringField("customerType", value.customerType);
                gen.writeStringField("description", value.description);
                gen.writeStringField("lowerLimit", value.lowerLimit);
                gen.writeStringField("upperLimit", value.upperLimit);
                gen.writeStringField("delFlag", value.delFlag);
                gen.writeEndObject();
            }
        };
    }


}
