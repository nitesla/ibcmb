package longbridge.models;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

/** A DirectDebit stores information for recurring payments to a specified
 * beneficiary from a user's account.
 * 
 * Created by Chigozirim Torti on 5/8/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class DirectDebit extends AbstractEntity {

	@ManyToOne
	private RetailUser retailUser;
	@ManyToOne
    private LocalBeneficiary beneficiary;
    protected String debitAccount;
    protected BigDecimal amount;
    protected int intervalDays;
    protected Date nextDebitDate;
    protected Date dateCreated;
    protected Date startDate;
    protected Date endDate;
    protected String narration;
    @OneToMany(mappedBy = "directDebit", cascade = CascadeType.ALL)
    protected Collection<Payment> payments;

	@ManyToOne
	CorporateUser corporateUser;

	Long corporate ;

	@ManyToOne
	CorpLocalBeneficiary corpLocalBeneficiary;

	public Long getCorporate() {
		return corporate;
	}

	public void setCorporate(Long corporate) {
		this.corporate = corporate;
	}

	public CorporateUser getCorporateUser() {
		return corporateUser;
	}

	public void setCorporateUser(CorporateUser corporateUser) {
		this.corporateUser = corporateUser;
	}

	public CorpLocalBeneficiary getCorpLocalBeneficiary() {
		return corpLocalBeneficiary;
	}

	public void setCorpLocalBeneficiary(CorpLocalBeneficiary corpLocalBeneficiary) {
		this.corpLocalBeneficiary = corpLocalBeneficiary;
	}







	public RetailUser getRetailUser(){
    	return retailUser;
    }
    
    public void setRetailUser(RetailUser retailUser){
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

	public void proceedToNextDebitDate(){
		LocalDate previous = LocalDate.fromDateFields(getDateCreated());
		setNextDebitDate(previous.plusDays(getIntervalDays()).toDate());
	}

	public Collection<Payment> getPayments() {
		return payments;
	}

	public void setPayments(Collection<Payment> payments) {
		this.payments = payments;
	}

	@Override
	public String toString() {
		return "DirectDebit{" +
				"retailUser=" + retailUser +
				", beneficiary=" + beneficiary +
				", debitAccount='" + debitAccount + '\'' +
				", amount=" + amount +
				", intervalDays=" + intervalDays +
				", nextDebitDate=" + nextDebitDate +
				", dateCreated=" + dateCreated +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", narration='" + narration + '\'' +
				'}';
	}
}
