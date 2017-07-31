package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 7/20/2017.
 */
public class CorporateRequestDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String corporateType;
    @NotEmpty(message = "corporateName")
    private String corporateName;
    @NotEmpty(message = "corporateId")
    private String corporateId;
    private String bvn;
    private  String status;
    private String rcNumber;
    @NotEmpty(message = "customerId")
    private String customerId;
    private String customerName;
    private String createdOn;
    private List<AuthorizerDTO> authorizers = new ArrayList<>();
    private List<CorporateUserDTO> corporateUsers = new ArrayList<>();
    private List<CorpTransferRuleDTO> corpTransferRules = new ArrayList<>();
    private List<AccountDTO> accounts = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCorporateType() {
        return corporateType;
    }

    public void setCorporateType(String corporateType) {
        this.corporateType = corporateType;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public List<AuthorizerDTO> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(List<AuthorizerDTO> authorizers) {
        this.authorizers = authorizers;
    }

    public List<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public List<CorporateUserDTO> getCorporateUsers() {
        return corporateUsers;
    }

    public void setCorporateUsers(List<CorporateUserDTO> corporateUsers) {
        this.corporateUsers = corporateUsers;
    }

    public List<CorpTransferRuleDTO> getCorpTransferRules() {
        return corpTransferRules;
    }

    public void setCorpTransferRules(List<CorpTransferRuleDTO> corpTransferRules) {
        this.corpTransferRules = corpTransferRules;
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
}
