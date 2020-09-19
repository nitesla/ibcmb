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
import java.util.Arrays;
import java.util.List;

/**
 * The {@code FinancialInstitution} class model contains details of a bank or any
 * other financial institution that participates in electronic transfers
 * @author Fortunatus Ekenachi
 * Created on 3/30/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"institutionCode","institutionType","deletedOn"}))
public class FinancialInstitution extends AbstractEntity implements PrettySerializer{


    private String sortCode;

    private String institutionCode;

    private FinancialInstitutionType institutionType;

    private String institutionName;

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public FinancialInstitutionType getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(FinancialInstitutionType institutionType) {
        this.institutionType = institutionType;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    @Override
    public String toString() {
        return "FinancialInstitution{" +
                "sortCode='" + sortCode + '\'' +
                ", institutionCode='" + institutionCode + '\'' +
                ", institutionType=" + institutionType +
                ", institutionName='" + institutionName + '\'' +
                '}';
    }

    @Override
   	public List<String> getDefaultSearchFields() {
   		return Arrays.asList("institutionCode","institutionName","sortCode");
   	}


    @Override @JsonIgnore
    public JsonSerializer<FinancialInstitution> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(FinancialInstitution value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Institution Code", value.institutionCode);
                gen.writeStringField("Institution Name", value.institutionName);
                gen.writeStringField("Institution Type", value.institutionType.name());
                gen.writeEndObject();
            }
        };
    }
    @Override @JsonIgnore
    public JsonSerializer<FinancialInstitution> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(FinancialInstitution value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("institutionCode", value.institutionCode);
                gen.writeStringField("institutionName", value.institutionName);
                gen.writeStringField("institutionType", value.institutionType.name());
                gen.writeEndObject();
            }
        };
    }

}

