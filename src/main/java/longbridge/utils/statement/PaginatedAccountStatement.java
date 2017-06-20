package longbridge.utils.statement;


import java.util.List;

public class PaginatedAccountStatement {


    private List<TransactionDetails> transactionDetails;
    private AccountBalance acctBal;
    private String field125;
    private String field126;
    private String field127;
    private String hasMoreData;


    public List<TransactionDetails> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetails> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public AccountBalance getAcctBal() {
        return acctBal;
    }

    public void setAcctBal(AccountBalance acctBal) {
        this.acctBal = acctBal;
    }

    public String getField125() {
        return field125;
    }

    public void setField125(String field125) {
        this.field125 = field125;
    }

    public String getField126() {
        return field126;
    }

    public void setField126(String field126) {
        this.field126 = field126;
    }

    public String getField127() {
        return field127;
    }

    public void setField127(String field127) {
        this.field127 = field127;
    }

    public String getHasMoreData() {
        return hasMoreData;
    }

    public void setHasMoreData(String hasMoreData) {
        this.hasMoreData = hasMoreData;
    }
}
