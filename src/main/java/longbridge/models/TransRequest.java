package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import longbridge.utils.TransferType;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**The {@code Transfer} class model represents a single transfer transaction.
 * This model can be used to represent intra-bank and inter-bank transfers
 * @author Fortunatus Ekenachi
 * Created on 3/30/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class TransRequest extends AbstractEntity implements PrettySerializer {


    private  String customerAccountNumber;
    private TransferType transferType;
    private Date tranDate = new Date();
    @ManyToOne
    private FinancialInstitution financialInstitution;
    private String beneficiaryAccountNumber;
    private String beneficiaryAccountName;
    private String remarks;
    private String status;
    private String referenceNumber;
    private String userReferenceNumber;
    private String narration;
    private String statusDescription;
    private BigDecimal amount;
    private String charge;

    public TransRequest() {
    }


    public TransRequest(String customerAccountNumber, TransferType transferType, Date tranDate, FinancialInstitution financialInstitution, String beneficiaryAccountNumber, String beneficiaryAccountName, String remarks, String status, String referenceNumber, String userReferenceNumber, String narration, String statusDescription, BigDecimal amount) {
        this.customerAccountNumber = customerAccountNumber;
        this.transferType = transferType;
        this.tranDate = tranDate;
        this.financialInstitution = financialInstitution;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.beneficiaryAccountName = beneficiaryAccountName;
        this.remarks = remarks;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.userReferenceNumber = userReferenceNumber;
        this.narration = narration;
        this.statusDescription = statusDescription;
        this.amount = amount;
    }


    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public FinancialInstitution getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(FinancialInstitution financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccountName) {
        this.beneficiaryAccountName = beneficiaryAccountName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getUserReferenceNumber() {
        return userReferenceNumber;
    }

    public void setUserReferenceNumber(String userReferenceNumber) {
        this.userReferenceNumber = userReferenceNumber;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransRequest{" +
                "customerAccountNumber='" + customerAccountNumber + '\'' +
                ", transferType=" + transferType +
                ", tranDate=" + tranDate +
                ", financialInstitution=" + financialInstitution +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryAccountName='" + beneficiaryAccountName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", status='" + status + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", userReferenceNumber='" + userReferenceNumber + '\'' +
                ", narration='" + narration + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", amount=" + amount +
                ", charge='" + charge + '\'' +
                '}';
    }

    @Override
    @JsonIgnore
    public <T> JsonSerializer<T> getSerializer() {
        return null;
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
                gen.writeStringField("customerAccountNumber", value.customerAccountNumber);
                if(value.transferType !=null) {
                    gen.writeStringField("transferType", value.transferType.toString());
                }else {
                    gen.writeStringField("transferType", "");
                }
                if(value.tranDate !=null) {
                    gen.writeStringField("tranDate", value.tranDate.toString());
                }else {
                    gen.writeStringField("tranDate", "");
                }
                gen.writeStringField("beneficiaryAccountNumber", value.beneficiaryAccountNumber);
                gen.writeStringField("beneficiaryAccountName", value.beneficiaryAccountName);
                gen.writeStringField("remarks", value.remarks);
                gen.writeStringField("status", value.status);
                gen.writeStringField("referenceNumber", value.referenceNumber);
                gen.writeStringField("userReferenceNumber", value.userReferenceNumber);
                gen.writeStringField("narration", value.narration);
                gen.writeStringField("statusDescription", value.statusDescription);
                if(value.amount != null){
                    gen.writeStringField("amount", value.amount.toString());
                }else {
                    gen.writeStringField("amount", "");
                }
                if(value.charge != null){
                    gen.writeStringField("charge", value.charge.toString());
                }else {
                    gen.writeStringField("charge", "");
                }

                gen.writeEndObject();
            }
        };
    }

}

