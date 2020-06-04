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

@Entity
@Table(name = "Transfer_Limit")
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class TransferSetLimit extends AbstractEntity implements PrettySerializer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "channel", nullable = true)
    private String channel;
    @Column(name = "customertype", nullable = true)
    private String customerType;
    @Column(name = "delFlag", columnDefinition = "N")
    private String delFlag;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "lowerLimit", nullable = true)
    private String lowerLimit;
    @Column(name = "upperLimit", nullable = true)
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
        return new JsonSerializer<TransferSetLimit>() {
            @Override
            public void serialize(TransferSetLimit value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("channel",value.channel);
                gen.writeStringField("customerType",value.customerType);
                gen.writeStringField("description",value.description);
                gen.writeStringField("lowerLimit",value.lowerLimit);
                gen.writeStringField("upperLimit",value.upperLimit);
                gen.writeStringField("delFlag",value.delFlag);
                gen.writeEndObject();
            }
        };
    }


    @Override @JsonIgnore
    public JsonSerializer<TransferSetLimit> getAuditSerializer() {
        return new JsonSerializer<TransferSetLimit>() {
            @Override
            public void serialize(TransferSetLimit value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                if(value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                }else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("channel",value.channel);
                gen.writeStringField("customerType",value.customerType);
                gen.writeStringField("description",value.description);
                gen.writeStringField("lowerLimit",value.lowerLimit);
                gen.writeStringField("upperLimit",value.upperLimit);
                gen.writeStringField("delFlag",value.delFlag);
                gen.writeEndObject();
            }
        };
    }


}
