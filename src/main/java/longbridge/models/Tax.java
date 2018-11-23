package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tax extends AbstractEntity{

    @JsonProperty("TaxCode")
    private String taxCode;

    @JsonProperty("TaxAmount")
    private String taxAmount;

    @JsonProperty("TaxDesc")
    private String taxDesc;

    @JsonIgnore
    @ManyToOne
    private CustomDutyPayment customDutyPayment;

    public Tax() {
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxDesc() {
        return taxDesc;
    }

    public void setTaxDesc(String taxDesc) {
        this.taxDesc = taxDesc;
    }

    public CustomDutyPayment getCustomDutyPayment() {
        return customDutyPayment;
    }

    public void setCustomDutyPayment(CustomDutyPayment customDutyPayment) {
        this.customDutyPayment = customDutyPayment;
    }

    @Override
    public String toString() {
        return "CustomTaxDetail{" +
                "taxCode='" + taxCode + '\'' +
                ", taxAmount='" + taxAmount + '\'' +
                ", taxDesc='" + taxDesc + '\'' +
                '}';
    }
}
