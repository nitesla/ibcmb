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



    @Column(name = "item_seq_no")
    private String itemSequenceNo;

    private String serialNo;

    private String sortCode;

    private String accountNo;

    private String tranCode;

    private BigDecimal amount;

    private String currency;

    @Column(name = "bnk_fst_dep_dat")
    private String bankOfFirstDepositDate;

    @Column(name = "bnk_fst_dep_cod")
    private String bankOfFirstDepositSortCode;

    @Column(name = "pstmt_date")
    private String presentmentDate;

    @Column(name = "pay_name")
    private String payerName;

    private String beneficiary;

    @Column(name = "ben_acct")
    private String beneficiaryAccountNo;

    @Column(name = "bvn_ben")
    private String BVNBeneficiary;

    private String BVNPayer;

    @Column(name = "col_type")
    private String collectionType;

    @Column(name = "inst_type")
    private String instrumentType;

    private String narration;

    @Column(name = "pstmt_bnk_cod")
    private String presentingBankSortCode;

    @Column(name = "spcl_clrg")
    private boolean specialClearing;

    @Column(name = "inst_date")
    private String instrumentDate;

    @Column(name = "micr_rep_ind")
    private String MICRRepairInd;

    @Column(name = "set_time")
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
