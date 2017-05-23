package longbridge.dtos;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
public class CorpLocalBeneficiaryDTO {
    private Long id;
    @NotEmpty
    private String accountName;
    @NotEmpty
    private String accountNumber;
    @NotEmpty
    private String beneficiaryBank;
    private String preferredName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
