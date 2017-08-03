package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.models.Corporate;
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


    @Override @JsonIgnore
    public JsonSerializer<CorporateRequestDTO> getSerializer() {
        return new JsonSerializer<CorporateRequestDTO>() {
            @Override
            public void serialize(CorporateRequestDTO value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException, JsonProcessingException
            {
                gen.writeStartObject();
                gen.writeStringField("CIFID",value.customerId);
                gen.writeStringField("Corporate Type",value.corporateType);
                gen.writeStringField("Corporate Name",value.corporateName);
                gen.writeStringField("Unique Corporate ID",value.corporateId);

                gen.writeObjectFieldStart("Accounts");
                for(AccountDTO accountDTO : value.accounts) {
                    gen.writeStringField("Account Number", accountDTO.getAccountNumber());

                }
                gen.writeEndObject();


                gen.writeObjectFieldStart("Users");
                Integer count = 0;
                for(CorporateUserDTO user : value.corporateUsers){

                        gen.writeObjectFieldStart((++count).toString());
                        //gen.writeStartObject();
                        gen.writeStringField("Username", user.getUserName());
                        gen.writeStringField("First Name", user.getFirstName());
                        gen.writeStringField("Last Name", user.getLastName());
                        gen.writeStringField("Email", user.getEmail());
                        gen.writeStringField("Phone Number", user.getPhoneNumber());
                        gen.writeStringField("User Type", user.getUserType());
                        if("AUTHORIZER".equals(user.getUserType())){
                            gen.writeStringField("Authorizer Level", user.getAuthorizerLevel());

                        }
                        gen.writeStringField("Role", user.getRole());
                        gen.writeEndObject();
                }
                gen.writeEndObject();

                gen.writeObjectFieldStart("Authorizer Levels");
                count = 0;
                for(AuthorizerDTO authorizer : value.authorizers){

                    gen.writeObjectFieldStart((++count).toString());
                    gen.writeStringField("Name", authorizer.getName());
                    gen.writeNumberField("Level",authorizer.getLevel());

                    gen.writeEndObject();
                }
                gen.writeEndObject();


                gen.writeObjectFieldStart("Transaction Rules");
                count = 0;
                for(CorpTransferRuleDTO transferRule : value.corpTransferRules){

                    gen.writeObjectFieldStart((++count).toString());

                    gen.writeStringField("Lower Amount", transferRule.getLowerLimitAmount());
                    if("Unlimited".equals(transferRule.getUpperLimitAmount())){
                        gen.writeStringField("Upper Amount","Unlimited");
                    }
                    else {
                        gen.writeStringField("Upper Amount",transferRule.getUpperLimitAmount());

                    }
                    gen.writeStringField("Currency", transferRule.getCurrency());
                    gen.writeStringField("Authorizers Required", transferRule.getAny());

                    gen.writeObjectFieldStart("Authorizer Levels");
                    Integer countAuth = 0;
                    for(String authorizer : transferRule.getAuthorizers()){
                        gen.writeObjectFieldStart((++countAuth).toString());
                        gen.writeStringField("Name",authorizer);
                        gen.writeEndObject();
                    }
                    gen.writeEndObject();
                    gen.writeEndObject();
                }
                gen.writeEndObject();


                gen.writeEndObject();
            }
        };
    }

    private  String getStatusDescription(String status){
        String description =null;
        if ("A".equals(status))
            description = "Active";
        else if ("I".equals(status))
            description = "Inactive";
        else if ("L".equals(status))
            description = "Locked";
        return description;
    }
}
