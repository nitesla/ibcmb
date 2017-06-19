package longbridge.utils;

import longbridge.models.FinancialTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by chigozirim on 3/30/17.
 */
public class AccountStatement {

    private List<FinancialTransaction> transactionList;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private int debitCount;
    private int creditCount;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;

    public List<FinancialTransaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<FinancialTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public int getDebitCount() {
        return debitCount;
    }

    public void setDebitCount(int debitCount) {
        this.debitCount = debitCount;
    }

    public int getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(int creditCount) {
        this.creditCount = creditCount;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(BigDecimal totalDebit) {
        this.totalDebit = totalDebit;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }


    @Override
    public String toString() {
        return "AccountStatement{" +
                "transactionList=" + transactionList +
                ", openingBalance=" + openingBalance +
                ", closingBalance=" + closingBalance +
                ", debitCount=" + debitCount +
                ", creditCount=" + creditCount +
                ", totalDebit=" + totalDebit +
                ", totalCredit=" + totalCredit +
                '}';
    }
}
