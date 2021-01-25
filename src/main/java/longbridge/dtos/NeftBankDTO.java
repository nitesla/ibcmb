package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class NeftBankDTO {
    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty(message = "bank name")
    private String bankName;
    @NotEmpty(message = "branch name")
    private String branchName;
    @NotEmpty(message = "sort code")
    private String sortCode;
    private int version;

    public NeftBankDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "NeftBankDTO{" +
                "bankName='" + bankName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", sortCode='" + sortCode + '\'' +
                '}';
    }
}
