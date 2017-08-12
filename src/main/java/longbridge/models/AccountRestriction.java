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
import java.util.Date;

/**
 * Created by Fortune on 4/27/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class AccountRestriction extends AbstractEntity implements PrettySerializer{

    private String restrictionType;
    private String restrictionValue;
    private String restrictedFor;
    private Date dateCreated;

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }


    public String getRestrictionValue() {
        return restrictionValue;
    }

    public void setRestrictionValue(String restrictionValue) {
        this.restrictionValue = restrictionValue;
    }

    public String getRestrictedFor() {
        return restrictedFor;
    }

    public void setRestrictedFor(String restrictedFor) {
        this.restrictedFor = restrictedFor;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Override
    public String toString() {
        return "AccountRestriction{" +
                "restrictionType='" + restrictionType + '\'' +
                ", restrictionValue='" + restrictionValue + '\'' +
                ", restrictedFor='" + restrictedFor + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }

    @Override @JsonIgnore
    public JsonSerializer<AccountRestriction> getSerializer() {
        return new JsonSerializer<AccountRestriction>() {
            @Override
            public void serialize(AccountRestriction value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("Restriction Value",value.restrictionValue);
                gen.writeStringField("Restriction Type",value.restrictionType);
                gen.writeStringField("Restriction For",value.restrictedFor);
                gen.writeEndObject();
            }
        };
    }
}
