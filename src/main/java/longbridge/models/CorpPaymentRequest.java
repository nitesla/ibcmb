package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpPaymentRequest extends TransRequest implements PrettySerializer {

    @ManyToOne
    @JsonIgnore
    private Corporate corporate;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private  CorpTransferAuth transferAuth;

    @OneToOne(cascade = {CascadeType.ALL})
    private CustomDutyPayment customDutyPayment;

    public CustomDutyPayment getCustomDutyPayment() {
        return customDutyPayment;
    }

    public void setCustomDutyPayment(CustomDutyPayment customDutyPayment) {
        this.customDutyPayment = customDutyPayment;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Column(unique = true)
    private String refCode;

    private  String status;

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public CorpTransferAuth getTransferAuth() {
        return transferAuth;
    }

    public void setTransferAuth(CorpTransferAuth transferAuth) {
        this.transferAuth = transferAuth;
    }
    @Override
    @JsonIgnore
    public JsonSerializer<TransRequest> getAuditSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(TransRequest value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {

                gen.writeStartObject();
                if (value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                } else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("customerAccountNumber", value.getCustomerAccountNumber());
                if (value.getTransferType() != null) {
                    gen.writeStringField("transferType", value.getTransferType().toString());
                } else {
                    gen.writeStringField("transferType", "");
                }
                if (value.getTranDate() != null) {
                    gen.writeStringField("tranDate", value.getTranDate().toString());
                } else {
                    gen.writeStringField("tranDate", "");
                }
                gen.writeStringField("beneficiaryAccountNumber", value.getBeneficiaryAccountNumber());
                gen.writeStringField("beneficiaryAccountName", value.getBeneficiaryAccountName());
                gen.writeStringField("remarks", value.getRemarks());
                gen.writeStringField("status", value.getStatus());
                gen.writeStringField("referenceNumber", value.getReferenceNumber());
                gen.writeStringField("userReferenceNumber", value.getUserReferenceNumber());
                gen.writeStringField("narration", value.getNarration());
                gen.writeStringField("statusDescription", value.getStatusDescription());
                if (value.getAmount() != null) {
                    gen.writeStringField("amount", value.getAmount().toString());
                } else {
                    gen.writeStringField("amount", "");
                }
                if (value.getCharge() != null) {
                    gen.writeStringField("charge", value.getCharge());
                } else {
                    gen.writeStringField("charge", "");
                }

                gen.writeEndObject();
            }
        };
    }

    @Override @JsonIgnore
    public JsonSerializer<CorpPaymentRequest> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorpPaymentRequest value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Custom Ref", value.refCode);
                gen.writeStringField("Status", value.status);
                gen.writeEndObject();
            }
        };
    }

    @Override
    public List<String> getDefaultSearchFields() {
        return Arrays.asList("status","statusDescription");
    }
}
