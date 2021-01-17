package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Fortune on 7/20/2017.
 */
public class CorporateRequestDTO extends AbstractDTO implements PrettySerializer {

    private int version;
    private String corporateType;
    @NotEmpty(message = "corporateName")
    private String corporateName;
    @NotEmpty(message = "corporateId")
    private String corporateId;
    private String bvn;
    private String taxId;
    private String status;
    private String rcNumber;
    @NotEmpty(message = "customerId")
    private String customerId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String createdOn;
    private List<AuthorizerLevelDTO> authorizers = new ArrayList<>();
    private List<CorporateUserDTO> corporateUsers = new ArrayList<>();
    private List<CorpTransferRuleDTO> corpTransferRules = new ArrayList<>();
    private Set<AccountDTO> accounts = new HashSet<>();
    private Set<String> cifids = new HashSet<>();


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

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
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

    public List<AuthorizerLevelDTO> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(List<AuthorizerLevelDTO> authorizers) {
        this.authorizers = authorizers;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<AccountDTO> accounts) {
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

    public Set<String> getCifids() {
        return cifids;
    }

    public void setCifids(Set<String> cifids) {
        this.cifids = cifids;
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


    @Override
    public String toString() {
        return "CorporateRequestDTO{" +
                "id=" + super.getId() +
                ", version=" + version +
                ", corporateType='" + corporateType + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", corporateId='" + corporateId + '\'' +
                ", bvn='" + bvn + '\'' +
                ", taxId='" + taxId + '\'' +
                ", status='" + status + '\'' +
                ", rcNumber='" + rcNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", authorizers=" + authorizers +
                ", corporateUsers=" + corporateUsers +
                ", corpTransferRules=" + corpTransferRules +
                ", accounts=" + accounts +
                ", cifids=" + cifids +
                '}';
    }

    @Override
    @JsonIgnore
    public JsonSerializer<CorporateRequestDTO> getSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(CorporateRequestDTO value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeStartObject();
                gen.writeStringField("Name", value.corporateName);
                gen.writeStringField("Type", value.corporateType);
                gen.writeStringField("CIFID(s)", value.cifids.toString());
                gen.writeStringField("Corporate ID", value.corporateId);
                gen.writeStringField("RC Number", value.rcNumber);


                gen.writeObjectFieldStart("Accounts");
                Integer count = 0;
                for (AccountDTO accountDTO : value.accounts) {
                    gen.writeObjectFieldStart((++count).toString());
                    gen.writeStringField("Account Number", accountDTO.getAccountNumber());
                    gen.writeStringField("Account Name", accountDTO.getAccountName());
                    gen.writeEndObject();
                }
                gen.writeEndObject();


                gen.writeObjectFieldStart("Users");
                count = 0;
                for (CorporateUserDTO user : value.corporateUsers) {

                    gen.writeObjectFieldStart((++count).toString());
                    gen.writeStringField("Username", user.getUserName());
                    gen.writeStringField("First Name", user.getFirstName());
                    gen.writeStringField("Last Name", user.getLastName());
                    gen.writeStringField("Email", user.getEmail());
                    gen.writeStringField("Phone Number", user.getPhoneNumber());

                    if ("MULTI".equals(value.getCorporateType())) {
                        gen.writeStringField("User Type", user.getUserType());
                        if ("AUTHORIZER".equals(user.getUserType())) {
                            gen.writeStringField("Authorizer Level", user.getAuthorizerLevel());

                        }

                        gen.writeStringField("Role", user.getRole());
                    }

                    if (!user.getAccountPermissions().isEmpty()) {
                        gen.writeObjectFieldStart("Account Permissions");

                        Integer accountNum = 0;
                        for (AccountPermissionDTO permission : user.getAccountPermissions()) {
                            gen.writeObjectFieldStart((++accountNum).toString());

                            gen.writeStringField("Account Number", permission.getAccountNumber());
                            gen.writeStringField("Account Name", permission.getAccountName());
                            gen.writeStringField("Account Permission", permission.getPermission().name());

                            gen.writeEndObject();
                        }
                        gen.writeEndObject();
                    }

                    gen.writeEndObject();
                }
                gen.writeEndObject();

                if ("MULTI".equals(value.getCorporateType())) {
                    gen.writeObjectFieldStart("Authorizer Levels");
                    count = 0;
                    for (AuthorizerLevelDTO authorizer : value.authorizers) {

                        gen.writeObjectFieldStart((++count).toString());
                        gen.writeStringField("Name", authorizer.getName());
                        gen.writeNumberField("Level", authorizer.getLevel());

                        gen.writeEndObject();
                    }
                    gen.writeEndObject();


                    gen.writeObjectFieldStart("Transaction Rules");
                    count = 0;
                    for (CorpTransferRuleDTO transferRule : value.corpTransferRules) {

                        gen.writeObjectFieldStart((++count).toString());

                        gen.writeStringField("Lower Amount", transferRule.getLowerLimitAmount());
                        if ("Unlimited".equalsIgnoreCase(transferRule.getUpperLimitAmount())) {
                            gen.writeStringField("Upper Amount", "UNLIMITED");
                        } else {
                            gen.writeStringField("Upper Amount", transferRule.getUpperLimitAmount());

                        }
                        gen.writeStringField("Currency", transferRule.getCurrency());

                        if (transferRule.isAnyCanAuthorize()) {
                            gen.writeStringField("Authorizers Required", "ANY");
                        } else {
                            gen.writeStringField("Authorizers Required", "ALL");

                        }

                        gen.writeObjectFieldStart("Authorizer Levels");
                        Integer countAuth = 0;
                        for (String authorizer : transferRule.getAuthorizers()) {
                            gen.writeObjectFieldStart((++countAuth).toString());
                            gen.writeStringField("Name", authorizer);
                            gen.writeEndObject();
                        }
                        gen.writeEndObject();
                        gen.writeEndObject();
                    }
                }
            }

        };
    }
@JsonIgnore
    @Override
    public <T> JsonSerializer<T> getAuditSerializer() {
        return null;
    }
}
