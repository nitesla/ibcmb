
package longbridge.dtos;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class LocalBeneficiaryDTO implements Serializable{


	private Long id;
    @NotEmpty(message = "Please enter a Beneficiary Name")
    private String accountName;
    @NotEmpty(message = "Please enter an Account Number")
    private String accountNumber;
    @NotEmpty(message = "Please enter a Beneficiary Bank")
    private String beneficiaryBank;
    private String preferredName;
    private String token;
    private String lastname;
    private String firstname;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
