package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 7/20/2017.
 */
public class CorporateRequestDTO implements PrettySerializer {

    @JsonProperty("DT_RowId")
    private Long id;
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
                "id=" + id +
                ", version=" + version +
                ", corporateType='" + corporateType + '\'' +
                ", corporateName='" + corporateName + '\'' +
                ", corporateId='" + corporateId + '\'' +
                ", bvn='" + bvn + '\'' +
                ", status='" + status + '\'' +
                ", rcNumber='" + rcNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", authorizers=" + authorizers +
                ", corporateUsers=" + corporateUsers +
                ", corpTransferRules=" + corpTransferRules +
                ", accounts=" + accounts +
                '}';
    }


    @Override
    @JsonIgnore
    public JsonSerializer<CorporateRequestDTO> getSerializer() {
        return new JsonSerializer<CorporateRequestDTO>() {
            @Override
            public void serialize(CorporateRequestDTO value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException {
                gen.writeStartObject();
                gen.writeStringField("Name", value.corporateName);
                gen.writeStringField("Type", value.corporateType);
                gen.writeStringField("CIF ID", value.customerId);
                gen.writeStringField("Corporate ID", value.corporateId);
                gen.writeStringField("RC Number", value.rcNumber);


                gen.writeObjectFieldStart("Accounts");
                Integer count = 0;
                for (AccountDTO accountDTO : value.accounts) {
                    gen.writeObjectFieldStart((++count).toString());
                    gen.writeStringField("Account Number", accountDTO.getAccountNumber());
                    gen.writeEndObject();
                }
                gen.writeEndObject();


                gen.writeObjectFieldStart("Users");
                count = 0;
                for (CorporateUserDTO user : value.corporateUsers) {

                    gen.writeObjectFieldStart((++count).toString());
                    //gen.writeStartObject();
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
