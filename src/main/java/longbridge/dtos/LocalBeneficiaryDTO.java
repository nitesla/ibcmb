package longbridge.dtos;

import longbridge.models.Beneficiary;
import longbridge.models.RetailUser;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by Fortune on 4/5/2017.
 */

public class LocalBeneficiaryDTO{

    private RetailUser user;
    @NotNull
    private String name;
    @NotNull
    private String accountNo;
    @NotNull
    private String beneficiaryBank;

    public RetailUser getUser() {
        return user;
    }

    public void setUser(RetailUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }


    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }
}
