package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */
public class CorpTransferRuleDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String lowerLimitAmount;
    private String upperLimitAmount;
    private String currency;
    private boolean unlimited;
    private boolean anyCanAuthorize;
    private String corporateId;
    private int numOfAuthorizers;
    private String corporateName;
    private List<CorporateUserDTO> authorizers;

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

    public String getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(String lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public String getUpperLimitAmount() {
        return upperLimitAmount;
    }

    public void setUpperLimitAmount(String upperLimitAmount) {
        this.upperLimitAmount = upperLimitAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isUnlimited() {
        return unlimited;
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }

    public boolean isAnyCanAuthorize() {
        return anyCanAuthorize;
    }

    public void setAnyCanAuthorize(boolean anyCanAuthorize) {
        this.anyCanAuthorize = anyCanAuthorize;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public int getNumOfAuthorizers() {
        return numOfAuthorizers;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public void setNumOfAuthorizers(int numOfAuthorizers) {
        this.numOfAuthorizers = numOfAuthorizers;
    }

    public List<CorporateUserDTO> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(List<CorporateUserDTO> authorizers) {
        this.authorizers = authorizers;
    }
}
