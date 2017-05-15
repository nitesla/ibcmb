package longbridge.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */

public class CorporateDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String rcNumber;
    private String corporateType;
    private String customerId;
    private String companyName;
    private String email;
    private String status;
    private String address;
    private String createdOn;


    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

    public String getCorporateType() {
        return corporateType;
    }

    public void setCorporateType(String corporateType) {
        this.corporateType = corporateType;
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

    public String getCreatedOn() {

        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
