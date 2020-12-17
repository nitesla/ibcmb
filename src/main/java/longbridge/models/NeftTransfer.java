package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import longbridge.response.NeftResponse;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class NeftTransfer extends AbstractEntity {


//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long Id;


    @Column(name = "itemsequenceno")
    private String ItemSequenceNo;
    @Column(name = "serialno")
    private String SerialNo;
    @Column(name = "sortcode")
    private String SortCode;
    @Column(name = "accountno")
    private String AccountNo;
    @Column(name = "trancode")
    private String TranCode;
    @Column(name = "amount")
    private BigDecimal Amount;
    @Column(name = "currency")
    private String Currency;
    @Column(name = "bankoffirstdepositdate")
    private String BankOfFirstDepositDate;
    @Column(name = "bankoffirstdepositsortcode")
    private String BankOfFirstDepositSortCode;
    @Column(name = "presentmentdate")
    private String PresentmentDate;
    @Column(name = "payername")
    private String PayerName;
    @Column(name = "beneficiary")
    private String Beneficiary;
    @Column(name = "beneficiaryacctno")
    private String BeneficiaryAccountNo;
    @Column(name = "bvnbeneficiary")
    private String BVNBeneficiary;
    @Column(name = "bvnpayer")
    private String BVNPayer;
    @Column(name = "collectiontype")
    private String CollectionType;
    @Column(name = "instrumenttype")
    private String InstrumentType;
    @Column(name = "narration")
    private String Narration;
    @Column(name = "presentingbanksortcode")
    private String PresentingBankSortCode;
    @Column(name = "specialclearing")
    private boolean SpecialClearing;
    @Column(name = "instrumentdate")
    private String InstrumentDate;
    @Column(name = "micrepairind")
    private String MICRRepairInd;
    @Column(name = "settlementtime")
    private String SettlementTime;
    @Column(name = "cycleno")
    private String CycleNo;
    @Column(name = "status")
    private String status;
    @Column(name = "beneficiarybank")
    private String beneficiaryBank;

    @ManyToOne
    private RetailUser retailUser;

    @ManyToOne
    @JsonIgnore
    private Corporate corporate;

    @OneToOne
    private NeftResponse neftResponse;

    // Getter Methods
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

    public boolean isSpecialClearing() {
        return SpecialClearing;
    }

    public NeftResponse getNeftResponse() {
        return neftResponse;
    }

    public void setNeftResponse(NeftResponse neftResponse) {
        this.neftResponse = neftResponse;
    }

    public RetailUser getRetailUser() {
        return retailUser;
    }

    public void setRetailUser(RetailUser retailUser) {
        this.retailUser = retailUser;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }

    @Override
    public String toString() {
        return "NeftTransfer{" +
                ", ItemSequenceNo='" + ItemSequenceNo + '\'' +
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
                ", status='" + status + '\'' +
                ", beneficiaryBank='" + beneficiaryBank + '\'' +
                ", retailUser=" + retailUser +
                ", corporate=" + corporate +
                ", neftResponse=" + neftResponse +
                '}';
    }
}
