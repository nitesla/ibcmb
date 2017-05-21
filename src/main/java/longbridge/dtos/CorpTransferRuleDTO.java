package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fortune on 5/18/2017.
 */
public class CorpTransferRuleDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    @NotEmpty(message = "lowerLimitAmount")
    private BigDecimal lowerLimitAmount;
    @NotEmpty(message = "upperLimitAmount")
    private BigDecimal upperLimitAmount;
    @NotEmpty(message = "currency")
    private String currency;
    private boolean infinite;
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

    public BigDecimal getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(BigDecimal lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public BigDecimal getUpperLimitAmount() {
        return upperLimitAmount;
    }

    public void setUpperLimitAmount(BigDecimal upperLimitAmount) {
        this.upperLimitAmount = upperLimitAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
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
