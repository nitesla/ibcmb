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
public class TransferFeeAdjustment extends AbstractEntity implements PrettySerializer{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


   private String feeDescription;

   private String feeRange;

   private String fixedAmount;

   private String fixedAmountValue;

   private String rate;

   private String rateValue;

   private String transactionChannel;

    private String delFlag;


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
    }

    public String getFeeRange() {
        return feeRange;
    }

    public void setFeeRange(String feeRange) {
        this.feeRange = feeRange;
    }

    public String getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(String fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public String getFixedAmountValue() {
        return fixedAmountValue;
    }

    public void setFixedAmountValue(String fixedAmountValue) {
        this.fixedAmountValue = fixedAmountValue;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getTransactionChannel() {
        return transactionChannel;
    }

    public void setTransactionChannel(String transactionChannel) {
        this.transactionChannel = transactionChannel;
    }



    @Override @JsonIgnore
    public JsonSerializer<TransferFeeAdjustment> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(TransferFeeAdjustment value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("feeDescription", value.feeDescription);
                gen.writeStringField("feeRange", value.feeRange);
                gen.writeStringField("fixedAmount", value.fixedAmount);
                gen.writeStringField("fixedAmountValue", value.fixedAmountValue);
                gen.writeStringField("fixedAmountValue", value.fixedAmountValue);
                gen.writeStringField("rate", value.rate);
                gen.writeStringField("rateValue", value.rateValue);
                gen.writeEndObject();
            }
        };
    }


    @Override @JsonIgnore
    public JsonSerializer<TransferFeeAdjustment> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(TransferFeeAdjustment value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("feeDescription", value.feeDescription);
                gen.writeStringField("feeRange", value.feeRange);
                gen.writeStringField("fixedAmount", value.fixedAmount);
                gen.writeStringField("fixedAmountValue", value.fixedAmountValue);
                gen.writeStringField("fixedAmountValue", value.fixedAmountValue);
                gen.writeStringField("rate", value.rate);
                gen.writeStringField("rateValue", value.rateValue);
                gen.writeEndObject();
            }
        };
    }


}
