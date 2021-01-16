package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tax extends AbstractEntity implements PrettySerializer {

    @JsonProperty("TaxCode")
    private String taxCode;

    @JsonProperty("TaxAmount")
    private String taxAmount;

    @JsonProperty("TaxDesc")
    private String taxDesc;

    @JsonIgnore
    @ManyToOne
    private CustomDutyPayment customDutyPayment;

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxDesc() {
        return taxDesc;
    }

    public void setTaxDesc(String taxDesc) {
        this.taxDesc = taxDesc;
    }

    public CustomDutyPayment getCustomDutyPayment() {
        return customDutyPayment;
    }

    public void setCustomDutyPayment(CustomDutyPayment customDutyPayment) {
        this.customDutyPayment = customDutyPayment;
    }

    @Override
    public String toString() {
        return "CustomTaxDetail{" +
                "taxCode='" + taxCode + '\'' +
                ", taxAmount='" + taxAmount + '\'' +
                ", taxDesc='" + taxDesc + '\'' +
                '}';
    }

    @Override @JsonIgnore
    public JsonSerializer<Tax> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Tax value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Amount", value.taxAmount);
                gen.writeStringField("Tax Code", value.taxCode);
                gen.writeStringField("Tax Desc.", value.taxDesc);
                gen.writeEndObject();
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<Tax> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Tax value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("Amount", value.taxAmount);
                gen.writeStringField("Tax Code", value.taxCode);
                gen.writeStringField("Tax Desc.", value.taxDesc);
                gen.writeEndObject();
            }
        };
    }

}
