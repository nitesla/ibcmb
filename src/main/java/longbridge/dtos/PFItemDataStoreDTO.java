package longbridge.dtos;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PFItemDataStoreDTO {
    private String itemSequenceNo;
    private String serialNo;
    private String sortCode;
    private String accountNo;
    private String tranCod;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime bankOfFirstDepositDat;
    private String bankOfFirstDepositSortCode;
    private LocalDateTime presentmentDate;
    private String payerName;
    private String beneficiary;
    private String beneficiaryAccountNo;
    private String bvnBeneficiary;
    private String bvnPayer;
    private String collectionType;
    private String instrumentType;
    private String narration;
    private String presentingBankSortCode;
    private boolean specialClearing;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime instrumentDate;
    private String MICRRepairInd;
    private LocalDateTime settlementTime;
    private String cycleNo;

    public PFItemDataStoreDTO() {
    }

    public String getItemSequenceNo() {
        return itemSequenceNo;
    }

    public void setItemSequenceNo(String itemSequenceNo) {
        this.itemSequenceNo = itemSequenceNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getTranCod() {
        return tranCod;
    }

    public void setTranCod(String tranCod) {
        this.tranCod = tranCod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getBankOfFirstDepositDat() {
        return bankOfFirstDepositDat;
    }

    public void setBankOfFirstDepositDat(LocalDateTime bankOfFirstDepositDat) {
        this.bankOfFirstDepositDat = bankOfFirstDepositDat;
    }

    public String getBankOfFirstDepositSortCode() {
        return bankOfFirstDepositSortCode;
    }

    public void setBankOfFirstDepositSortCode(String bankOfFirstDepositSortCode) {
        this.bankOfFirstDepositSortCode = bankOfFirstDepositSortCode;
    }

    public LocalDateTime getPresentmentDate() {
        return presentmentDate;
    }

    public void setPresentmentDate(LocalDateTime presentmentDate) {
        this.presentmentDate = presentmentDate;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getBeneficiaryAccountNo() {
        return beneficiaryAccountNo;
    }

    public void setBeneficiaryAccountNo(String beneficiaryAccountNo) {
        this.beneficiaryAccountNo = beneficiaryAccountNo;
    }

    public String getBvnBeneficiary() {
        return bvnBeneficiary;
    }

    public void setBvnBeneficiary(String bvnBeneficiary) {
        this.bvnBeneficiary = bvnBeneficiary;
    }

    public String getBvnPayer() {
        return bvnPayer;
    }

    public void setBvnPayer(String bvnPayer) {
        this.bvnPayer = bvnPayer;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getPresentingBankSortCode() {
        return presentingBankSortCode;
    }

    public void setPresentingBankSortCode(String presentingBankSortCode) {
        this.presentingBankSortCode = presentingBankSortCode;
    }

    public boolean isSpecialClearing() {
        return specialClearing;
    }

    public void setSpecialClearing(boolean specialClearing) {
        this.specialClearing = specialClearing;
    }

    public LocalDateTime getInstrumentDate() {
        return instrumentDate;
    }

    public void setInstrumentDate(LocalDateTime instrumentDate) {
        this.instrumentDate = instrumentDate;
    }

    public String getMICRRepairInd() {
        return MICRRepairInd;
    }

    public void setMICRRepairInd(String MICRRepairInd) {
        this.MICRRepairInd = MICRRepairInd;
    }

    public LocalDateTime getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(LocalDateTime settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getCycleNo() {
        return cycleNo;
    }

    public void setCycleNo(String cycleNo) {
        this.cycleNo = cycleNo;
    }
}
