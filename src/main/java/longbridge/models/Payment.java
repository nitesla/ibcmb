package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by mac on 22/02/2018.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Payment extends AbstractEntity {

    @ManyToOne
    private DirectDebit directDebit;
    private Date debitDate ;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;



   public  enum PaymentStatus{
        PENDING,COMPLETED
    }


    public DirectDebit getDirectDebit() {
        return directDebit;
    }

    public void setDirectDebit(DirectDebit directDebit) {
        this.directDebit = directDebit;
    }

    public Date getDebitDate() {
        return debitDate;
    }

    public void setDebitDate(Date debitDate) {
        this.debitDate = debitDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
