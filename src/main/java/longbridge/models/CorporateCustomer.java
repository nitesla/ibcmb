package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Fortune on 3/29/2017.
 */
@Entity
public class CorporateCustomer extends AbstractEntity{

    private String rcNumber;
    private String companyName;
    private String email;
    private String address;

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CorporateCustomer{" +
                "rcNumber='" + rcNumber + '\'' +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

}
