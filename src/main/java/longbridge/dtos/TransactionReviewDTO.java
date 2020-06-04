package longbridge.dtos;

import longbridge.utils.TransferType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class TransactionReviewDTO {

    private String startDate;
    private String endDate;
    @Enumerated(EnumType.ORDINAL)
    private TransferType TransactionType;
    private String CustomerAccountNumber;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public TransferType getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(TransferType transactionType) {
        TransactionType = transactionType;
    }

    public String getCustomerAccountNumber() {
        return CustomerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        CustomerAccountNumber = customerAccountNumber;
    }


}
