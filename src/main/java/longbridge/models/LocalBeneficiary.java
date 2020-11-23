package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;

/**
 * Created by Wunmi on 31/03/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class LocalBeneficiary extends Beneficiary implements PrettySerializer{

    @ManyToOne
    private RetailUser user;

    public RetailUser getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LocalBeneficiary{" + super.toString() + "}";
    }

    @Override @JsonIgnore
    public JsonSerializer<LocalBeneficiary> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(LocalBeneficiary value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<LocalBeneficiary> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(LocalBeneficiary value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.getId() != null) {
                    gen.writeStringField("id", value.getId().toString());
                } else {
                    gen.writeStringField("id", null);
                }
                gen.writeStringField("accountName", value.getAccountName());
                gen.writeStringField("beneficiaryBank", value.getBeneficiaryBank());
                gen.writeStringField("accountNumber", value.getAccountNumber());
                gen.writeStringField("preferredName", value.getPreferredName());
                gen.writeEndObject();
            }
        };
    }
}
