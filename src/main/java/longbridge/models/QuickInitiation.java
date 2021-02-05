package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class QuickInitiation extends AbstractEntity{

    private BigDecimal amount;
    private String channel;
    @Column(name = "curcy_cod")
    private String currencyCode;
    @Column(name = "pay_met_cod")
    private String paymentMethodCode;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    @Override
    public String toString() {
        return "QuickInitiation{" +
                "amount='" + amount + '\'' +
                ", channel='" + channel + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", paymentMethodCode='" + paymentMethodCode + '\'' +
                '}';
    }
}
