package longbridge.dtos;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class NeftTransferRequestDTO {
    private String beneficiaryAccNo;
    private String beneficiaryAccName;
    private String beneficiaryBVN;
    private String beneficiaryBankName;
    private String instrumentType;
    @NotEmpty(message = "Choose a collect Type")
    private String collectionType;
    @NotEmpty(message = "Amount can not be empty")
    private String amount;
    private String narration;
    private String payerName;
    private String currency;
    private Date tranDate = new Date();
    private String customerAccountNumber;
    private String transferType;
    private String charge;

    public NeftTransferRequestDTO() {
    }

    public String getBeneficiaryAccNo() {
        return beneficiaryAccNo;
    }

    public void setBeneficiaryAccNo(String beneficiaryAccNo) {
        this.beneficiaryAccNo = beneficiaryAccNo;
    }

    public String getBeneficiaryAccName() {
        return beneficiaryAccName;
    }

    public void setBeneficiaryAccName(String beneficiaryAccName) {
        this.beneficiaryAccName = beneficiaryAccName;
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

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "NeftTransferRequestDTO{" +
                "beneficiaryAccNo='" + beneficiaryAccNo + '\'' +
                ", beneficiaryAccName='" + beneficiaryAccName + '\'' +
                ", beneficiaryBVN='" + beneficiaryBVN + '\'' +
                ", beneficiaryBankName='" + beneficiaryBankName + '\'' +
                ", instrumentType='" + instrumentType + '\'' +
                ", collectionType='" + collectionType + '\'' +
                ", amount='" + amount + '\'' +
                ", narration='" + narration + '\'' +
                ", payerName='" + payerName + '\'' +
                ", currency='" + currency + '\'' +
                ", tranDate=" + tranDate +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", transferType='" + transferType + '\'' +
                ", charge='" + charge + '\'' +
                '}';
    }
}
