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
    private double lowerLimitAmount;
    private double upperLimitAmount;
    private boolean anyOne;
    private String corporateId;
    private int numOfAuthorizers;
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

    public double getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(double lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public double getUpperLimitAmount() {
        return upperLimitAmount;
    }

    public void setUpperLimitAmount(double upperLimitAmount) {
        this.upperLimitAmount = upperLimitAmount;
    }

    public boolean isAnyOne() {
        return anyOne;
    }

    public void setAnyOne(boolean anyOne) {
        this.anyOne = anyOne;
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
