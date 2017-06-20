package longbridge.utils.statement;



/**
 * Created by ayoade_farooq@yahoo.com on 6/15/2017.
 */


public class AccountBalance {


    private String accountNumber;
    private String branchId;
    private String currencyCode;

    private AvailableBalance availableBalance;

    private FFDBalance ffdBalance;

    private FloatingBalance floatBal;

    private LedgerBalance ledgerBalance;

    private UserDefinedBalance userDefinedBalance;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public AvailableBalance getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(AvailableBalance availableBalance) {
        this.availableBalance = availableBalance;
    }

    public FFDBalance getFfdBalance() {
        return ffdBalance;
    }

    public void setFfdBalance(FFDBalance ffdBalance) {
        this.ffdBalance = ffdBalance;
    }

    public FloatingBalance getFloatBal() {
        return floatBal;
    }

    public void setFloatBal(FloatingBalance floatBal) {
        this.floatBal = floatBal;
    }

    public LedgerBalance getLedgerBalance() {
        return ledgerBalance;
    }

    public void setLedgerBalance(LedgerBalance ledgerBalance) {
        this.ledgerBalance = ledgerBalance;
    }

    public UserDefinedBalance getUserDefinedBalance() {
        return userDefinedBalance;
    }

    public void setUserDefinedBalance(UserDefinedBalance userDefinedBalance) {
        this.userDefinedBalance = userDefinedBalance;
    }



}
