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
    private boolean isCorp;
    private String lastName;
    private String firstName;
    private String middleName;
    private String preferredName;
    private String rcNo;
    private String taxId;



    public CustomerDetails() {
    }


    public CustomerDetails(String cifId, String customerName, String dateOfBirth, String email, String phone, String bvn, String lastName, String firstName, String middleName, String preferredName, boolean isCorp, String rcNo,String taxId) {
        this.cifId = cifId;
        this.customerName = customerName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.bvn = bvn;
        this.isCorp = isCorp;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.preferredName = preferredName;
        this.rcNo=rcNo;
        this.taxId=taxId;
    }


    public boolean isCorp() {
        return isCorp;
    }

    public void setCorp(boolean corp) {
        isCorp = corp;
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

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getRcNo() {
        return rcNo;
    }

    public void setRcNo(String rcNo) {
        this.rcNo = rcNo;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    @Override
    public String toString() {
        return "{\"CustomerDetails\":{"
                + "                        \"cifId\":\"" + cifId + "\""
                + ",                         \"customerName\":\"" + customerName + "\""
                + ",                         \"dateOfBirth\":\"" + dateOfBirth + "\""
                + ",                         \"email\":\"" + email + "\""
                + ",                         \"phone\":\"" + phone + "\""
                + ",                         \"bvn\":\"" + bvn + "\""
                + ",                         \"isCorp\":\"" + isCorp + "\""
                + ",                         \"lastName\":\"" + lastName + "\""
                + ",                         \"firstName\":\"" + firstName + "\""
                + ",                         \"middleName\":\"" + middleName + "\""
                + ",                         \"preferredName\":\"" + preferredName + "\""
                + ",                         \"rcNo\":\"" + rcNo + "\""
                + ",                         \"taxId\":\"" + taxId + "\""
                + "}}";
    }
}
