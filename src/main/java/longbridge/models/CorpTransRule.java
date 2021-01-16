package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
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

    public boolean isRank() {
        return rank;
    }

    public void setRank(boolean rank) {
        this.rank = rank;
    }


    @Override
    @JsonIgnore
    public JsonSerializer<CorpTransRule> getSerializer() {
        return new JsonSerializer<>() {

            @Override
            public void serialize(CorpTransRule value, JsonGenerator gen, SerializerProvider arg2)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Corporate", value.corporate.getName());
                gen.writeNumberField("Lower Amount", value.lowerLimitAmount);
                if (value.unlimited) {
                    gen.writeStringField("Upper Amount", "No Upper Limit");

                } else {
                    gen.writeNumberField("Upper Amount", value.upperLimitAmount);

                }
                gen.writeStringField("Currency", value.currency);
                if (value.anyCanAuthorize) {
                    gen.writeStringField("Authorizers Required", "ANY");
                } else {
                    gen.writeStringField("Authorizers Required", "ALL");

                }


                gen.writeObjectFieldStart("Authorizer Levels");
                for (CorporateRole role : value.roles) {
                    gen.writeObjectFieldStart(role.getId().toString());
                    //gen.writeStartObject();
                    gen.writeStringField("Name", role.getName());
                    gen.writeNumberField("Rank", role.getRank());
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }

    @Override
    @JsonIgnore
    public JsonSerializer<CorpTransRule> getAuditSerializer() {
        return new JsonSerializer<>() {

            @Override
            public void serialize(CorpTransRule value, JsonGenerator gen, SerializerProvider arg2)
                    throws IOException {
                gen.writeStartObject();
//                gen.writeStringField("corporate", value.corporate.getName());
                gen.writeNumberField("lowerAmount", value.lowerLimitAmount);
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                if (value.unlimited) {
                    gen.writeStringField("upperAmount", "No Upper Limit");

                } else {
                    gen.writeNumberField("upperAmount", value.upperLimitAmount);

                }
                gen.writeStringField("currency", value.currency);
                if (value.anyCanAuthorize) {
                    gen.writeStringField("authorizersRequired", "ANY");
                } else {
                    gen.writeStringField("authorizersRequired", "ALL");

                }


                gen.writeObjectFieldStart("authorizerLevels");
                for (CorporateRole role : value.roles) {
                    gen.writeObjectFieldStart(role.getId().toString());
                    //gen.writeStartObject();
                    gen.writeStringField("name", role.getName());
                    gen.writeNumberField("rank", role.getRank());
                    gen.writeEndObject();
                }
                gen.writeEndObject();
                //gen.writeEndArray();
                gen.writeEndObject();
            }
        };
    }

}
