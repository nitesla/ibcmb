package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by mac on 23/02/2018.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpRecurringPayment extends RecurringPayment {

    @ManyToOne
    CorporateUser corporateUser;

    Long corporate ;


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


    @Override
    public String toString() {
        return "CorpRecurringPayment{" +
                "corporate=" + corporate +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", amount=" + amount +
                ", intervalDays=" + intervalDays +
                ", nextDebitDate=" + nextDebitDate +
                ", dateCreated=" + dateCreated +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", narration='" + narration + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", paymentItemId='" + paymentItemId + '\'' +
                ", paymentItemName='" + paymentItemName + '\'' +
                ", billerId='" + billerId + '\'' +
                ", billerName='" + billerName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                ", terminalId='" + terminalId + '\'' +
                ", paymentCode=" + paymentCode +
                ", customerId='" + customerId + '\'' +
                ", requestReference='" + requestReference + '\'' +
                ", token='" + token + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", authenticate=" + authenticate +
                ", payments=" + payments +
                ", corporateUser=" + corporateUser +
                ", corporate=" + corporate +
                ", id=" + id +
                '}';
    }
}
