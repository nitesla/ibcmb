package longbridge.dtos;

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
}
