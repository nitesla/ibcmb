package longbridge.utils.statement;


/**
 * Created by ayoade_farooq@yahoo.com on 6/15/2017.
 */


public class TransactionDetails {

    private TransactionSummary transactionSummary;

    private String postedDate;

    private String transactionCategory;

    private String transactionId;

    private String valueDate;

    private TransactionBalance transactionBalance;

    private String transactionSerialNumber;

    public TransactionSummary getTransactionSummary() {
        return transactionSummary;
    }

    public void setTransactionSummary(TransactionSummary transactionSummary) {
        this.transactionSummary = transactionSummary;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public TransactionBalance getTransactionBalance() {
        return transactionBalance;
    }

    public void setTransactionBalance(TransactionBalance transactionBalance) {
        this.transactionBalance = transactionBalance;
    }

    public String getTransactionSerialNumber() {
        return transactionSerialNumber;
    }

    public void setTransactionSerialNumber(String transactionSerialNumber) {
        this.transactionSerialNumber = transactionSerialNumber;
    }
}


