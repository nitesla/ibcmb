package longbridge.models;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class NeftTransfer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;


    @Column(name = "itemsequenceno", nullable = true)
    private String ItemSequenceNo;
    @Column(name = "serialno", nullable = true)
    private String SerialNo;
    @Column(name = "sortcode", nullable = true)
    private String SortCode;
    @Column(name = "accountno", nullable = true)
    private String AccountNo;
    @Column(name = "trancode", nullable = true)
    private String TranCode;
    @Column(name = "amount", nullable = true)
    private BigDecimal Amount;
    @Column(name = "currency", nullable = true)
    private String Currency;
    @Column(name = "bankoffirstdepositdate", nullable = true)
    private String BankOfFirstDepositDate;
    @Column(name = "bankoffirstdepositsortcode", nullable = true)
    private String BankOfFirstDepositSortCode;
    @Column(name = "presentmentdate", nullable = true)
    private String PresentmentDate;
    @Column(name = "payername", nullable = true)
    private String PayerName;
    @Column(name = "beneficiary", nullable = true)
    private String Beneficiary;
    @Column(name = "beneficiaryacctno", nullable = true)
    private String BeneficiaryAccountNo;
    @Column(name = "bvnbeneficiary", nullable = true)
    private String BVNBeneficiary;
    @Column(name = "bvnpayer", nullable = true)
    private String BVNPayer;
    @Column(name = "collectiontype", nullable = true)
    private String CollectionType;
    @Column(name = "instrumenttype", nullable = true)
    private String InstrumentType;
    @Column(name = "narration", nullable = true)
    private String Narration;
    @Column(name = "presentingbanksortcode", nullable = true)
    private String PresentingBankSortCode;
    @Column(name = "specialclearing", nullable = true)
    private boolean SpecialClearing;
    @Column(name = "instrumentdate", nullable = true)
    private String InstrumentDate;
    @Column(name = "micrepairind", nullable = true)
    private String MICRRepairInd;
    @Column(name = "settlementtime", nullable = true)
    private String SettlementTime;
    @Column(name = "cycleno", nullable = true)
    private String CycleNo;




    // Getter Methods


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getItemSequenceNo() {
        return ItemSequenceNo;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public String getSortCode() {
        return SortCode;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public String getTranCode() {
        return TranCode;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public String getBankOfFirstDepositDate() {
        return BankOfFirstDepositDate;
    }

    public String getBankOfFirstDepositSortCode() {
        return BankOfFirstDepositSortCode;
    }

    public String getPresentmentDate() {
        return PresentmentDate;
    }

    public String getPayerName() {
        return PayerName;
    }

    public String getBeneficiary() {
        return Beneficiary;
    }

    public String getBeneficiaryAccountNo() {
        return BeneficiaryAccountNo;
    }

    public String getBVNBeneficiary() {
        return BVNBeneficiary;
    }

    public String getBVNPayer() {
        return BVNPayer;
    }

    public String getCollectionType() {
        return CollectionType;
    }

    public String getInstrumentType() {
        return InstrumentType;
    }

    public String getNarration() {
        return Narration;
    }

    public String getPresentingBankSortCode() {
        return PresentingBankSortCode;
    }

    public boolean getSpecialClearing() {
        return SpecialClearing;
    }

    public String getInstrumentDate() {
        return InstrumentDate;
    }

    public String getMICRRepairInd() {
        return MICRRepairInd;
    }

    public String getSettlementTime() {
        return SettlementTime;
    }

    public String getCycleNo() {
        return CycleNo;
    }

    // Setter Methods

    public void setItemSequenceNo(String ItemSequenceNo) {
        this.ItemSequenceNo = ItemSequenceNo;
    }

    public void setSerialNo(String SerialNo) {
        this.SerialNo = SerialNo;
    }

    public void setSortCode(String SortCode) {
        this.SortCode = SortCode;
    }

    public void setAccountNo(String AccountNo) {
        this.AccountNo = AccountNo;
    }

    public void setTranCode(String TranCode) {
        this.TranCode = TranCode;
    }

    public void setAmount(BigDecimal Amount) {
        this.Amount = Amount;
    }

    public void setCurrency(String Currency) {
        this.Currency = Currency;
    }

    public void setBankOfFirstDepositDate(String BankOfFirstDepositDate) {
        this.BankOfFirstDepositDate = BankOfFirstDepositDate;
    }

    public void setBankOfFirstDepositSortCode(String BankOfFirstDepositSortCode) {
        this.BankOfFirstDepositSortCode = BankOfFirstDepositSortCode;
    }

    public void setPresentmentDate(String PresentmentDate) {
        this.PresentmentDate = PresentmentDate;
    }

    public void setPayerName(String PayerName) {
        this.PayerName = PayerName;
    }

    public void setBeneficiary(String Beneficiary) {
        this.Beneficiary = Beneficiary;
    }

    public void setBeneficiaryAccountNo(String BeneficiaryAccountNo) {
        this.BeneficiaryAccountNo = BeneficiaryAccountNo;
    }

    public void setBVNBeneficiary(String BVNBeneficiary) {
        this.BVNBeneficiary = BVNBeneficiary;
    }

    public void setBVNPayer(String BVNPayer) {
        this.BVNPayer = BVNPayer;
    }

    public void setCollectionType(String CollectionType) {
        this.CollectionType = CollectionType;
    }

    public void setInstrumentType(String InstrumentType) {
        this.InstrumentType = InstrumentType;
    }

    public void setNarration(String Narration) {
        this.Narration = Narration;
    }

    public void setPresentingBankSortCode(String PresentingBankSortCode) {
        this.PresentingBankSortCode = PresentingBankSortCode;
    }

    public void setSpecialClearing(boolean SpecialClearing) {
        this.SpecialClearing = SpecialClearing;
    }

    public void setInstrumentDate(String InstrumentDate) {
        this.InstrumentDate = InstrumentDate;
    }

    public void setMICRRepairInd(String MICRRepairInd) {
        this.MICRRepairInd = MICRRepairInd;
    }

    public void setSettlementTime(String SettlementTime) {
        this.SettlementTime = SettlementTime;
    }

    public void setCycleNo(String CycleNo) {
        this.CycleNo = CycleNo;
    }

    @Override
    public String toString() {
        return "NeftTransfer{" +
                "ItemSequenceNo='" + ItemSequenceNo + '\'' +
                ", SerialNo='" + SerialNo + '\'' +
                ", SortCode='" + SortCode + '\'' +
                ", AccountNo='" + AccountNo + '\'' +
                ", TranCode='" + TranCode + '\'' +
                ", Amount=" + Amount +
                ", Currency='" + Currency + '\'' +
                ", BankOfFirstDepositDate='" + BankOfFirstDepositDate + '\'' +
                ", BankOfFirstDepositSortCode='" + BankOfFirstDepositSortCode + '\'' +
                ", PresentmentDate='" + PresentmentDate + '\'' +
                ", PayerName='" + PayerName + '\'' +
                ", Beneficiary='" + Beneficiary + '\'' +
                ", BeneficiaryAccountNo='" + BeneficiaryAccountNo + '\'' +
                ", BVNBeneficiary='" + BVNBeneficiary + '\'' +
                ", BVNPayer='" + BVNPayer + '\'' +
                ", CollectionType='" + CollectionType + '\'' +
                ", InstrumentType='" + InstrumentType + '\'' +
                ", Narration='" + Narration + '\'' +
                ", PresentingBankSortCode='" + PresentingBankSortCode + '\'' +
                ", SpecialClearing=" + SpecialClearing +
                ", InstrumentDate='" + InstrumentDate + '\'' +
                ", MICRRepairInd='" + MICRRepairInd + '\'' +
                ", SettlementTime='" + SettlementTime + '\'' +
                ", CycleNo='" + CycleNo + '\'' +
                '}';
    }
}
