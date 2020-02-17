package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Longbridge on 14/06/2017.
 */

public class CreditRequestDTO {
    @JsonProperty("DT_RowId")
    private Long id;
    private String customerAccountNumber;
    private String accountNumber;
    private String sortCode;
    private String accountName;
    private BigDecimal amount;
    private String beneficiaryBank;
    private String narration;
    private String status;
    private String referenceNumber;
    private Date tranDate;

    private String enquiredAccountName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getEnquiredAccountName() {
        return enquiredAccountName;
    }

    public void setEnquiredAccountName(String enquiredAccountName) {
        this.enquiredAccountName = enquiredAccountName;
    }

}
