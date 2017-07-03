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

    private String accountNumber;
    private String restrictionType;
    private Date dateCreated;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
    public String
    toString() {
        return "AccountRestriction{" +
                "accountNumber='" + accountNumber + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
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
                gen.writeStringField("Account Number",value.accountNumber);
                gen.writeStringField("Restriction Type",value.restrictionType);


                gen.writeEndObject();
            }
        };
    }
}
