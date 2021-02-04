package longbridge.models;

import longbridge.response.NeftResponse;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.StringJoiner;

@Entity
public class CorpNeftTransfer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;



    private String itemSequenceNo;

    private String serialNo;

    private String sortCode;

    private String accountNo;

    private String tranCode;

    private BigDecimal amount;

    private String currency;

    @Column(name = "bnkFistDepDate")
    private String bankOfFirstDepositDate;

    @Column(name = "bnkFistDepSortCode")
    private String bankOfFirstDepositSortCode;

    private String presentmentDate;

    private String payerName;

    private String beneficiary;

    private String beneficiaryAccountNo;

    private String BVNBeneficiary;

    private String BVNPayer;

    private String collectionType;

    private String instrumentType;

    private String narration;

    private String presentingBankSortCode;

    private boolean specialClearing;

    private String instrumentDate;

    private String MICRRepairInd;

    private String settlementTime;

    private String cycleNo;

    @ManyToOne
    private Corporate corporate;

    @OneToOne
    private NeftResponse neftResponse;

    // Getter Methods


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
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

    public String getBankOfFirstDepositDate() {
        return bankOfFirstDepositDate;
    }

    public void setBankOfFirstDepositDate(String bankOfFirstDepositDate) {
        this.bankOfFirstDepositDate = bankOfFirstDepositDate;
    }

    public String getBankOfFirstDepositSortCode() {
        return bankOfFirstDepositSortCode;
    }

    public void setBankOfFirstDepositSortCode(String bankOfFirstDepositSortCode) {
        this.bankOfFirstDepositSortCode = bankOfFirstDepositSortCode;
    }

    public String getPresentmentDate() {
        return presentmentDate;
    }

    public void setPresentmentDate(String presentmentDate) {
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

    public String getBVNBeneficiary() {
        return BVNBeneficiary;
    }

    public void setBVNBeneficiary(String BVNBeneficiary) {
        this.BVNBeneficiary = BVNBeneficiary;
    }

    public String getBVNPayer() {
        return BVNPayer;
    }

    public void setBVNPayer(String BVNPayer) {
        this.BVNPayer = BVNPayer;
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

    public String getInstrumentDate() {
        return instrumentDate;
    }

    public void setInstrumentDate(String instrumentDate) {
        this.instrumentDate = instrumentDate;
    }

    public String getMICRRepairInd() {
        return MICRRepairInd;
    }

    public void setMICRRepairInd(String MICRRepairInd) {
        this.MICRRepairInd = MICRRepairInd;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getCycleNo() {
        return cycleNo;
    }

    public void setCycleNo(String cycleNo) {
        this.cycleNo = cycleNo;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public NeftResponse getNeftResponse() {
        return neftResponse;
    }

    public void setNeftResponse(NeftResponse neftResponse) {
        this.neftResponse = neftResponse;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CorpNeftTransfer.class.getSimpleName() + "[", "]")
                .add("Id=" + Id)
                .add("itemSequenceNo='" + itemSequenceNo + "'")
                .add("serialNo='" + serialNo + "'")
                .add("sortCode='" + sortCode + "'")
                .add("accountNo='" + accountNo + "'")
                .add("tranCode='" + tranCode + "'")
                .add("currency='" + currency + "'")
                .add("payerName='" + payerName + "'")
                .toString();
    }
}
