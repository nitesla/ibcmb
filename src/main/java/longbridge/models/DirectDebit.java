package longbridge.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
    private String debitAccount;
    private BigDecimal amount;
    private int intervalDays;
    private Date nextDebitDate;
    private Date dateCreated;
    private String narration;


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


	public void proceedToNextDebitDate(){
		LocalDate previous = LocalDate.fromDateFields(getDateCreated());
		setNextDebitDate(previous.plusDays(getIntervalDays()).toDate());
	}
    
}
