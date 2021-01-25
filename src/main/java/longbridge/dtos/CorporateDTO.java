package longbridge.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */

public class CorporateDTO implements Serializable{

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String rcNumber;
    private String corporateType;
    private String corporateId;
    private String corporateName;
    @NotEmpty(message = "customerId")
    private String customerId;
    private String customerName;
    private String name;
    private String email;
    private String phoneNumber;
    private String status;
    private String address;
    private Date createdOnDate;
    private String bvn;
    private String taxId;
    private List<String> coverageCodes;



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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public List<String> getCoverageCodes() {
        return coverageCodes;
    }

    public void setCoverageCodes(List<String> coverageCodes) {
        this.coverageCodes = coverageCodes;
    }



    @Override
    public String toString() {
        return "CorporateDTO{" +
                "id=" + id +
                ", version=" + version +
                ", rcNumber='" + rcNumber + '\'' +
                ", corporateType='" + corporateType + '\'' +
                ", corporateId='" + corporateId + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                ", address='" + address + '\'' +
                ", createdOnDate=" + createdOnDate +
                ", bvn='" + bvn + '\'' +
                ", taxId='" + taxId + '\'' +
                ", coverageCodes=" + coverageCodes +
                '}';
    }
}
