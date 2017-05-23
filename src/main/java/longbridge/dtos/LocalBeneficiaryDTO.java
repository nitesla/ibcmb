
package longbridge.dtos;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 4/5/2017.
 */

public class LocalBeneficiaryDTO{

    private Long id;
    @NotEmpty(message = "Please enter a Beneficiary Name")
    private String accountName;
    @NotEmpty(message = "Please enter an Account Number")
    private String accountNumber;
    @NotEmpty(message = "Please enter a Beneficiary Bank")
    private String beneficiaryBank;
    private String preferredName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
}
