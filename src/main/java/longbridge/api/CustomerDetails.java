package longbridge.api;

/**
 * Created by ayoade_farooq@yahoo.com on 4/24/2017.
 */
public class CustomerDetails {

    private String cifId;
    private String customerName;
    private String dateOfBirth;
    private String email;
    private String phone;
    private String bvn;


    public CustomerDetails() {
    }

    public CustomerDetails(String cifId, String customerName, String dateOfBirth, String email, String phone, String bvn) {

        this.cifId = cifId;
        this.customerName = customerName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.bvn = bvn;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
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

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
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
