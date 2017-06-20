package longbridge.models;

/**
 * Created by Showboy on 15/06/2017.
 */
public class regcode extends Beneficiary {

    private String accountNo;
    private String email;

    private String regCode;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegCode() {
        return regCode;
    }

    public void setRegCode(String regCode) {
        this.regCode = regCode;
    }
}
