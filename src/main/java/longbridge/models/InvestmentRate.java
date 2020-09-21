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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"investmentName","tenor","minAmount","maxAmount"}))

public class InvestmentRate extends AbstractEntity implements PrettySerializer{

    private String investmentName;
    private int tenor;
    private BigDecimal value;
    private int maxAmount;
    private int minAmount;


    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "InvestmentRate{" +
                "investmentName='" + investmentName + '\'' +
                ", tenor='" + tenor + '\'' +
                ", value='" + value + '\'' +
                ", maxAmount=" + maxAmount +
                ", minAmount=" + minAmount +
                '}';
    }

    @Override
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("investmentName", "tenor","description");
    }

    @Override @JsonIgnore
    public JsonSerializer<InvestmentRate> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(InvestmentRate value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("InvestmentName", value.investmentName);
                gen.writeStringField("Value", value.value.toString());
                gen.writeStringField("Tenor", String.valueOf(value.tenor));
                gen.writeStringField("MaxAmount", String.valueOf(value.maxAmount));
                gen.writeStringField("MinAmount", String.valueOf(value.minAmount));
                gen.writeEndObject();
            }
        };
    }
    @Override @JsonIgnore
    public JsonSerializer<InvestmentRate> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(InvestmentRate value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("InvestmentName", value.investmentName);
                gen.writeStringField("Value", value.value.toString());
                gen.writeStringField("Tenor", String.valueOf(value.tenor));
                gen.writeStringField("MaxAmount", String.valueOf(value.maxAmount));
                gen.writeStringField("MinAmount", String.valueOf(value.minAmount));
                gen.writeEndObject();
            }
        };
    }

}
