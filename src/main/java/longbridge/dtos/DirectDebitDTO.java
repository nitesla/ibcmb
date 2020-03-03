package longbridge.dtos;

import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;

import java.math.BigDecimal;
import java.util.Date;


public class DirectDebitDTO {

    private Long id;
    private LocalBeneficiary beneficiary;
    private String debitAccount;
    private BigDecimal amount;
    private int intervalDays;
    private Date nextDebitDate;
    private Date dateCreated;
    private RetailUser retailUser;
    private String narration;
    private String start;
    private String end;
    private Date startDate;
    private Date endDate;
    private CorpLocalBeneficiary corpLocalBeneficiary;






    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalBeneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(LocalBeneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(int intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Date getNextDebitDate() {
        return nextDebitDate;
    }

    public void setNextDebitDate(Date nextDebitDate) {
        this.nextDebitDate = nextDebitDate;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public RetailUser getRetailUser() {
        return retailUser;
    }

    public void setRetailUser(RetailUser retailUser) {
        this.retailUser = retailUser;
    }

	public String getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public CorpLocalBeneficiary getCorpLocalBeneficiary() {
        return corpLocalBeneficiary;
    }

    public void setCorpLocalBeneficiary(CorpLocalBeneficiary corpLocalBeneficiary) {
        this.corpLocalBeneficiary = corpLocalBeneficiary;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }



    @Override
    public String toString() {
        return "DirectDebitDTO{" +
                "id=" + id +
                ", beneficiary=" + beneficiary +
                ", debitAccount='" + debitAccount + '\'' +
                ", amount=" + amount +
                ", intervalDays=" + intervalDays +
                ", nextDebitDate=" + nextDebitDate +
                ", dateCreated=" + dateCreated +
                ", retailUser=" + retailUser +
                ", narration='" + narration + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", corpLocalBeneficiary=" + corpLocalBeneficiary +
                '}';
    }
}
