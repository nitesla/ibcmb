package longbridge.api;

/**
 * Created by ayoade_farooq@yahoo.com on 4/24/2017.
 */
public class CustomerDetails {

private String cifId;
private String  customerName;
private String   dateOfBirth;
private String   email;
private String phone;


    public CustomerDetails() {
    }

    public CustomerDetails(String cifId, String customerName, String dateOfBirth, String email, String phone) {
        this.cifId = cifId;
        this.customerName = customerName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
    }


    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
