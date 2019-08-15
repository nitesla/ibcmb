package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class BulkTransfer extends TransRequest {

    @Column(unique = true)
    private String refCode;
    private  String status;
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private  CorpTransferAuth transferAuth;

    @OneToMany(mappedBy = "bulkTransfer",cascade = {CascadeType.ALL})
    private List<CreditRequest> crRequestList;

    @ManyToOne
    private Corporate corporate;

    @Transient
    private String tranLocation;



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

    public List<CreditRequest> getCrRequestList() {
		return crRequestList;
	}

	public void setCrRequestList(List<CreditRequest> crRequestList) {
		this.crRequestList = crRequestList;
	}

	public String getRefCode() {
		return refCode;
	}

	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTranLocation() {
        return tranLocation;
    }

    public void setTranLocation(String tranLocation) {
        this.tranLocation = tranLocation;
    }

    public BulkTransfer(String reference, String status, List<CreditRequest> creditRequestList, Corporate corporate) {

        this.refCode = reference;
        this.status = status;
        this.crRequestList = creditRequestList;
        this.corporate = corporate;
    }


    public BulkTransfer() {
    }
    @Override
    @JsonIgnore
    public JsonSerializer<TransRequest> getAuditSerializer() {
        return new JsonSerializer<TransRequest>() {
            @Override
            public void serialize(TransRequest value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException {

                gen.writeStartObject();
                if(value.id != null) {
                    gen.writeStringField("id", value.id.toString());
                }else {
                    gen.writeStringField("id", "");
                }
                gen.writeStringField("customerAccountNumber", value.getCustomerAccountNumber());
                if(value.getTransferType() !=null) {
                    gen.writeStringField("transferType", value.getTransferType().toString());
                }else {
                    gen.writeStringField("transferType", "");
                }
                if(value.getTranDate() !=null) {
                    gen.writeStringField("tranDate", value.getTranDate().toString());
                }else {
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
                BulkTransfer b =  (BulkTransfer) value;
                gen.writeStringField("refCode", b.getRefCode());
                gen.writeStringField("status", b.getStatus());
                if(value.getAmount() != null){
                    gen.writeStringField("amount", value.getAmount().toString());
                }else {
                    gen.writeStringField("amount", "");
                }
                if(value.getCharge() != null){
                    gen.writeStringField("charge", value.getCharge().toString());
                }else {
                    gen.writeStringField("charge", "");
                }

                gen.writeEndObject();
            }
        };
    }

}
