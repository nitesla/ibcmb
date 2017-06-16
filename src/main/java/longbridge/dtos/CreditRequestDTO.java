package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Longbridge on 14/06/2017.
 */
public class CreditRequestDTO {
    private long dt_RowId;
    private String accountNumber;
    private String sortCode;
    private String accountName;
    private String amount;

    public CreditRequestDTO(String accountNumber, String sortCode, String accountName, String amount) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.accountName = accountName;
        this.amount = amount;
    }

    public CreditRequestDTO() {
    }

    public long getDt_RowId() {
        return dt_RowId;
    }

    @JsonSetter("DT_RowId")
    public void setDt_RowId(long dt_RowId) {
        this.dt_RowId = dt_RowId;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
