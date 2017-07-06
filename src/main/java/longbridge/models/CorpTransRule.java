package longbridge.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause = "del_Flag='N'")
public class CorpTransRule extends AbstractEntity implements PrettySerializer{

    private BigDecimal lowerLimitAmount;
    private BigDecimal upperLimitAmount;
    private String currency;
    private boolean unlimited;
    private boolean anyCanAuthorize;
    private boolean rank;


    @ManyToOne
    private Corporate corporate;

    @ManyToMany
    private List<CorporateRole> roles;

    @ManyToMany
    private List<CorpTransRole> tranRoles;

    public BigDecimal getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(BigDecimal lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public BigDecimal getUpperLimitAmount() {
        return upperLimitAmount;
    }

    public void setUpperLimitAmount(BigDecimal upperLimitAmount) {
        this.upperLimitAmount = upperLimitAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isAnyCanAuthorize() {
        return anyCanAuthorize;
    }

    public void setAnyCanAuthorize(boolean anyCanAuthorize) {
        this.anyCanAuthorize = anyCanAuthorize;
    }

    public boolean isUnlimited() {
        return unlimited;
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }


    public List<CorporateRole> getRoles() {
        return roles;
    }

    public void setRoles(List<CorporateRole> roles) {
        this.roles = roles;
    }

    public List<CorpTransRole> getTranRoles() {
        return tranRoles;
    }

    public void setTranRoles(List<CorpTransRole> tranRoles) {
        this.tranRoles = tranRoles;
    }

    public boolean isRank() {
        return rank;
    }

    public void setRank(boolean rank) {
        this.rank = rank;
    }


    @Override
    public JsonSerializer<CorpTransRule> getSerializer() {
        return new JsonSerializer<CorpTransRule>() {

            @Override
            public void serialize(CorpTransRule value, JsonGenerator gen, SerializerProvider arg2)
                    throws IOException, JsonProcessingException {
                gen.writeStartObject();
                gen.writeStringField("Corporate", value.corporate.getName());
                gen.writeNumberField("Lower Amount",value.lowerLimitAmount);
                gen.writeNumberField("Upper Amount",value.lowerLimitAmount);
                gen.writeStringField("Currency", value.currency);
                gen.writeBooleanField("Unlimited", value.unlimited);
                gen.writeBooleanField("Any Role", value.anyCanAuthorize);
                gen.writeBooleanField("Follow Rank", value.rank);


                gen.writeObjectFieldStart("Participating Roles");
                for(CorporateRole role : value.roles){
                    gen.writeObjectFieldStart(role.getId().toString());
                    //gen.writeStartObject();
                    gen.writeStringField("Name",role.name);
                    gen.writeNumberField("Rank",role.rank);
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }

}
