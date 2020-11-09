package longbridge.models;


import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.math.BigDecimal;


@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class QuickTermination extends AbstractEntity  {

    @OneToOne(cascade = CascadeType.ALL)
    @Transient
    private AccountReceivable accountReceivable;
    private BigDecimal amount;
    private String countryCode;
    private String currencyCode;
    private String entityCode;
    private String paymentMethodCode;



    public AccountReceivable getAccountReceivable() {
        return accountReceivable;
    }

    public void setAccountReceivable(AccountReceivable accountReceivable) {
        this.accountReceivable = accountReceivable;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "QuickTermination{" +
                "accountReceivable=" + accountReceivable +
                ", amount=" + amount +
                ", countryCode='" + countryCode + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", entityCode='" + entityCode + '\'' +
                ", paymentMethodCode='" + paymentMethodCode + '\'' +
                '}';
    }
}
