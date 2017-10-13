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
import javax.persistence.ManyToOne;
import java.io.IOException;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause="del_Flag='N")
public class CorpInterBen extends Beneficiary implements PrettySerializer{
    @ManyToOne
    private Corporate corporate;
    private String swiftCode;
    private String sortCode;
    private String beneficiaryAddress;
    private String intermediaryBankName;
    private String intermediaryBankAcctNo;


    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    public void setBeneficiaryAddress(String beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress;
    }

    public String getIntermediaryBankName() {
        return intermediaryBankName;
    }

    public void setIntermediaryBankName(String intermediaryBankName) {
        this.intermediaryBankName = intermediaryBankName;
    }

    public String getIntermediaryBankAcctNo() {
        return intermediaryBankAcctNo;
    }

    public void setIntermediaryBankAcctNo(String intermediaryBankAcctNo) {
        this.intermediaryBankAcctNo = intermediaryBankAcctNo;
    }
    @Override @JsonIgnore
    public JsonSerializer<CorpInterBen> getSerializer() {
        return new JsonSerializer<CorpInterBen>() {
            @Override
            public void serialize(CorpInterBen value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<CorpInterBen> getAuditSerializer() {
        return new JsonSerializer<CorpInterBen>() {
            @Override
            public void serialize(CorpInterBen value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                if(value.getId() != null) {
                    gen.writeStringField("id", value.getId().toString());
                }else {
                    gen.writeStringField("id", null);
                }
                gen.writeStringField("swiftCode", value.swiftCode);
                gen.writeStringField("beneficiaryAddress", value.beneficiaryAddress);
                gen.writeStringField("sortCode", value.sortCode);
                gen.writeStringField("intermediaryBankName", value.intermediaryBankName);
                gen.writeStringField("intermediaryBankAcctNo", value.intermediaryBankAcctNo);
                gen.writeStringField("accountName", value.getAccountName());
                gen.writeStringField("beneficiaryBank",value.getBeneficiaryBank());
                gen.writeStringField("accountNumber",value.getAccountNumber());
                gen.writeStringField("preferredName",value.getPreferredName());
                gen.writeEndObject();
            }
        };
    }
}
