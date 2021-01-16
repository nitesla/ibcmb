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
 * Created by SYLVESTER on 5/19/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause="del_Flag='N'")
public class CorpLocalBeneficiary extends Beneficiary implements PrettySerializer{
    @ManyToOne
    private Corporate corporate;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }
    @Override @JsonIgnore
    public JsonSerializer<CorpLocalBeneficiary> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorpLocalBeneficiary value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<CorpLocalBeneficiary> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorpLocalBeneficiary value, JsonGenerator gen, SerializerProvider serializers)
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
