package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * Created by Fortune on 4/25/2017.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
//@Table(uniqueConstraints=@UniqueConstraint(columnNames="channel"))
public class AccountLimit extends  AbstractEntity {

    private String customerType;
    private String description;
    private String accountNumber;
    private String channel;
    private double lowerLimit;
    private double upperLimit;
    private String currency;
    private String status;
    private String frequency;
    private Date effectiveDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public String toString() {
        return "AccountLimit{" +
                "customerType='" + customerType + '\'' +
                ", description='" + description + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", channel='" + channel + '\'' +
                ", lowerLimit=" + lowerLimit +
                ", upperLimit=" + upperLimit +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", frequency='" + frequency + '\'' +
                ", effectiveDate=" + effectiveDate +
                '}';
    }
}
