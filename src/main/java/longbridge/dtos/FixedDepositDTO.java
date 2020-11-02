package longbridge.dtos;


/**
 * Created by Fortune on 2/26/2018.
 */

public class FixedDepositDTO {

    private String accountId;
    private String accountNumber;
    private String tenor;
    private String rate;
    private String bookingDate;
    private String maturityDate;
    private String initialDepositAmount;
    private String maturityAmount;
    private String depositType;
    private String bookRefNo;
    private String depositStatus;
    private String valueDate;
    private String liquidateType;
    private String status;
    private String comment;
    private String action;

    private Long serviceReqConfigId;
    private String requestName;
    private String recipientName;
    private String recipientEmail;


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getInitialDepositAmount() {
        return initialDepositAmount;
    }

    public void setInitialDepositAmount(String initialDepositAmount) {
        this.initialDepositAmount = initialDepositAmount;
    }

    public String getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(String maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getBookRefNo() {
        return bookRefNo;
    }

    public void setBookRefNo(String bookRefNo) {
        this.bookRefNo = bookRefNo;
    }

    public String getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(String depositStatus) {
        this.depositStatus = depositStatus;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getLiquidateType() {
        return liquidateType;
    }

    public void setLiquidateType(String liquidateType) {
        this.liquidateType = liquidateType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getServiceReqConfigId() {
        return serviceReqConfigId;
    }

    public void setServiceReqConfigId(Long serviceReqConfigId) {
        this.serviceReqConfigId = serviceReqConfigId;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    @Override
    public String toString() {
        return "FixedDepositDTO{" +
                "accountId='" + accountId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", tenor='" + tenor + '\'' +
                ", rate='" + rate + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", maturityDate='" + maturityDate + '\'' +
                ", initialDepositAmount='" + initialDepositAmount + '\'' +
                ", maturityAmount='" + maturityAmount + '\'' +
                ", depositType='" + depositType + '\'' +
                ", bookRefNo='" + bookRefNo + '\'' +
                ", depositStatus='" + depositStatus + '\'' +
                ", valueDate='" + valueDate + '\'' +
                ", liquidateType='" + liquidateType + '\'' +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", action='" + action + '\'' +
                ", serviceReqConfigId=" + serviceReqConfigId +
                ", requestName='" + requestName + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                '}';
    }
}
