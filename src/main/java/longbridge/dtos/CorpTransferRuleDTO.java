package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

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
    private int numOfRoles;
    private String roleNames;
    private String corporateName;
    private boolean rank;
    private Set<CorporateRoleDTO> roles;

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


    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }


    public int getNumOfRoles() {
        return numOfRoles;
    }

    public void setNumOfRoles(int numOfRoles) {
        this.numOfRoles = numOfRoles;
    }

    public boolean isRank() {
        return rank;
    }

    public void setRank(boolean rank) {
        this.rank = rank;
    }

    public Set<CorporateRoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<CorporateRoleDTO> roles) {
        this.roles = roles;
    }
}
