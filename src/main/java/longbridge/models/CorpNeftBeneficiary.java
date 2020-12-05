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
    private String beneficiaryCurrencyCode;
    private String beneficiaryType;

    @ManyToOne
    Corporate user;

    public CorpNeftBeneficiary() {
    }

    public String getBeneficiaryBVN() {
        return beneficiaryBVN;
    }

    public void setBeneficiaryBVN(String beneficiaryBVN) {
        this.beneficiaryBVN = beneficiaryBVN;
    }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccountName) {
        this.beneficiaryAccountName = beneficiaryAccountName;
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

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryBankName() {
        return beneficiaryBankName;
    }

    public void setBeneficiaryBankName(String beneficiaryBankName) {
        this.beneficiaryBankName = beneficiaryBankName;
    }

    public Corporate getUser() {
        return user;
    }

    public void setUser(Corporate user) {
        this.user = user;
    }

    public String getBeneficiaryCurrencyCode() {
        return beneficiaryCurrencyCode;
    }

    public void setBeneficiaryCurrencyCode(String beneficiaryCurrencyCode) {
        this.beneficiaryCurrencyCode = beneficiaryCurrencyCode;
    }

    public String getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
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
                gen.writeStringField("beneficiaryCurrencyCode", value.getBeneficiaryCurrencyCode());
                gen.writeStringField("beneficiaryType", value.getBeneficiaryType());
                gen.writeEndObject();
            }
        };
    }

    @Override
    public String toString() {
        return "CorpNeftBeneficiary{" +
                "beneficiaryBVN='" + beneficiaryBVN + '\'' +
                ", beneficiaryAccountName='" + beneficiaryAccountName + '\'' +
                ", beneficiarySortCode='" + beneficiarySortCode + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryBankName='" + beneficiaryBankName + '\'' +
                ", beneficiaryCurrencyCode='" + beneficiaryCurrencyCode + '\'' +
                ", beneficiaryType='" + beneficiaryType + '\'' +
                ", user=" + user +
                '}';
    }

    public enum  Type{
        CR("CR"), DB("DB");
        private String type;

        Type(String type){this.type = type;}

        public String getType() {
            return type;
        }
    }
}
