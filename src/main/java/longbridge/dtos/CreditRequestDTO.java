package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Longbridge on 14/06/2017.
 */
public class CreditRequestDTO {
    private String serial;
    private String refCode;
    private String accountNumber;
    private String sortCode;
    private String accountName;
    private String amount;
    private String narration;
    private String status;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getRefCode() {
        return refCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
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

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public CreditRequestDTO(String serial, String refCode, String accountNumber, String sortCode, String accountName, String amount, String narration) {
        this.serial = serial;
        this.refCode = refCode;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.accountName = accountName;
        this.amount = amount;
        this.narration = narration;
    }

    public CreditRequestDTO() {
    }
}
