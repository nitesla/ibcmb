package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Where(clause = "delFlag='N'")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDataStore extends AbstractEntity {


    @Column(name = "item_seq_no")
    private String itemSequenceNo;

    private String serialNo;

    private String sortCode;

    private String accountNo;

    private String tranCode;

    private BigDecimal amount;

    private String currency;
    @Column(name = "bnk_frt_dep_date")
    private LocalDateTime bankOfFirstDepositDate;
    @Column(name = "bnk_fst_dep_cod")
    private String bankOfFirstDepositSortCode;
    @Column(name = "pstmt_date")
    private LocalDateTime presentmentDate;

    private String payerName;

    private String beneficiary;
    @Column(name = "ben_acct_no")
    private String beneficiaryAccountNo;
    @Column(name = "bvn_ben")
    private String bvnBeneficiary;

    private String bvnPayer;
    @Column(name = "col_type")
    private String collectionType;
    @Column(name = "inst_type")
    private String instrumentType;

    private String narration;
    @Column(name = "pstmt_bnk_cod")
    private String presentingBankSortCode;
    @Column(name = "special_clrg")
    private boolean specialClearing;
    @Column(name = "inst_date")
    private LocalDateTime instrumentDate;
    @Column(name = "micr_rep_ind")
    private String MICRRepairInd;
    @Column(name = "set_time")
    private LocalDateTime settlementTime;

    private String cycleNo;

    public ItemDataStore() {
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

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCod) {
        this.tranCode = tranCod;
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

    public LocalDateTime getBankOfFirstDepositDate() {
        return bankOfFirstDepositDate;
    }

    public void setBankOfFirstDepositDate(LocalDateTime bankOfFirstDepositDate) {
        this.bankOfFirstDepositDate = bankOfFirstDepositDate;
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

    @Override
    public String toString() {
        return "PFItemDataStore{" +
                "itemSequenceNo='" + itemSequenceNo + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", sortCode='" + sortCode + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", tranCod='" + tranCode + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", bankOfFirstDepositDate=" + bankOfFirstDepositDate +
                ", bankOfFirstDepositSortCode='" + bankOfFirstDepositSortCode + '\'' +
                ", presentmentDate=" + presentmentDate +
                ", payerName='" + payerName + '\'' +
                ", beneficiary='" + beneficiary + '\'' +
                ", beneficiaryAccountNo='" + beneficiaryAccountNo + '\'' +
                ", bvnBeneficiary='" + bvnBeneficiary + '\'' +
                ", bvnPayer='" + bvnPayer + '\'' +
                ", collectionType='" + collectionType + '\'' +
                ", instrumentType='" + instrumentType + '\'' +
                ", naration='" + narration + '\'' +
                ", presentingBankSortCode='" + presentingBankSortCode + '\'' +
                ", specialClearing=" + specialClearing +
                ", instrumentDate=" + instrumentDate +
                ", MICRRepairInd='" + MICRRepairInd + '\'' +
                ", settlementTime=" + settlementTime +
                ", cycleNo='" + cycleNo + '\'' +
                '}';
    }
}
