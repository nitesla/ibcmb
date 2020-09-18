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
import java.io.IOException;
import java.util.Date;

/**
 * Created by Fortune on 4/27/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class AccountClassRestriction extends AbstractEntity implements PrettySerializer{

    private String accountClass;
    private String restrictionType;
    private Date dateCreated;

    public String getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(String accountClass) {
        this.accountClass = accountClass;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Override
    public String toString() {
        return "AccountClassRestriction{" +
                "accountClass='" + accountClass + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }



    @Override @JsonIgnore
    public JsonSerializer<AccountClassRestriction> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(AccountClassRestriction value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Account Class", value.accountClass);
                gen.writeStringField("Restriction Type", value.restrictionType);
                gen.writeEndObject();
            }
        };
    }
    @Override @JsonIgnore
    public JsonSerializer<AccountClassRestriction> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(AccountClassRestriction value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("accountClass", value.accountClass);
                gen.writeStringField("restrictionType", value.restrictionType);
                gen.writeStringField("dateCreated", value.dateCreated.toString());
                gen.writeEndObject();
            }
        };
    }

}
