package longbridge.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import javax.persistence.Entity;
import java.io.IOException;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class NeftBank extends AbstractEntity implements PrettySerializer {
    private String bankName;
    private String branchName;
    private String sortCode;

    public NeftBank() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    @Override
    public String toString() {
        return "NeftBank{" +
                "bankName='" + bankName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", sortCode='" + sortCode + '\'' +
                '}';
    }

    @Override
    public JsonSerializer<NeftBank> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(NeftBank value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
            }
        };
    }

    @Override
    public JsonSerializer<NeftBank> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(NeftBank value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.getId() != null) {
                    gen.writeStringField("id", value.getId().toString());
                } else {
                    gen.writeStringField("id", null);
                }
                gen.writeStringField("bankName", value.getBankName());
                gen.writeStringField("branchName", value.getBranchName());
                gen.writeStringField("sortCode", value.getSortCode());
                gen.writeEndObject();
            }
        };
    }
}
