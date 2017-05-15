package longbridge.dtos;

import java.math.BigDecimal;
import java.util.Date;

import longbridge.models.Account;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;

public class DirectDebitDTO {

		private Long id;
	    private LocalBeneficiary beneficiary;
	    private Account debitAccount;
	    private BigDecimal amount;
	    private int intervalDays;
	    private Date nextDebitDate;
	    private Date dateCreated;
	    private RetailUser retailUser;


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

	    
	}
