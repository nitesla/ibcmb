package longbridge.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpNeftBeneficiary extends AbstractEntity implements PrettySerializer {
    private String beneficiaryBVN;
    private String beneficiaryAccountName;
    private String beneficiarySortCode;
    private String beneficiaryAccountNumber;
    private String beneficiaryBankName;

    @ManyToOne
    private Corporate corporate;

    public CorpNeftBeneficiary() {
    }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccName) {
        this.beneficiaryAccountName = beneficiaryAccName;
    }

    public String getBeneficiarySortCode() {
        return beneficiarySortCode;
    }

    public void setBeneficiarySortCode(String beneficiarySortCode) {
        this.beneficiarySortCode = beneficiarySortCode;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccNo) {
        this.beneficiaryAccountNumber = beneficiaryAccNo;
    }

    public String getBeneficiaryBVN() {
        return beneficiaryBVN;
    }

    public void setBeneficiaryBVN(String beneficiaryBVN) {
        this.beneficiaryBVN = beneficiaryBVN;
    }

    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    @Override
    public JsonSerializer<CorpNeftBeneficiary> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorpNeftBeneficiary value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
            }
        };
    }

    @Override
    public JsonSerializer<CorpNeftBeneficiary> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorpNeftBeneficiary value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                if (value.getId() != null) {
                    gen.writeStringField("id", value.getId().toString());
                } else {
                    gen.writeStringField("id", null);
                }
                gen.writeStringField("beneficiaryAccountName", value.getBeneficiaryAccountName());
                gen.writeStringField("beneficiaryBankSortCode", value.getBeneficiarySortCode());
                gen.writeStringField("beneficiaryAccountNumber", value.getBeneficiaryAccountNumber());
                gen.writeStringField("beneficiaryAccountBVN", value.getBeneficiaryAccountNumber());
                gen.writeStringField("beneficiaryBankName", value.getBeneficiaryBankName());
                gen.writeEndObject();
            }
        };
    }
}
