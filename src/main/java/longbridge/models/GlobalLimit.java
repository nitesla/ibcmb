package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Fortune on 4/25/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class GlobalLimit extends  AbstractEntity implements PrettySerializer{

    private String customerType;
    private String description;
    private String channel;
    private BigDecimal maxLimit;
    private String currency;
    private String status;
    private String frequency;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BigDecimal getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(BigDecimal maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override @JsonIgnore
    public JsonSerializer<GlobalLimit> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(GlobalLimit value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Channel", value.channel);
                gen.writeNumberField("Maximum Limit", value.maxLimit);
                gen.writeStringField("Currency", value.currency);
                gen.writeStringField("Frequency", value.frequency);
                gen.writeStringField("Description", value.description);
                gen.writeEndObject();
            }
        };
    }
    @Override @JsonIgnore
    public JsonSerializer<GlobalLimit> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(GlobalLimit value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("channel", value.channel);
                gen.writeStringField("status", value.status);
                gen.writeStringField("customerType", value.customerType);
                gen.writeNumberField("maximumLimit", value.maxLimit);
                gen.writeStringField("currency", value.currency);
                gen.writeStringField("frequency", value.frequency);
                gen.writeStringField("description", value.description);
                gen.writeEndObject();
            }
        };
    }
    }
