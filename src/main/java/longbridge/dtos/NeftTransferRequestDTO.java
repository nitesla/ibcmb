package longbridge.dtos;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

public class NeftTransferRequestDTO implements Serializable {
    private String beneficiaryAccountNumber;
    private String beneficiaryAccountName;
    private String beneficiaryBVN;
    private String beneficiaryBankName;
    private String beneficiarySortCode;
    private String instrumentType;
    @NotEmpty(message = "Choose a collect Type")
    private String collectionType;
    @NotEmpty(message = "Amount can not be empty")
    private String amount;
    private String narration;
    private String payerName;
    private String currencyCode;
    private Date tranDate = new Date();
    private String customerAccountNumber;
    private String transferType;
    private String charge;
    private String channel;
    private String beneficiaryCurrencyCode;
    private String beneficiaryType;


    public NeftTransferRequestDTO() {
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    public String getBeneficiarySortCode() {
        return beneficiarySortCode;
    }

    public void setBeneficiarySortCode(String beneficiarySortCode) {
        this.beneficiarySortCode = beneficiarySortCode;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
    public String toString() {
        return "NeftTransferRequestDTO{" +
                "beneficiaryAccNo='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryAccName='" + beneficiaryAccountName + '\'' +
                ", beneficiaryBVN='" + beneficiaryBVN + '\'' +
                ", beneficiaryBankName='" + beneficiaryBankName + '\'' +
                ", beneficiarySortCode='" + beneficiarySortCode + '\'' +
                ", instrumentType='" + instrumentType + '\'' +
                ", collectionType='" + collectionType + '\'' +
                ", amount='" + amount + '\'' +
                ", narration='" + narration + '\'' +
                ", payerName='" + payerName + '\'' +
                ", currency='" + currencyCode + '\'' +
                ", tranDate=" + tranDate +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", transferType='" + transferType + '\'' +
                ", charge='" + charge + '\'' +
                ", channel='" + channel + '\'' +
                ", beneficiaryCurrencyCode='" + beneficiaryCurrencyCode + '\'' +
                ", beneficiaryType='" + beneficiaryType + '\'' +
                '}';
    }
}
