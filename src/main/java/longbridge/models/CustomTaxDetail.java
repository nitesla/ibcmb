package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomTaxDetail {

    @JsonProperty("TaxCode")
    private String taxCode;

    @JsonProperty("TaxAmount")
    private String taxAmount;

    @JsonProperty("TaxDesc")
    private String taxDesc;

    public CustomTaxDetail() {
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

    @Override
    public String toString() {
        return "CustomTaxDetail{" +
                "taxCode='" + taxCode + '\'' +
                ", taxAmount='" + taxAmount + '\'' +
                ", taxDesc='" + taxDesc + '\'' +
                '}';
    }
}
