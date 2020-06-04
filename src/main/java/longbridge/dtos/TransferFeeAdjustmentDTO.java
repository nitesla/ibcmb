package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferFeeAdjustmentDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private String feeDescription;
    private String feeRange;
    private String fixedAmount;
    private String fixedAmountValue;
    private String rate;
    private String rateValue;
    private String transactionChannel;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
    }

    public String getFeeRange() {
        return feeRange;
    }

    public void setFeeRange(String feeRange) {
        this.feeRange = feeRange;
    }

    public String getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(String fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public String getFixedAmountValue() {
        return fixedAmountValue;
    }

    public void setFixedAmountValue(String fixedAmountValue) {
        this.fixedAmountValue = fixedAmountValue;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getTransactionChannel() {
        return transactionChannel;
    }

    public void setTransactionChannel(String transactionChannel) {
        this.transactionChannel = transactionChannel;
    }

    @Override
    public String toString() {
        return "TransferFeeAdjustmentDTO{" +
                "id=" + id +
                ", feeDescription='" + feeDescription + '\'' +
                ", feeRange='" + feeRange + '\'' +
                ", fixedAmount='" + fixedAmount + '\'' +
                ", fixedAmountValue='" + fixedAmountValue + '\'' +
                ", rate='" + rate + '\'' +
                ", rateValue='" + rateValue + '\'' +
                ", transactionChannel='" + transactionChannel + '\'' +
                '}';
    }
}
