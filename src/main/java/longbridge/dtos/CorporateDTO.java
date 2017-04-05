package longbridge.dtos;


/**
 * Created by Fortune on 4/5/2017.
 */

public class CorporateDTO {

    private Long id;
    private String rcNumber;
    private String customerId;
    private String companyName;
    private String email;
    private String address;


    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

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


}
